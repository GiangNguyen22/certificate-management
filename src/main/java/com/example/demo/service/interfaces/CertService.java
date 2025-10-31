package com.example.demo.service.interfaces;

import com.example.demo.entity.Certificate;

public interface CertService {
    String createCertificateForStudent(String studentCode) throws Exception;
    Certificate saveCertificateRecord(String certId, String templateId, String studentId, String issuedAt, String expireAt, String status, String serialNo, String pdfUri, String pdfSha256) throws Exception;
}
