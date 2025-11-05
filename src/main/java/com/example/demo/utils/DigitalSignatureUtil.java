// package com.example.demo.utils;

// import com.example.demo.dto.VerifyResult;
// import com.example.demo.repository.UserPubKeysRepository;
// import com.itextpdf.kernel.pdf.PdfDocument;
// import com.itextpdf.kernel.pdf.PdfReader;
// import com.itextpdf.signatures.PdfPKCS7;
// import com.itextpdf.signatures.SignatureUtil;

// import java.security.PublicKey;
// import java.security.cert.Certificate;
// import java.security.cert.X509Certificate;
// import java.util.Base64;
// import java.util.List;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// public class DigitalSignatureUtil {

//     public static VerifyResult verifySignature(String filepath, UserPubKeysRepository userPKRepo) {
//         VerifyResult result = new VerifyResult();
        
//         try (PdfReader reader = new PdfReader(filepath);
//              PdfDocument pdfDoc = new PdfDocument(reader)) {
            
//             SignatureUtil signUtil = new SignatureUtil(pdfDoc);
//             List<String> signatureNames = signUtil.getSignatureNames();
            
//             if (signatureNames.isEmpty()) {
//                 result.setValid(false);
//                 result.setMessage("Không tìm thấy chữ ký số nào!");
//                 return result;
//             }
            
//             String sigName = signatureNames.get(0); // Lấy chữ ký đầu tiên

//             PdfPKCS7 pkcs7 = signUtil.readSignatureData(sigName);
//             Certificate[] certs = pkcs7.getSignCertificateChain();

//             // Thiết lập thông tin cơ bản
//             result.setSignerName(pkcs7.getSignName() != null ? pkcs7.getSignName() : "Unknown");
//             result.setSignDate(pkcs7.getSignDate() != null ? pkcs7.getSignDate().getTime() : null);
//             result.setReason(pkcs7.getReason() != null ? pkcs7.getReason() : "No reason specified");

//             // Kiểm tra chữ ký
//             boolean signatureValid = pkcs7.verifySignatureIntegrityAndAuthenticity();
//             result.setSignatureValid(signatureValid);

//             // Kiểm tra toàn vẹn tài liệu
//             boolean coversWholeDoc = signUtil.signatureCoversWholeDocument(sigName);
//             result.setCoversWholeDocument(coversWholeDoc);

//             // Lấy thông tin chứng chỉ
//             X509Certificate signCert = pkcs7.getSigningCertificate();
//             if (signCert != null) {
//                 result.setSubjectDN(signCert.getSubjectX500Principal().getName());
//                 result.setIssuerDN(signCert.getIssuerX500Principal().getName());
                
//                 // Kiểm tra public key
//                 Matcher matcher = Pattern.compile("UID=([^,]+)")
//                         .matcher(signCert.getSubjectX500Principal().getName());
//                 String staffcode = matcher.find() ? matcher.group(1) : null;
                
//                 if (staffcode != null && userPKRepo != null) {
//                     String pubkeyfromDB = userPKRepo.findPublicKeyByUserId(staffcode);
//                     if (pubkeyfromDB != null) {
//                         PublicKey publicKey = signCert.getPublicKey();
//                         String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
//                         result.setPublicKeyValid(publicKeyBase64.equals(pubkeyfromDB));
//                     } else {
//                         result.setPublicKeyValid(false);
//                     }
//                 } else {
//                     result.setPublicKeyValid(false);
//                 }
//             }

//             // Chuỗi chứng chỉ
//             result.setChainLength(certs != null ? certs.length : 0);

//             // Timestamp
//             if (pkcs7.getSignDate() != null) {
//                 result.setTimeStampValid(pkcs7.verifyTimestampImprint());
//             } else {
//                 result.setTimeStampValid(false);
//             }

//             // Tổng kết
//             result.setValid(signatureValid && coversWholeDoc);
//             result.setMessage("Xác minh hoàn tất");
            
//         } catch (Exception e) {
//             result.setValid(false);
//             result.setMessage("Lỗi xác minh: " + e.getMessage());
//             // Set default values để tránh null
//             result.setSignerName("N/A");
//             result.setReason("N/A");
//             result.setSubjectDN("N/A");
//             result.setIssuerDN("N/A");
//             result.setSignatureValid(false);
//             result.setCoversWholeDocument(false);
//             result.setChainLength(0);
//             result.setTimeStampValid(false);
//             result.setPublicKeyValid(false);
//         }
        
//         return result;
//     }
// }
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
      System.out.println("Success read");
      PdfDocument pdfDoc = new PdfDocument(reader);
      System.out.println("Success pdfDoc");
      SignatureUtil signUtil = new SignatureUtil(pdfDoc);
      System.out.println("Success signUtil");
      List<String> signatureNames = signUtil.getSignatureNames();
      if (signatureNames.isEmpty()) {
         result.setValid(false);
         result.setMessage("Không tìm thấy chữ ký số nào!");
         pdfDoc.close();
         return result;
      }

      String sigName = signatureNames.get(signatureNames.size() - 1);
System.out.println("Success sigName: " + sigName);
      PdfPKCS7 pkcs7 = signUtil.readSignatureData(sigName, "BC");
      System.out.println("Success pkcs7");
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
      String staffCode = matcher.find() ? matcher.group(1) : null;
      String pubkeyfromDB = userPKRepo.findPublicKeyByStaffCode(staffCode);
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