package com.example.demo.utils;

import com.example.demo.dto.VerifyResult;
import com.example.demo.repository.UserPubKeysRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;

import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;

import java.security.PublicKey;

import java.security.cert.X509Certificate;
import java.security.cert.Certificate;
import java.util.List;
import java.util.regex.Matcher;


public class DigitalSignatureUtil {

   // public static String extractPublicKey(String filepath) throws Exception {
   // PdfReader reader = new PdfReader(filepath);
   // PdfDocument pdfDoc = new PdfDocument(reader);
   // SignatureUtil signUtil = new SignatureUtil(pdfDoc);
   // List<String> signatureNames = signUtil.getSignatureNames();
   // String sigName = signatureNames.get(signatureNames.size() - 1);
   // Security.addProvider(new BouncyCastleProvider());
   // PdfPKCS7 pkcs7 = signUtil.readSignatureData(sigName,"BC");
   // Certificate[] certs = pkcs7.getSignCertificateChain();
   // if (certs.length == 0) {
   // throw new RuntimeException("Không tìm thấy chứng chỉ trong chữ ký!");
   // }

   // X509Certificate signCert = (X509Certificate) certs[0];
   // PublicKey publicKey = signCert.getPublicKey();

   // // In ra thông tin
   // System.out.println("Chủ sở hữu: " + signCert.getSubjectX500Principal());
   // System.out.println("Nhà cung cấp: " + signCert.getIssuerX500Principal());
   // System.out.println("Thuật toán: " + publicKey.getAlgorithm());
   // String publickeybase64 =
   // java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
   // System.out.println("Public Key (Base64): " + publickeybase64);

   // // Ghi public key ra file PEM

   // pdfDoc.close();
   // reader.close();
   // return publickeybase64;

   // }
   public static VerifyResult verifySignature(String filepath, UserPubKeysRepository userPKRepo) throws Exception {
      VerifyResult result = new VerifyResult();
      PdfReader reader = new PdfReader(filepath);
      PdfDocument pdfDoc = new PdfDocument(reader);
      SignatureUtil signUtil = new SignatureUtil(pdfDoc);
      List<String> signatureNames = signUtil.getSignatureNames();
      if (signatureNames.isEmpty()) {
         result.setValid(false);
         result.setMessage("Không tìm thấy chữ ký số nào!");
         pdfDoc.close();
         return result;
      }
      String sigName = signatureNames.get(signatureNames.size() - 1);

      PdfPKCS7 pkcs7 = signUtil.readSignatureData(sigName, "BC");
      Certificate[] certs = pkcs7.getSignCertificateChain();

      result.setSignerName(pkcs7.getSignName());
      result.setSignDate(pkcs7.getSignDate() != null ? pkcs7.getSignDate().getTime() : null);
      result.setReason(pkcs7.getReason());

      // 1. Kiểm tra chữ ký có hợp lệ không?
      boolean signatureValid = pkcs7.verifySignatureIntegrityAndAuthenticity();
      result.setSignatureValid(signatureValid);

      // 2. Kiểm tra PDF có bị sửa không?
      boolean coversWholeDoc = signUtil.signatureCoversWholeDocument(sigName);
      result.setCoversWholeDocument(coversWholeDoc);

      // 3. Lấy chứng chỉ người ký
      X509Certificate signCert = pkcs7.getSigningCertificate();
      result.setSubjectDN(signCert.getSubjectX500Principal().toString());

      result.setIssuerDN(signCert.getIssuerX500Principal().toString());

      // 4. Kiểm tra chuỗi chứng chỉ (CA)
      Certificate[] chain = pkcs7.getSignCertificateChain();
      result.setChainLength(chain.length);

      // 5. Kiểm tra thời gian ký
      if (pkcs7.getSignDate() != null) {
         result.setTimeStampValid(pkcs7.verifyTimestampImprint());
      }
      // 6. Kiểm tra public key
      Matcher matcher = java.util.regex.Pattern.compile("UID=([^,]+)")
            .matcher(signCert.getSubjectX500Principal().toString());
      String staffcode = matcher.find() ? matcher.group(1) : null;
      String pubkeyfromDB = userPKRepo.findPublicKeyByUserId(staffcode);
      X509Certificate signCertificate = (X509Certificate) certs[0];
      PublicKey publicKey = signCertificate.getPublicKey();
      String publicKeyBase64 = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
      boolean publicKeyValid = publicKeyBase64.equals(pubkeyfromDB);
      if (publicKeyValid) {
         result.setPublicKeyValid(true);
      } else {
         result.setPublicKeyValid(false);
      }

      // Tổng kết
      result.setValid(signatureValid && coversWholeDoc);

      pdfDoc.close();
      reader.close();
      return result;

   }
}