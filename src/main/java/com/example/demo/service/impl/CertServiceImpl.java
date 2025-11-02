package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Certificate;
import com.example.demo.entity.Student;
import com.example.demo.repository.CertificateRepository;
import com.example.demo.service.fillCertificate;
import com.example.demo.service.interfaces.CertService;
import com.example.demo.service.StudentService;

@Service
public class CertServiceImpl implements CertService {
    @Autowired
    private StudentService studentService;

    @Autowired
    private fillCertificate fillCert;

    @Autowired
    private com.example.demo.repository.CertificateRepository certRepo;

    @Override
    public String createCertificateForStudent(String studentCode) throws Exception {
        Student student = studentService.getStudentByStudentCode(studentCode);

        // validate GPA
        Double gpa = student.getGpa();
        if (gpa == null || gpa < 0.0 || gpa > 4.0) {
            throw new Exception("Invalid GPA for student with studentCode: " + studentCode);
        }

        // validate graduation status
        String status = student.getStatusSV();
        if (status == null || !"GRADUATED".equalsIgnoreCase(status.trim())) {
            throw new Exception("Student is not graduated, cannot create certificate: " + studentCode);
        }

        // generate the certificate PDF (path returned) — keep return type void per interface
        String pdfPath = fillCert.generateCertificate(studentCode);
        // Persist certificate record to database
        Certificate certificate = new Certificate();
        certificate.setCertId(studentCode);
        certificate.setTemplateId("CERT_001"); // Default template
        certificate.setUserId(student.getId());
        certificate.setIssued_at(java.time.LocalDate.now().toString());
        certificate.setExpire_at(java.time.LocalDate.now().plusYears(1).toString()); // 1 year validity
        certificate.setStatus("ACTIVE");
        certificate.setSerial_no("CERT_" + System.currentTimeMillis());
        certificate.setAlias(studentCode);
        certificate.setPassword("default_password"); // Should be generated securely
        certificate.setPdf_uri(pdfPath);
        certificate.setPdf_sha256(""); // Calculate SHA256 if needed

        // Save to database
        certRepo.save(certificate);

        return pdfPath;
    }

    @Autowired
    private CertificateRepository certrepo;
    @Override
    public Certificate saveCertificateRecord(String certId, String templateId, String studentId,String userSignedId ,String issuedAt, String expireAt, String status, String serialNo, String pdfUri, String pdfSha256) throws Exception {
        Certificate cert = new Certificate();
        cert.setCertId(certId);
        cert.setTemplateId(templateId);
        cert.setStudentId(studentId);
        cert.setUserSignedId(userSignedId);
        cert.setIssued_at(issuedAt);
        cert.setExpire_at(expireAt);
        cert.setStatus(status);
        cert.setSerial_no(serialNo);
        cert.setPdf_uri(pdfUri);
        cert.setPdf_sha256(pdfSha256);

        // Here you would typically save the certificate to the database
        

        return certrepo.save(cert);
    }
}

