package com.example.demo;

import com.example.demo.service.interfaces.p12Service;
import com.example.demo.service.CertificateService;
import com.example.demo.utils.PdfSignerUtil;
import com.example.demo.service.fillCertificate;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

@SpringBootApplication
public class CertificateManagementApplication {

    public static void main(String[] args) throws Exception {
        // ✅ Start Spring Boot once and get the ApplicationContext
        ApplicationContext context = SpringApplication.run(CertificateManagementApplication.class, args);

        try {
            String keystorePath = "./keycert/STF001_1761756453751.p12";
            String alias = "STF001";
            // Use an existing student code from resources/data/students.json for certificate generation
            String studentCode = "STU001";
            char[] keystorePassword = "123456".toCharArray();
 
            // Load the PKCS12 keystore (optional for this test)
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(new FileInputStream(keystorePath), keystorePassword);

            // Generate PDF certificate (your custom class)
            fillCertificate fillCert = context.getBean(fillCertificate.class);
            String rawPdfPath = fillCert.generateCertificate(studentCode);
            System.out.println("✅ Generated PDF at: " + rawPdfPath);
            String hashOfFile = PdfSignerUtil.hashFile(rawPdfPath);
            System.out.println("✅ Hash of PDF file: " + hashOfFile);
            PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keystorePassword);
            X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);
             System.out.println("=== THÔNG TIN CHỨNG CHỈ ===");
    System.out.println("Subject: " + certificate.getSubjectX500Principal());
    System.out.println("Issuer: " + certificate.getIssuerX500Principal());
    System.out.println("Serial Number: " + certificate.getSerialNumber());
    System.out.println("Valid From: " + certificate.getNotBefore());
    System.out.println("Valid Until: " + certificate.getNotAfter());
    System.out.println("Signature Algorithm: " + certificate.getSigAlgName());
    System.out.println("Public Key Algorithm: " + certificate.getPublicKey().getAlgorithm());
    
            String signature = PdfSignerUtil.signDocumentBase64(hashOfFile, privateKey);
            System.out.println("✅ Generated signature: " + signature);
            String pathEmbeddedSignedPdf = PdfSignerUtil.embedSignatureInPdf(rawPdfPath, studentCode, signature, keystore.getCertificateChain(alias));
            System.out.println("✅ Signed PDF with embedded signature at: " + pathEmbeddedSignedPdf);

        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Uncomment to test P12 or CertificateService beans
        /*
        try {
            p12Service p12 = context.getBean(p12Service.class);
            System.out.println("Invoking p12Service.generateP12(\"STF001\")...");
            p12.generateP12("STF001");
        } catch (Exception ex) {
            System.err.println("❌ p12Service test failed: " + ex.getMessage());
        }

        try {
            CertificateService certService = context.getBean(CertificateService.class);
            Path created = certService.create("123", "123456");
            System.out.println("✅ Certificate created: " + created.toAbsolutePath());
        } catch (Exception ex) {
            System.err.println("❌ CertificateService test failed: " + ex.getMessage());
        }
        */
    }
}
