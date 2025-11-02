package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;



@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "User Management API",
        version = "1.0",
        description = "API for managing users with Swagger documentation"
    )
)
public class CertificateManagementApplication {

    public static void main(String[] args) throws Exception {
        // ✅ Start Spring Boot once and get the ApplicationContext
        //// ApplicationContext context = SpringApplication.run(CertificateManagementApplication.class, args);

        // Uncomment to test P12 or CertificateService beans
        /*
      

        try {
            CertificateService certService = context.getBean(CertificateService.class);
            Path created = certService.create("123", "123456");
            System.out.println("✅ Certificate created: " + created.toAbsolutePath());
        } catch (Exception ex) {
            System.err.println("❌ CertificateService test failed: " + ex.getMessage());
        }
        */
//     }

//     // Optional startup runner to generate and sign a certificate. Disabled by default.
//     @Bean
//     public CommandLineRunner generateCertificateOnStartup(ApplicationContext context,
//                                                           @Value("${app.generate-on-startup:false}") boolean generateOnStartup) {
//         return args -> {
//             if (!generateOnStartup) return;

//             try {
//                 String keystorePath = "./keycert/STF001_1761756453751.p12";
//                 String alias = "STF001";
//                 String studentCode = "STU001";
//                 char[] keystorePassword = "123456".toCharArray();

//                 KeyStore keystore = KeyStore.getInstance("PKCS12");
//                 keystore.load(new FileInputStream(keystorePath), keystorePassword);

//                 fillCertificate fillCert = context.getBean(fillCertificate.class);
//                 String rawPdfPath = fillCert.generateCertificate(studentCode);
//                 System.out.println("✅ Generated PDF at: " + rawPdfPath);
//                 String hashOfFile = PdfSignerUtil.hashFile(rawPdfPath);
//                 System.out.println("✅ Hash of PDF file: " + hashOfFile);
//                 PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, keystorePassword);
//                 X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);
//                 System.out.println("=== THÔNG TIN CHỨNG CHỈ ===");
//                 System.out.println("Subject: " + certificate.getSubjectX500Principal());
//                 System.out.println("Issuer: " + certificate.getIssuerX500Principal());
//                 System.out.println("Serial Number: " + certificate.getSerialNumber());
//                 System.out.println("Valid From: " + certificate.getNotBefore());
//                 System.out.println("Valid Until: " + certificate.getNotAfter());
//                 System.out.println("Signature Algorithm: " + certificate.getSigAlgName());
//                 System.out.println("Public Key Algorithm: " + certificate.getPublicKey().getAlgorithm());

//                 String signature = PdfSignerUtil.signDocumentBase64(hashOfFile, privateKey);
//                 System.out.println("✅ Generated signature: " + signature);
//                 String pathEmbeddedSignedPdf = PdfSignerUtil.embedSignatureInPdf(rawPdfPath, studentCode, signature, keystore.getCertificateChain(alias));
//                 System.out.println("✅ Signed PDF with embedded signature at: " + pathEmbeddedSignedPdf);

//             } catch (Exception e) {
//                 System.out.println("❌ Error: " + e.getMessage());
//                 e.printStackTrace();
//             }
//         };
//     }
// }    
// ApplicationContext context = SpringApplication.run(CertificateManagementApplication.class, args);

//           try {
//             p12Service p12 = context.getBean(p12Service.class);
//             System.out.println("Invoking p12Service.generateP12(\"STF001\")...");
//             p12.generateP12("STF001");
//         } catch (Exception ex) {
//             System.err.println("❌ p12Service test failed: " + ex.getMessage());
//         }
        SpringApplication.run(CertificateManagementApplication.class, args);
    }
}