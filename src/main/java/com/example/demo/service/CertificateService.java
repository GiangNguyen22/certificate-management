package com.example.demo.service;


import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.StaffRepository;
import com.example.demo.repository.CertificateRepository;
import com.example.demo.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.demo.entity.Staff;
import com.example.demo.dto.response.CertificateDTO;
import com.example.demo.entity.Certificate;

import com.example.demo.service.interfaces.UserKeyService;

@Service
public class CertificateService {


    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CertificateRepository certRepo;

    public Path create(String staffId, String password) throws Exception {
        // 1️⃣ Lấy thông tin staff
        Staff staff = staffRepository.findByStaffCode(staffId).orElseThrow(() -> new RuntimeException("Staff not found"));
        if (staff == null) {
            throw new ResourceNotFoundEx("Staff not found with ID: " + staffId);
        } else{
        // 2️⃣ Sinh keystore base64 bằng RSA (self-signed)
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
        //convert publickey to base 64
        String publicKeyBase64 = Base64.getEncoder().encodeToString(keyUtil.getPublicKey().getEncoded());
        //luw vao db
        saveNewPubKeyInfoWithUser(staff.getStaffCode(), publicKeyBase64, java.time.LocalDateTime.now().toString(), keyUtil.getCryptoType().toString());
        // Trả về đường dẫn để controller có thể gửi file
        System.out.println(keyUtil.getPublicKey().toString());
        System.out.println(keyUtil.getPrivateKey().toString());
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


    public Certificate signCertificate(String certificateId, String staffId) throws Exception {
        // Get certificate
        Certificate cert = certRepo.findById(Long.parseLong(certificateId))
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        // Here we would implement the actual signing logic
        // For now, just update the status to indicate it's signed
        cert.setStatus("SIGNED");
        return certRepo.save(cert);
    }

    @Autowired
    private fillCertificate fillCertificate;

    /**
     * Get all certificates with pagination
     */
    public Page<Certificate> getAllCertificatesPaged(Pageable pageable) {
        return certRepo.findAll(pageable);
    }


    /**
     * Get certificates by student code with pagination
     */
    public Page<CertificateDTO> getCertificatesByStudentCodePaged(String studentCode, Pageable pageable) {
        Page<Certificate> certificates = certRepo.findByStudentId(studentCode, pageable);
        return certificates.map(cert -> new CertificateDTO(cert.getId(), cert.getCertId(), cert.getStudentId(),
                cert.getIssued_at(), cert.getExpire_at(), cert.getStatus(), cert.getSerial_no()));
    }
    public Certificate getCertificateByCertIdAndStudentId(String certId, String studentCode) {
        return certRepo.findByCertIdAndStudentId(certId, studentCode);
    }
    /**
     * Get certificate expiration statistics
     */
    public Map<String, Object> getExpirationStats() {
        List<Certificate> allCertificates = certRepo.findAll();
        LocalDate now = LocalDate.now();

        long expiringSoon = allCertificates.stream()
                .filter(cert -> cert.getExpire_at() != null && !cert.getExpire_at().isEmpty())
                .filter(cert -> {
                    try {
                        LocalDate expireDate = LocalDate.parse(cert.getExpire_at().substring(0, 10)); // Extract date part
                        long daysUntilExpiry = ChronoUnit.DAYS.between(now, expireDate);
                        return daysUntilExpiry <= 30 && daysUntilExpiry > 0;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        long expired = allCertificates.stream()
                .filter(cert -> cert.getExpire_at() != null && !cert.getExpire_at().isEmpty())
                .filter(cert -> {
                    try {
                        LocalDate expireDate = LocalDate.parse(cert.getExpire_at().substring(0, 10)); // Extract date part
                        return expireDate.isBefore(now);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        long totalActive = allCertificates.stream()
                .filter(cert -> "ACTIVE".equalsIgnoreCase(cert.getStatus()))
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("expiringSoon", expiringSoon);
        stats.put("expired", expired);
        stats.put("totalActive", totalActive);

        return stats;
    }

    /**
     * Get certificate by ID
     */
    public Certificate getCertificateById(String id) throws Exception {
        return certRepo.findById(Long.parseLong(id))
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    /**
     * Get certificate PDF by ID
     */
    public byte[] getCertificatePdf(String id) throws Exception {
        Certificate cert = getCertificateById(id);

        if (cert.getPdf_uri() == null || cert.getPdf_uri().isEmpty()) {
            throw new RuntimeException("PDF not found for certificate");
        }

        // Read PDF file from path
        Path pdfPath = Path.of(cert.getPdf_uri());
        if (!Files.exists(pdfPath)) {
            throw new RuntimeException("PDF file not found on disk");
        }

        return Files.readAllBytes(pdfPath);
    }

    /**
     * Check if certificate PDF exists
     */
    public boolean certificatePdfExists(String id) throws Exception {
        Certificate cert = getCertificateById(id);
        if (cert.getPdf_uri() == null || cert.getPdf_uri().isEmpty()) {
            return false;
        }
        Path pdfPath = Path.of(cert.getPdf_uri());
        return Files.exists(pdfPath);
    }

}
