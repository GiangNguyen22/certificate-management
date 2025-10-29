package com.example.demo.service;

import com.example.demo.entity.Certificate;
import com.example.demo.entity.Student;
import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.CertificateRepository;
import com.example.demo.repository.StudentRepositoryI;
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


@Service
public class CertificateService {
    @Autowired
    private CertificateRepository certRepo;

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private StudentRepositoryI studentRepositoryI;

    public Certificate create(String studentId, String password) throws Exception {
        Student student = studentRepositoryI.findByStudentCode(studentId).orElseThrow(() -> new ResourceNotFoundEx("Student not found"));

        String pkcs12Base64 = KeyUtil.generatePKCS12Base64(student.getStudentCode(), password.toCharArray(), student);
        Map<String, Object> infoMap = getCertificateInfoFromBase64(pkcs12Base64, student.getStudentCode(), password.toCharArray());
        Certificate certificate = Certificate.builder()
                .certId("123456")
                .templateId("123")
                .issued_at(infoMap.get("issuer").toString())
                 .expire_at(infoMap.get("notAfter").toString())
                 .serial_no(infoMap.get("serialNumber").toString())
                .alias(student.getStudentCode())
                .password(password)
                .studentId(studentId)
                 .certificate(pkcs12Base64)
                .build();
        return certificate;
    }

    private  Map<String, Object> getCertificateInfoFromBase64(String base64P12, String alias, char[] password) throws Exception {
        // 1. Decode Base64 -> byte[]
        byte[] p12Bytes = Base64.getDecoder().decode(base64P12);

        // 2. Load PKCS12 keystore
        KeyStore pkcs12 = KeyStore.getInstance("PKCS12");
        pkcs12.load(new ByteArrayInputStream(p12Bytes), password);

        // 3. Lấy certificate theo alias
        java.security.cert.Certificate cert = pkcs12.getCertificate(alias);
        if (cert == null) {
            throw new ResourceNotFoundEx("No certificate found with alias: " + alias);
        }

        X509Certificate x509 = (X509Certificate) cert;

        // 4. Extract thông tin
        Map<String, Object> info = new HashMap<>();
        info.put("subject", x509.getSubjectX500Principal().getName());
        info.put("issuer", x509.getIssuerX500Principal().getName());
        info.put("serialNumber", x509.getSerialNumber().toString());
        info.put("notBefore", x509.getNotBefore()); //Valid from
        info.put("notAfter", x509.getNotAfter()); //Valid to
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
