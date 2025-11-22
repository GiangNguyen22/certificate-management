package com.example.demo.utils;

import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.util.Base64;

public class PdfSignerUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Tạo hash SHA-256 cho file PDF.
     */
    public static String hashFile(String filePath) throws Exception {
        try (InputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            byte[] hashBytes = digest.digest();
            return Base64.getEncoder().encodeToString(hashBytes);
        }
    }

    /**
     * Ký một hash đã có (ở dạng base64) bằng private key.
     */
    public static String signDocumentBase64(String hashBase64, PrivateKey privateKey) throws Exception {
        byte[] hashBytes = Base64.getDecoder().decode(hashBase64);
        Signature signature = Signature.getInstance("NONEwithRSA");
        signature.initSign(privateKey);
        signature.update(hashBytes);
        byte[] digitalSignature = signature.sign();
        return Base64.getEncoder().encodeToString(digitalSignature);
    }

    /**
     * Xác minh chữ ký với public key.
     */
    public static boolean verifySignature(String hashBase64, String signatureBase64, PublicKey publicKey)
            throws Exception {
        byte[] hashBytes = Base64.getDecoder().decode(hashBase64);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(hashBytes);
        return signature.verify(signatureBytes);
    }

    /**
     * Hash + ký trực tiếp file PDF, trả về chữ ký base64.
     */
    public static String hashAndSignFile(String filePath, PrivateKey privateKey) throws Exception {
        String hashBase64 = hashFile(filePath);
        return signDocumentBase64(hashBase64, privateKey);
    }

    static String signInternalSignatureInPdf(String srcPdfPath, String courseCode, String studentCode,
            PrivateKey privateKey, Certificate[] chain) throws Exception {
        PdfReader reader = new PdfReader(srcPdfPath);
        File outDir = new File("certificates_signed");
        if (!outDir.exists())
            outDir.mkdirs();
        String outputPath = outDir.getAbsolutePath() + "/" + studentCode +"_"+ courseCode + "_certificate.pdf";
        FileOutputStream os = new FileOutputStream(outputPath);
        PdfSigner signer = new PdfSigner(reader, os, new StampingProperties().useAppendMode());
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason("Certificate Issuance")
                .setLocation("KMA University")
                .setReuseAppearance(false);
        signer.setFieldName("Director_Signature");
        IExternalDigest digest = new BouncyCastleDigest();
        IExternalSignature signature = new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256, "BC");
        signer.signDetached(
                digest,
                signature,
                chain,
                null, null, null,
                0,
                PdfSigner.CryptoStandard.CMS);
        os.close();
            return outputPath;
    }

    /**
     * Lấy chuỗi chứng chỉ từ keystore (.p12 hoặc .pfx)
     */
    public static Certificate[] getCertificateChainFromKeystore(String keystorePath,
            String keystorePassword,
            String alias) throws Exception {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        try (InputStream is = new FileInputStream(keystorePath)) {
            keystore.load(is, keystorePassword.toCharArray());
            return keystore.getCertificateChain(alias);
        }
    }

    /**
     * Lấy private key từ keystore (.p12 hoặc .pfx)
     */
    public static PrivateKey getPrivateKeyFromKeystore(String keystorePath,
            String keystorePassword,
            String alias,
            String keyPassword) throws Exception {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        try (InputStream is = new FileInputStream(keystorePath)) {
            keystore.load(is, keystorePassword.toCharArray());
            return (PrivateKey) keystore.getKey(alias, keyPassword.toCharArray());
        }
    }

}