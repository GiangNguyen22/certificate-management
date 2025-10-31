package com.example.demo.service;

import com.example.demo.entity.Staff;
import com.example.demo.entity.Student;
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
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;

@Service
public class KeyService {

    @Autowired
    private UserPublicKeysRepository keyRepository;

    @Autowired
    private UserRepository userRepository;

    public UserPublicKeys generateKeyPair(String userId, String password) throws Exception {
        // Generate RSA key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        // Encode private key to Base64 for storage in .crt file
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());

        // Get user info
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Save to database
        UserPublicKeys keyEntity = new UserPublicKeys();
        keyEntity.setStaff((Staff) user);
        keyEntity.setPublicKey(publicKeyBase64);
        keyEntity.setCreatedAt(java.time.LocalDateTime.now().toString());

        UserPublicKeys savedKey = keyRepository.save(keyEntity);

        // Create .crt file with private key
        createCrtFile(savedKey.getId(), privateKeyBase64, user.getUsername());

        return savedKey;
    }

    private void createCrtFile(Long keyId, String privateKeyBase64, String userName) throws Exception {
        // Create certificates directory if it doesn't exist
        Path certDir = Paths.get("certificates");
        if (!java.nio.file.Files.exists(certDir)) {
            java.nio.file.Files.createDirectories(certDir);
        }

        // Write private key to .crt file
        String fileName = "key_" + userName + "_" + keyId + ".crt";
        Path filePath = certDir.resolve(fileName);

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

        User user = userRepository.findById(key.getStaff().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileName = "key_" + user.getUsername() + "_" + keyId + ".crt";
        Path filePath = Paths.get("certificates", fileName);

        if (!java.nio.file.Files.exists(filePath)) {
            throw new RuntimeException("Key file not found");
        }

        return filePath;
    }
}