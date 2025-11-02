package com.example.demo.service;

import com.example.demo.entity.CertificateRequest;
import com.example.demo.repository.CertificateRequestRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateRequestService {

    private final CertificateRequestRepository certificateRequestRepository;

    @Autowired
    private UserService userService;

    /**
     * Tạo mới một yêu cầu chứng nhận
     */
    public String createRequest(String templateId, String requestCode, String type, String status, String studentId) {
        try {
            // Validate input cơ bản
            if (studentId == null || studentId.trim().isEmpty()) {
                throw new IllegalArgumentException("Student ID cannot be null or empty");
            }
            if (templateId == null || templateId.trim().isEmpty()) {
                throw new IllegalArgumentException("Template ID cannot be null or empty");
            }

            CertificateRequest request = new CertificateRequest();
            request.setTemplateId(templateId);
            request.setRequestCode(requestCode);
            request.setRequestType(type);
            request.setStatus(status != null ? status : "PENDING");
            request.setStudentRequestId(studentId);

            // Populate student info from UserService
            try {
                Map<String, Object> studentProfile = userService.getUserProfile(studentId);
                request.setStudentName((String) studentProfile.get("name"));
                request.setStudentCode((String) studentProfile.get("studentCode"));
            } catch (Exception e) {
                log.warn("Could not populate student info for request: {}", e.getMessage());
                // Set defaults if profile lookup fails
                request.setStudentName("Unknown");
                request.setStudentCode(studentId);
            }

            request.setCreatedAt(LocalDateTime.now());
            request.setUpdatedAt(LocalDateTime.now());

            certificateRequestRepository.save(request);

            log.info("Created certificate request [{}] for student {}", requestCode, studentId);
            return requestCode;
        } catch (Exception e) {
            log.error(" Failed to create certificate request: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating certificate request: " + e.getMessage());
        }
    }

    /**
     * Lấy tất cả request (phân trang)
     */
    public Page<CertificateRequest> getAllRequestsPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return certificateRequestRepository.findAll(pageable);
    }

    /**
     * Lấy request theo studentId (chỉ của student đó)
     */
    public List<CertificateRequest> getRequestsByStudent(String studentId) {
        return certificateRequestRepository.findByStudentRequestId(studentId);
    }

    /**
     * Lấy danh sách request có trạng thái "PENDING"
     */
    public List<CertificateRequest> getPendingRequests() {
        return certificateRequestRepository.findByStatus("PENDING");
    }

    /**
     * Lấy các request mới nhất (theo limit)
     */
    public List<CertificateRequest> getRecentRequests(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return certificateRequestRepository.findAll(pageable).getContent();
    }

    /**
     * Cập nhật trạng thái request (nhân viên)
     */
    public CertificateRequest updateRequestStatus(Long requestId, String status, Long staffId) {
        CertificateRequest request = certificateRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found with id: " + requestId));

        request.setStatus(status);
        request.setUpdatedAt(LocalDateTime.now());

        if ("APPROVED".equalsIgnoreCase(status) && staffId != null) {
            request.setStaffApprovedId(staffId);
            request.setReviewedAt(LocalDateTime.now());

            // TODO: gọi service tạo certificate
            log.info("🧾 Request {} approved by staff {}", requestId, staffId);
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
        // TODO: thêm trường adminNotes nếu entity có

        return certificateRequestRepository.save(request);
    }

    /**
     * Lấy chi tiết request theo id
     */
    public Optional<CertificateRequest> getRequestById(Long id) {
        return certificateRequestRepository.findById(id);
    }

}
