package com.example.demo.service.impl;

import com.example.demo.entity.Staff;
import com.example.demo.repository.StaffRepository;
import com.example.demo.service.interfaces.p12Service;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@Service
public class p12ServiceImpl implements p12Service {
    private final StaffRepository staffRepository;

    public p12ServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public ByteArrayResource generateP12(String staffCode) {
        // ✅ Normalize input
        String normalizedCode = staffCode.trim().toUpperCase();
        System.out.println("🔍 Input staffCode = [" + normalizedCode + "]");
        System.out.println("📋 All staff in DB:");
        List<Staff> all = staffRepository.findAll();
        System.out.println("Total staff: " + all.size());
        for (Staff s : all) {
            System.out.println(" - [" + s.getStaffCode() + "] (len=" + s.getStaffCode().length() + ")");
        }
        System.out.println("🔍 Input staffCode = [" + staffCode + "] (len=" + staffCode.length() + ")");

        try {
            if (normalizedCode.isEmpty()) {
                throw new RuntimeException("Staff code cannot be null or empty");
            }

            Staff staff = staffRepository.findByStaffCode(normalizedCode)
                    .orElseThrow(() -> new RuntimeException("Staff not found with staff code: " + normalizedCode));

            // Generate secure random password
            String demoPassword = "123456";

            com.example.demo.utils.KeyUtil keyUtil = new com.example.demo.utils.KeyUtil();
            String pkcs12Base64 = keyUtil.generatePKCS12Base64(staff.getStaffCode(), demoPassword.toCharArray(), staff);
            byte[] p12Bytes = Base64.getDecoder().decode(pkcs12Base64);

            Path keycertDir = Path.of("keycert");
            if (!Files.exists(keycertDir)) {
                Files.createDirectories(keycertDir);
            }

            String filename = String.format("%s_%d.p12", staff.getStaffCode(), System.currentTimeMillis());
            Path filePath = keycertDir.resolve(filename);
            Files.write(filePath, p12Bytes);

            return new ByteArrayResource(p12Bytes);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to generate p12 for user " + normalizedCode + ": " + ex.getMessage(),
                    ex);
        }
    }
}
