package com.example.demo.service;


import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.StaffRepository;
import com.example.demo.repository.CertificateRepository;
import com.example.demo.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
import com.example.demo.entity.Certificate;


@Service
public class CertificateService {


    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CertificateRepository certRepo;

    
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
     * Sign certificate with staff's private key and generate PDF
     */
    public Certificate signCertificate(String certificateId, String staffId) throws Exception {
        // Get certificate
        Certificate cert = certRepo.findById(Long.parseLong(certificateId))
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        // Generate the actual PDF certificate using student info
        // This should use the existing template and fill with student data
        String studentId = cert.getStudentId();
        if (studentId != null && !studentId.isEmpty()) {
            try {
                // Generate PDF using fillCertificate service
                // We need to get student code from studentId
                // For now, assume studentId is the student code
                String pdfPath = fillCertificate.generateCertificate(studentId);

                // Update certificate with PDF path
                cert.setPdf_uri(pdfPath);
                cert.setStatus("COMPLETED");
                cert.setUserSignedId(staffId); // Set who signed it
            } catch (Exception e) {
                System.err.println("Failed to generate PDF for certificate: " + e.getMessage());
                cert.setStatus("ERROR");
            }
        } else {
            cert.setStatus("ERROR");
        }

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
     * Get certificates by student ID with pagination
     */
    public Page<Certificate> getCertificatesByStudentIdPaged(String studentId, Pageable pageable) {
        return certRepo.findByStudentId(studentId, pageable);
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
