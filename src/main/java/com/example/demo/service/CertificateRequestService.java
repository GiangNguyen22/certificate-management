package com.example.demo.service;

import com.example.demo.entity.CertificateRequest;
import com.example.demo.entity.Student;
import com.example.demo.repository.CertificateRequestRepository;
import com.example.demo.repository.StudentRepositoryI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateRequestService {

    @Autowired
    private CertificateRequestRepository certificateRequestRepository;

    @Autowired
    private StudentRepositoryI studentRepository;

    public CertificateRequest createRequest(String templateId, String requestType, String reason, String serialNo, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        CertificateRequest request = CertificateRequest.builder()
                .templateId(templateId)
                .requestType(requestType)
                .reason(reason)
                .serialNo(serialNo)
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .student(student)
                .build();

        return certificateRequestRepository.save(request);
    }

    public List<CertificateRequest> getAllRequests() {
        return certificateRequestRepository.findAllOrderByCreatedAtDesc();
    }

    public Page<CertificateRequest> getAllRequestsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return certificateRequestRepository.findAll(pageable);
    }

    public List<CertificateRequest> getRequestsByStudent(Long studentId) {
        return certificateRequestRepository.findByStudentId(studentId);
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
            // TODO: Implement staff assignment logic
            // For now, we'll just update the status
        }

        return certificateRequestRepository.save(request);
    }

    public CertificateRequest reviewRequest(Long requestId, String status, String adminNotes) {
        CertificateRequest request = certificateRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());

        return certificateRequestRepository.save(request);
    }

    public Optional<CertificateRequest> getRequestById(Long id) {
        return certificateRequestRepository.findById(id);
    }
}