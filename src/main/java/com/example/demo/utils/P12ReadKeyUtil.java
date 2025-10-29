package com.example.demo.utils;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class P12ReadKeyUtil {

    public static PrivateKey getPrivateKey(String path, String alias, char[] password) throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(path), password);
        return (PrivateKey) ks.getKey(alias, password);
    }

    public static X509Certificate getCertificate(String path, String alias, char[] password) throws Exception {
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(path), password);
        return (X509Certificate) ks.getCertificate(alias);
    }
}
