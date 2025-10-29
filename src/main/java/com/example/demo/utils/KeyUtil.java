
package com.example.demo.utils;

import com.example.demo.entity.Student;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import com.example.demo.entity.Staff;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;

public class KeyUtil {
    private String privateKey;
    private String publicKey;
    private String createAt;
    private String CryptoType; 
    public KeyUtil(){

    }
    public String getPrivateKey(){
        return this.privateKey;
    }
    public String getPublicKey(){
        return this.publicKey;  
    }
    public String getCreateAt(){
        return this.createAt;
    }
    public String getCryptoType(){
        return this.CryptoType;
    }
    public void setPrivateKey(String privateKey){
        this.privateKey = privateKey;
    }
    public void setPublicKey(String publicKey){
        this.publicKey = publicKey;
    }
    public void setCreateAt(String createAt){
        this.createAt = createAt;
    }
    public void setCryptoType(String CryptoType){
        this.CryptoType = CryptoType;
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public enum KeyAlgo { EC, RSA }

    public static class CertOptions {
        public KeyAlgo keyAlgo = KeyAlgo.EC;
        public String ecCurve = "secp256r1"; // for EC
        public int rsaKeySize = 2048; // for RSA
        public String sigAlgEc = "SHA256withECDSA";
        public String sigAlgRsa = "SHA256withRSA";
        public int validityDays = 365;
        public String subjectDn; // if null, DnUtil.buildDnName(student) used
        public BigInteger serialNumber; // if null uses currentTime millis
        public PrivateKey issuerPrivateKey; // optional: sign by issuer (create chain)
        public X509Certificate issuerCert;  // optional: issuer cert for chain
    }

    /**
     * Generate PKCS12 keystore (.p12) as Base64 string using flexible options.
     * If options.issuerPrivateKey/options.issuerCert provided, certificate will be signed by issuer (chain).
     */
    public String generatePKCS12Base64(String alias, char[] password, Staff staff, CertOptions options) throws Exception {
        Objects.requireNonNull(alias, "alias required");
        Objects.requireNonNull(password, "password required");

        // 1. generate keypair
        KeyPair keyPair;
        if (options == null) options = new CertOptions();
        if (options.keyAlgo == KeyAlgo.EC) {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
            keyGen.initialize(new ECGenParameterSpec(options.ecCurve));
            keyPair = keyGen.generateKeyPair();
            this.setCryptoType("EC");
            this.setCreateAt(new Date().toString());
            this.setPrivateKey(keyPair.getPrivate().toString());
            this.setPublicKey(keyPair.getPublic().toString());
        } else {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
            keyGen.initialize(options.rsaKeySize);
            keyPair = keyGen.generateKeyPair();
            this.setCryptoType("RSA");
            this.setCreateAt(new Date().toString());
            this.setPrivateKey(keyPair.getPrivate().toString());
            this.setPublicKey(keyPair.getPublic().toString());
        
        }

        // 2. build subject DN
        X500Name subject;
        if (options.subjectDn != null && !options.subjectDn.isBlank()) {
            subject = new X500Name(options.subjectDn);
        } else {
            subject = DnUtil.buildDnName(staff);
        }

        // 3. validity & serial
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        Date endDate = new Date(now + (long) Math.max(1, options.validityDays) * 24 * 60 * 60 * 1000);
        BigInteger serial = options.serialNumber != null ? options.serialNumber : BigInteger.valueOf(now);

        // 4. prepare signer: if issuer provided use issuer private key and issuer DN; otherwise self-sign
        String sigAlg = (options.keyAlgo == KeyAlgo.RSA) ? options.sigAlgRsa : options.sigAlgEc;
        ContentSigner contentSigner;
        X500Name issuerName;
        if (options.issuerPrivateKey != null && options.issuerCert != null) {
            contentSigner = new JcaContentSignerBuilder(sigAlg).setProvider("BC").build(options.issuerPrivateKey);
            issuerName = new X500Name(options.issuerCert.getSubjectX500Principal().getName());
        } else {
            contentSigner = new JcaContentSignerBuilder(sigAlg).setProvider("BC").build(keyPair.getPrivate());
            issuerName = subject; // self-signed
        }

        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuerName, serial, startDate, endDate, subject, keyPair.getPublic()
        );

        X509CertificateHolder certHolder = certBuilder.build(contentSigner);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

        // 5. create keystore and chain: if issuerCert provided, chain = {cert, issuerCert}
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);
        if (options.issuerCert != null) {
            pkcs12.setKeyEntry(alias, keyPair.getPrivate(), password, new Certificate[]{cert, options.issuerCert});
        } else {
            pkcs12.setKeyEntry(alias, keyPair.getPrivate(), password, new Certificate[]{cert});
        }

        // 6. export to Base64 with try-with-resources and clear password array
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            pkcs12.store(baos, password);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            // zero password for security
            for (int i = 0; i < password.length; i++) password[i] = 0;
            return base64;
        }
    }

    /**
     * Overload for backward compatibility: original simple generate (EC, 1 year, self-signed)
     */
    public String generatePKCS12Base64(String alias, char[] password, Staff staff) throws Exception {
        return generatePKCS12Base64(alias, password, staff, null);
    }

    /**
     * Load PrivateKey from Base64-encoded .p12 with alias fallback (first alias if provided alias missing).
     */
    public static PrivateKey getPrivateKeyFromBase64(String base64P12, String alias, char[] password) throws Exception {
        Objects.requireNonNull(base64P12, "base64P12 required");
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);

        String useAlias = alias;
        if (useAlias == null || pkcs12.getKey(useAlias, password) == null) {
            Enumeration<String> aliases = pkcs12.aliases();
            useAlias = aliases.hasMoreElements() ? aliases.nextElement() : null;
        }
        if (useAlias == null) throw new IllegalArgumentException("No alias found in keystore");
        return (PrivateKey) pkcs12.getKey(useAlias, password);
    }

    /**
     * Load PublicKey from Base64-encoded .p12 with alias fallback.
     */
    public static PublicKey getPublicKeyFromBase64(String base64P12, String alias, char[] password) throws Exception {
        Objects.requireNonNull(base64P12, "base64P12 required");
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);

        String useAlias = alias;
        Certificate cert = null;
        if (useAlias != null) cert = pkcs12.getCertificate(useAlias);

        if (cert == null) {
            Enumeration<String> aliases = pkcs12.aliases();
            String first = aliases.hasMoreElements() ? aliases.nextElement() : null;
            if (first != null) cert = pkcs12.getCertificate(first);
        }
        if (cert == null) throw new IllegalArgumentException("No certificate found in keystore");
        return cert.getPublicKey();
    }

    /**
     * Load X.509 Certificate from Base64-encoded .p12 with alias fallback.
     */
    public static X509Certificate getCertificateFromBase64(String base64P12, String alias, char[] password) throws Exception {
        Objects.requireNonNull(base64P12, "base64P12 required");
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);

        Certificate cert = null;
        if (alias != null) cert = pkcs12.getCertificate(alias);
        if (cert == null) {
            Enumeration<String> aliases = pkcs12.aliases();
            String first = aliases.hasMoreElements() ? aliases.nextElement() : null;
            if (first != null) cert = pkcs12.getCertificate(first);
        }
        if (cert == null) throw new IllegalArgumentException("No certificate found in keystore");
        return (X509Certificate) cert;
    }
}