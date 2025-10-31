package com.example.demo.service;

import com.example.demo.entity.CertificateRequest;
import com.example.demo.entity.Student;
import com.example.demo.repository.CertificateRequestRepository;
import com.example.demo.repository.StudentRepositoryI;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertificateRequestService {

    private final CertificateRequestRepository certificateRequestRepository;

    private final StudentRepositoryI studentRepository;

    public String createRequest(String templateId, String requestCode, String type, String status, String studentId) {

        CertificateRequest request = new CertificateRequest();

        request.setTemplateId(templateId);
        request.setRequestCode(requestCode);
        request.setRequestType(type);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        request.setStatus(status);
        request.setStudentRequestId(studentId);
        certificateRequestRepository.save(request);
        return requestCode;
    }



    public Page<CertificateRequest> getAllRequestsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return certificateRequestRepository.findAll(pageable);
    }

    public List<CertificateRequest> getRequestsByStudent(String studentId) {
        return certificateRequestRepository.findByStudentRequestId(studentId);
    }

    public List<CertificateRequest> getPendingRequests() {
        return certificateRequestRepository.findByStatus("PENDING");
    }

    public List<CertificateRequest> getRecentRequests(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<CertificateRequest> page = certificateRequestRepository.findAll(pageable);
        return page.getContent();
    }

    public CertificateRequest updateRequestStatus(Long requestId, String status, Long staffId) {
        CertificateRequest request = certificateRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());

        // If approving, assign staff
        if ("APPROVED".equals(status) && staffId != null) {
            // Note: Need to implement staff assignment logic
        }

        return certificateRequestRepository.save(request);
    }

    public Optional<CertificateRequest> getRequestById(Long id) {
        return certificateRequestRepository.findById(id);
    }
}
