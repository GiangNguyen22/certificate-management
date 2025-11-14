package com.example.demo.service;
import com.example.demo.entity.CertificateRequest;
import com.example.demo.repository.CertificateRequestRepository;
import lombok.RequiredArgsConstructor;
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


//    public String findStatusById(Long requestId) {
//        return certificateRequestRepository.findStatusById(requestId);
//    }

    public String findStatusByRequestCode(String requestCode) {
        return certificateRequestRepository.findStatusByRequestCode(requestCode);
    }

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
        return certificateRequestRepository.findAll(pageable).getContent();
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

    /**
     * Review request bởi admin
     */
    public CertificateRequest reviewRequest(Long requestId, String status, String adminNotes) {
        CertificateRequest request = certificateRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());

        return certificateRequestRepository.save(request);
    }

    /**
     * Lấy chi tiết request theo id
     */
    public Optional<CertificateRequest> getRequestById(Long id) {
        return certificateRequestRepository.findById(id);
    }
public boolean updateStatusOfRequest(String requestCode, String newStatus) {
    Optional<CertificateRequest> optionalRequest = 
        certificateRequestRepository.findByRequestCode(requestCode); // Correct

    if (optionalRequest.isPresent()) {
        CertificateRequest request = optionalRequest.get();
        request.setStatus(newStatus);
        request.setUpdatedAt(LocalDateTime.now());
        certificateRequestRepository.save(request);
        return true;
    }
    return false;
}

}