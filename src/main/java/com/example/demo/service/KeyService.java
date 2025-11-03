package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.UserPublicKeys;
import com.example.demo.repository.UserPublicKeysRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

@Service
public class KeyService {

    @Autowired
    private UserPublicKeysRepository keyRepository;

    @Autowired
    private UserRepository userRepository;

    public UserPublicKeys generateKeyPair(Long userId, String cryptographyType) throws Exception {
        // Generate key pair based on type
        KeyPairGenerator keyGen;
        if ("EC".equalsIgnoreCase(cryptographyType)) {
            keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(new java.security.spec.ECGenParameterSpec("secp256r1"));
        } else {
            keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
        }
        KeyPair keyPair = keyGen.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Encode keys to Base64
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        // Get user info
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Save to database
        UserPublicKeys keyEntity = new UserPublicKeys();
        keyEntity.setUser(user);
        keyEntity.setPublicKey(publicKeyBase64);
        keyEntity.setCreatedAt(java.time.LocalDateTime.now().toString());
        keyEntity.setCryptographyType(cryptographyType != null ? cryptographyType : "RSA");

        UserPublicKeys savedKey = keyRepository.save(keyEntity);

        // Create .key file with private key (not .crt)
        createPrivateKeyFile(savedKey.getId(), privateKeyBase64, user.getUsername());

        return savedKey;
    }

    private void createPrivateKeyFile(Long keyId, String privateKeyBase64, String userName) throws Exception {
        // Create keys directory if it doesn't exist
        Path keysDir = Paths.get("keys");
        if (!java.nio.file.Files.exists(keysDir)) {
            java.nio.file.Files.createDirectories(keysDir);
        }

        // Write private key to .key file
        String fileName = "private_key_" + userName + "_" + keyId + ".key";
        Path filePath = keysDir.resolve(fileName);

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            // Write private key in PEM format
            fos.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
            fos.write(privateKeyBase64.getBytes());
            fos.write("\n-----END PRIVATE KEY-----\n".getBytes());
        }
    }

    public List<UserPublicKeys> getAllKeys() {
        return keyRepository.findAll();
    }

    public Path getKeyFile(Long keyId) throws Exception {
        UserPublicKeys key = keyRepository.findById(keyId)
                .orElseThrow(() -> new RuntimeException("Key not found"));

        User user = key.getUser();
        if (user == null) {
            throw new RuntimeException("User not found for key");
        }

        String fileName = "private_key_" + user.getUsername() + "_" + keyId + ".key";
        Path filePath = Paths.get("keys", fileName);

        if (!java.nio.file.Files.exists(filePath)) {
            throw new RuntimeException("Key file not found");
        }

        return filePath;
    }
}