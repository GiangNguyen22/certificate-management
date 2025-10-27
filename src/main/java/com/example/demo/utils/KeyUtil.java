package com.example.demo.utils;

import com.example.demo.entity.Student;
import com.example.demo.repository.CertificateRepository;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class KeyUtil {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Generate PKCS12 keystore (.p12) as Base64 string
     */
//    alias: ten dinh danh cho moi khoa/chung chi cua sinh vien
    public static String generatePKCS12Base64(String alias, char[] password, Student student) throws Exception {
        // 1. Generate RSA KeyPair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        // 2. Create self-signed X.509 certificate
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        Date endDate = new Date(now + 365L * 24 * 60 * 60 * 1000); // valid 1 year

        X500Name dnName = DnUtil.buildDnName(student);
        BigInteger certSerial = BigInteger.valueOf(now);

        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256withRSA")
                .build(keyPair.getPrivate());

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                dnName, certSerial, startDate, endDate, dnName, keyPair.getPublic());

        X509CertificateHolder certHolder = certBuilder.build(contentSigner);
        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider("BC").getCertificate(certHolder);

        // 3. Create PKCS12 keystore
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);
        pkcs12.setKeyEntry(alias, keyPair.getPrivate(), password, new Certificate[]{cert});

        // 4. Export keystore to byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pkcs12.store(baos, password);

        // 5. Encode Base64 for MongoDB
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }


    /**
     * Load private key from Base64 .p12
     */
    public  PrivateKey getPrivateKeyFromBase64(String base64P12, String alias, char[] password) throws Exception {
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);
        return (PrivateKey) pkcs12.getKey(alias, password);
    }

    public  PublicKey getPublicKeyFromBase64(String base64P12, String alias, char[] password) throws Exception {
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);
        return (PublicKey) pkcs12.getKey(alias, password);
    }

    /**
     * Load certificate from Base64 .p12
     */
    public  X509Certificate getCertificateFromBase64(String base64P12, String alias, char[] password) throws Exception {
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);
        return (X509Certificate) pkcs12.getCertificate(alias);
    }


}
