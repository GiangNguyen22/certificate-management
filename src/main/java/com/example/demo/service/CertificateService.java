package com.example.demo.service;


import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.StaffRepository;
import com.example.demo.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.example.demo.entity.Staff;

import com.example.demo.service.interfaces.UserKeyService;

@Service
public class CertificateService {


    @Autowired
    private StaffRepository staffRepository;

    public Path create(String staffId, String password) throws Exception {
        // 1️⃣ Lấy thông tin staff
        Staff staff = staffRepository.findByStaffCode(staffId);
        if (staff == null) {
            throw new ResourceNotFoundEx("Staff not found with ID: " + staffId);
        } else{
        // 2️⃣ Sinh keystore base64 bằng ECC (self-signed)
        KeyUtil keyUtil = new KeyUtil();
    
        String pkcs12Base64 = keyUtil.generatePKCS12Base64(
                staff.getStaffCode(),
                password.toCharArray(),
                staff
        );

        //  Giải Base64 -> bytes
        byte[] p12Bytes = Base64.getDecoder().decode(pkcs12Base64);

        //  Tạo thư mục keycert (nếu chưa có)
        Path keycertDir = Path.of("keycert");
        if (!Files.exists(keycertDir)) {
            Files.createDirectories(keycertDir);
        }

        //  Đặt tên file (VD: STAFFCODE_YYYYMMDDHHMMSS.p12)
        String filename = String.format("%s_%d.p12", staff.getStaffCode(), System.currentTimeMillis());
        Path filePath = keycertDir.resolve(filename);

        //  Ghi file ra thư mục
        Files.write(filePath, p12Bytes);

        //  (Tùy chọn) trích xuất thông tin để log hoặc lưu DB
        Map<String, Object> infoMap = getCertificateInfoFromBase64(pkcs12Base64, staff.getStaffCode(), password.toCharArray());

        System.out.println("Certificate info:");
        infoMap.forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println("Keystore saved at: " + filePath.toAbsolutePath());
        //luw vao db
        saveNewPubKeyInfoWithUser(staff.getStaffCode(), keyUtil.getPublicKey().toString(), java.time.LocalDate.now().toString(), keyUtil.getCryptoType().toString());
        // 8️⃣ Trả về đường dẫn để controller có thể gửi file
        System.out.println(keyUtil.getPublicKey().toString());
        return filePath;
        }
         
    }
    @Autowired
    private UserKeyService userKeyService;
    public void saveNewPubKeyInfoWithUser(String staffcode, String publickey, String createdAt, String CryptoType){
        userKeyService.saveNewUserKey(staffcode, publickey, createdAt, CryptoType);
    }
    
    private Map<String, Object> getCertificateInfoFromBase64(String base64P12, String alias, char[] password) throws Exception {
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);

        java.security.cert.Certificate cert = pkcs12.getCertificate(alias);
        if (cert == null) throw new ResourceNotFoundEx("No certificate found with alias: " + alias);

        X509Certificate x509 = (X509Certificate) cert;
        Map<String, Object> info = new HashMap<>();
        info.put("subject", x509.getSubjectX500Principal().getName());
        info.put("issuer", x509.getIssuerX500Principal().getName());
        info.put("serialNumber", x509.getSerialNumber().toString());
        info.put("notBefore", x509.getNotBefore());
        info.put("notAfter", x509.getNotAfter());
        info.put("signatureAlgorithm", x509.getSigAlgName());
        info.put("version", x509.getVersion());
        return info;
    }

    /**
     * Export .p12 ra file tạm để tải về
     */
    public Path exportP12ToFile(String studentId) throws Exception {
        com.example.demo.entity.Certificate certEntity = certRepo.findCertificateByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        // Decode Base64
        byte[] p12Bytes = Base64.getDecoder().decode(certEntity.getCertificate());

        // Tạo file tạm
        Path tempFile = Files.createTempFile(studentId + "_cert", ".p12");
        Files.write(tempFile, p12Bytes);
        System.out.println(tempFile.toAbsolutePath());

        return tempFile;
    }

    /**
     * Sign certificate with staff's private key
     */
    public Certificate signCertificate(String certificateId, String staffId) throws Exception {
        // Get certificate
        Certificate cert = certRepo.findById(Long.parseLong(certificateId))
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        // Here we would implement the actual signing logic
        // For now, just update the status to indicate it's signed
        cert.setStatus("SIGNED");
        return certRepo.save(cert);
    }
}
