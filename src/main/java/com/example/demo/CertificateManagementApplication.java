package com.example.demo;

import com.example.demo.service.interfaces.p12Service;
import com.example.demo.service.CertificateService;
import com.example.demo.utils.KeyUtil;
import java.security.cert.X509Certificate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.PrivateKey;

@SpringBootApplication
public class CertificateManagementApplication {

    public static void main(String[] args) throws Exception {

        // ✅ Start Spring Boot once and get the ApplicationContext
        ApplicationContext context = SpringApplication.run(CertificateManagementApplication.class, args);
		try{
			
			String p12path = "./keycert/STF001_1761756453751.p12";
			String alias = "STF001";
			char[] password = "123456".toCharArray();
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			keystore.load(new FileInputStream(p12path), password);

			X509Certificate cert = (X509Certificate) keystore.getCertificate(alias);
		System.out.println("Public Key: " + cert.getPublicKey());
System.out.println("Subject: " + cert.getSubjectX500Principal());
System.out.println("Issuer: " + cert.getIssuerX500Principal());
PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, password);
System.out.println("Private Key: " + privateKey);
		}
		catch(Exception e){
			System.out.println("Error: " + e.getMessage());
		}
        // --- Smoke test 1: Generate .p12 file for staff id = 1 ---
        // try {
        //     p12Service p12 = context.getBean(p12Service.class);
        //     System.out.println("Invoking p12Service.generateP12(\"STF001\") as a quick smoke-test...");
        //     p12.generateP12("STF001");
        //     System.out.println("✅ Smoke-test finished. Check ./keycert/ for created .p12 files (if staff with id=1 exists).\n");
        // } catch (Exception ex) {
        //     System.err.println("❌ Smoke-test p12 generation failed: " + ex.getMessage());
        // }

        // --- Smoke test 2: Create certificate for staff id = 1 ---
        // try {
        //     CertificateService certService = context.getBean(CertificateService.class);
        //     System.out.println("Invoking CertificateService.create(\"123\", \"123456\") ...");

        //     Path created = certService.create("123", "123456");
        //     System.out.println("✅ CertificateService created file: " + created.toAbsolutePath());
        // } catch (Exception ex) {
        //     System.err.println("❌ CertificateService smoke-test failed: " + ex.getMessage());
        // }

        // You can add other tests here if needed, but avoid re-running SpringApplication.run()
    }
}
