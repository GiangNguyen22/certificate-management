package com.example.demo.utils;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.example.demo.service.fillCertificate;
import com.example.demo.service.interfaces.CertService;

import ch.qos.logback.core.testUtil.RandomUtil;

import com.example.demo.config.AppContext;

public class ProcessSignUtil {

    

    public static String completeSign(String studentCode, String staffCode, String keyStorePath, String keyStorePassword, String alias) throws Exception {
       
    // obtain Spring-managed fillCertificate bean so its @Autowired studentRepository is initialized
    fillCertificate fillCert = AppContext.getBean(fillCertificate.class);
    CertService certService = AppContext.getBean(CertService.class);
        KeyUtil keyUtil = new KeyUtil();
        char[] pwdArray = keyStorePassword.toCharArray();
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(new FileInputStream(keyStorePath), pwdArray);
        String rawPdfPath = fillCert.generateCertificate(studentCode);
        String hashOfPdf = PdfSignerUtil.hashFile(rawPdfPath);
        PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, pwdArray);
        X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);
        String signature = PdfSignerUtil.signDocumentBase64(hashOfPdf, privateKey);
        System.out.println("✅ Document signed successfully. Signature (Base64): " + signature);
        String pathDocSigned = PdfSignerUtil.signInternalSignatureInPdf(rawPdfPath, studentCode, privateKey, keystore.getCertificateChain(alias));
        // String pathDocSigned = PdfSignerUtil.embedSignatureInPdf(rawPdfPath, studentCode, signature, keystore.getCertificateChain(alias));
       String certId = UUID.randomUUID().toString() + "-" + studentCode;
       String templateId = "template-001"; // Example template ID
         String studentId = studentCode; // Assuming studentCode is used as studentId
         String issuedAt = new SimpleDateFormat("yyyy-MM-dd").format(new Date()); 
         String expireAt = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L)); // 1 year later
         String status = "ISSUED";
        String serialNo = certificate.getSerialNumber().toString();

        certService.saveCertificateRecord(certId, templateId, studentId,staffCode, issuedAt, expireAt, status, serialNo, pathDocSigned, hashOfPdf);
        return pathDocSigned;
    }
}
