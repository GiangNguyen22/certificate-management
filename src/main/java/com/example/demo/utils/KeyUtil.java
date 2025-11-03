package com.example.demo.utils;

import com.example.demo.entity.Staff;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String createAt;
    private String cryptoType;

    public KeyUtil() {}

    public PrivateKey getPrivateKey() { return privateKey; }
    public PublicKey getPublicKey() { return publicKey; }
    public String getCreateAt() { return createAt; }
    public String getCryptoType() { return cryptoType; }

    public void setPrivateKey(PrivateKey privateKey) { this.privateKey = privateKey; }
    public void setPublicKey(PublicKey publicKey) { this.publicKey = publicKey; }
    public void setCreateAt(String createAt) { this.createAt = createAt; }
    public void setCryptoType(String cryptoType) { this.cryptoType = cryptoType; }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public enum KeyAlgo { RSA, EC }

    public static class CertOptions {
        public KeyAlgo keyAlgo = KeyAlgo.RSA;        // ✅ mặc định RSA
        public String ecCurve = "secp256r1";         // cho EC
        public int rsaKeySize = 2048;                // cho RSA
        public String sigAlgRsa = "SHA256withRSA";
        public String sigAlgEc = "SHA256withECDSA";
        public int validityDays = 365;
        public String subjectDn;
        public BigInteger serialNumber;
        public PrivateKey issuerPrivateKey;
        public X509Certificate issuerCert;
    }

    /**
     * Sinh keystore PKCS12 (.p12) và xuất base64, có thể chọn EC hoặc RSA (mặc định RSA)
     */
    public String generatePKCS12Base64(String alias, char[] password, Staff staff, CertOptions options) throws Exception {
        Objects.requireNonNull(alias, "alias required");
        Objects.requireNonNull(password, "password required");

        if (options == null) options = new CertOptions();

        // 1️⃣ Tạo KeyPair theo thuật toán được chọn
        KeyPair keyPair;
        if (options.keyAlgo == KeyAlgo.EC) {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
            keyGen.initialize(new ECGenParameterSpec(options.ecCurve));
            keyPair = keyGen.generateKeyPair();
            this.setCryptoType("EC");
        } else {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
            keyGen.initialize(options.rsaKeySize);
            keyPair = keyGen.generateKeyPair();
            this.setCryptoType("RSA");
            this.setPrivateKey(keyPair.getPrivate());
            this.setPublicKey(keyPair.getPublic());
        }

        this.setCreateAt(new Date().toString());
      
        // 2️⃣ Xây dựng Subject DN
        X500Name subject;
        if (options.subjectDn != null && !options.subjectDn.isBlank()) {
            subject = new X500Name(options.subjectDn);
        } else {
            subject = DnUtil.buildDnName(staff);
        }

        // 3️⃣ Thời hạn & serial
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        Date endDate = new Date(now + (long) options.validityDays * 24 * 60 * 60 * 1000);
        BigInteger serial = options.serialNumber != null ? options.serialNumber : BigInteger.valueOf(now);

        // 4️⃣ Tạo signer
        String sigAlg = (options.keyAlgo == KeyAlgo.EC) ? options.sigAlgEc : options.sigAlgRsa;
        ContentSigner contentSigner;
        X500Name issuerName;

        if (options.issuerPrivateKey != null && options.issuerCert != null) {
            contentSigner = new JcaContentSignerBuilder(sigAlg).setProvider("BC").build(options.issuerPrivateKey);
            issuerName = new X500Name(options.issuerCert.getSubjectX500Principal().getName());
        } else {
            contentSigner = new JcaContentSignerBuilder(sigAlg).setProvider("BC").build(keyPair.getPrivate());
            issuerName = subject;
        }

        // 5️⃣ Xây dựng chứng chỉ X.509
        JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuerName, serial, startDate, endDate, subject, keyPair.getPublic()
        );
        X509CertificateHolder certHolder = certBuilder.build(contentSigner);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);

        // 6️⃣ Tạo keystore PKCS12
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(null, null);

        if (options.issuerCert != null) {
            pkcs12.setKeyEntry(alias, keyPair.getPrivate(), password, new Certificate[]{cert, options.issuerCert});
        } else {
            pkcs12.setKeyEntry(alias, keyPair.getPrivate(), password, new Certificate[]{cert});
        }

        // 7️⃣ Xuất keystore Base64
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            pkcs12.store(baos, password);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            for (int i = 0; i < password.length; i++) password[i] = 0; // clear password
            return base64;
        }
    }

    public String generatePKCS12Base64(String alias, char[] password, Staff staff) throws Exception {
        return generatePKCS12Base64(alias, password, staff, null);
    }

    // 🔒 Các hàm load key/cert giữ nguyên
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

    public static PublicKey getPublicKeyFromBase64(String base64P12, String alias, char[] password) throws Exception {
        Objects.requireNonNull(base64P12, "base64P12 required");
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);

        Certificate cert = alias != null ? pkcs12.getCertificate(alias) : null;
        if (cert == null) {
            Enumeration<String> aliases = pkcs12.aliases();
            String first = aliases.hasMoreElements() ? aliases.nextElement() : null;
            if (first != null) cert = pkcs12.getCertificate(first);
        }
        if (cert == null) throw new IllegalArgumentException("No certificate found in keystore");
        return cert.getPublicKey();
    }

    public static X509Certificate getCertificateFromBase64(String base64P12, String alias, char[] password) throws Exception {
        Objects.requireNonNull(base64P12, "base64P12 required");
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);

        Certificate cert = alias != null ? pkcs12.getCertificate(alias) : null;
        if (cert == null) {
            Enumeration<String> aliases = pkcs12.aliases();
            String first = aliases.hasMoreElements() ? aliases.nextElement() : null;
            if (first != null) cert = pkcs12.getCertificate(first);
        }
        if (cert == null) throw new IllegalArgumentException("No certificate found in keystore");
        return (X509Certificate) cert;
    }

    public InputStream getFileInputStream(String p12File) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFileInputStream'");
    }
}
