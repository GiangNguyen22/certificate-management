package com.example.demo.utils;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.*;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.util.Base64;

public class PdfSignerUtil {
        public static String HashFile(String filePath) throws Exception {
            InputStream fis = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            fis.close();
            byte[] hashBytes = digest.digest();
            return Base64.getEncoder().encodeToString(hashBytes);
        }
        public static String signDocumentBase64(String hashfile, PrivateKey privateKEy) throws Exception {
            byte[] hashBytes = Base64.getDecoder().decode(hashfile);
            //Thuc hien ky so
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKEy);
            signature.update(hashBytes);
                byte[] digitalSignature = signature.sign();
                return Base64.getEncoder().encodeToString(digitalSignature);
        }
            public static boolean verifySignature(String hashBase64, String signatureBase64, PublicKey publicKey) throws Exception {
        byte[] hashBytes = Base64.getDecoder().decode(hashBase64);
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
        
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(hashBytes);
        return signature.verify(signatureBytes);
    }
     public static String hashAndSignFile(String filePath, PrivateKey privateKey) throws Exception {
        String hashBase64 = HashFile(filePath);
        return signDocumentBase64(hashBase64, privateKey);
    }
    
}
