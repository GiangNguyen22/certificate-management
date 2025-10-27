package com.example.demo.utils;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Base64;

public class PdfSignerUtil {

    /**
     * Ký số file PDF bằng private key & certificate trong file .p12 (Base64)
     *
     * @param srcPdfPath    PDF đầu vào (tạo từ PdfGenerator)
     * @param destPdfPath   PDF đầu ra (đã ký số)
     * @param base64P12     nội dung .p12 dạng Base64 (MongoDB lưu)
     * @param alias         alias trong keystore (vd: "student123")
     * @param password      password bảo vệ .p12
     */
    public static void signPdf(String srcPdfPath,
                               String destPdfPath,
                               String base64P12,
                               String alias,
                               char[] password) throws Exception {

        // 1. Giải mã Base64 -> byte[]
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);

        // 2. Load keystore từ byte[]
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);

        // 3. Lấy PrivateKey & Certificate chain
        PrivateKey privateKey = (PrivateKey) pkcs12.getKey(alias, password);
        Certificate[] chain = pkcs12.getCertificateChain(alias);

        if (privateKey == null) {
            throw new IllegalArgumentException("Không tìm thấy private key với alias: " + alias);
        }

        // 4. Chuẩn bị reader & signer
        PdfReader reader = new PdfReader(srcPdfPath);
        PdfSigner signer = new PdfSigner(reader,
                new FileOutputStream(destPdfPath),
                new StampingProperties());

        // 5. Cấu hình vị trí hiển thị chữ ký (visible signature)
        Rectangle rect = new Rectangle(50, 50, 200, 100); // x, y, w, h
        PdfSignatureAppearance appearance = signer.getSignatureAppearance()
                .setReason("Student Certificate Digital Signature")
                .setLocation("MySchool")
                .setPageRect(rect)
                .setPageNumber(1)
                .setReuseAppearance(false);

        signer.setFieldName("sig_field");

        // 6. Tạo signature
        IExternalSignature pks = new PrivateKeySignature(privateKey, "SHA256", "BC");
        IExternalDigest digest = new BouncyCastleDigest();

        // 7. Ký số
        signer.signDetached(digest, pks, chain, null, null, null, 0,
                PdfSigner.CryptoStandard.CADES);
    }
}
