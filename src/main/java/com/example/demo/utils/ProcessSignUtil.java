package com.example.demo.utils;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;


import com.example.demo.service.fillCertificate;
import com.example.demo.config.AppContext;

public class ProcessSignUtil {
    public static String completeSign(String studentCode, String staffCode, String keyStorePath, String keyStorePassword, String alias) throws Exception {
       
    // obtain Spring-managed fillCertificate bean so its @Autowired studentRepository is initialized
    fillCertificate fillCert = AppContext.getBean(fillCertificate.class);
        KeyUtil keyUtil = new KeyUtil();
        char[] pwdArray = keyStorePassword.toCharArray();
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(new FileInputStream(keyStorePath), pwdArray);
        String rawPdfPath = fillCert.generateCertificate(studentCode);
        String hashOfPdf = PdfSignerUtil.hashFile(rawPdfPath);
        PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, pwdArray);
        X509Certificate certificate = (X509Certificate) keystore.getCertificate(alias);
        String signature = PdfSignerUtil.signDocumentBase64(hashOfPdf, privateKey);
        String pathEmbeddedSign = PdfSignerUtil.embedSignatureInPdf(rawPdfPath, studentCode, signature, keystore.getCertificateChain(alias));
        return pathEmbeddedSign;
    }
}
