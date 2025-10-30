package com.example.demo.controller;

import com.example.demo.entity.CertificateRequest;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepositoryI;
import com.example.demo.service.CertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificate-requests")
public class CertificateRequestController {

    @Autowired
    private CertificateRequestService certificateRequestService;

    @Autowired
    private StudentRepositoryI studentRepository;

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody Map<String, Object> requestData, Authentication authentication) {
        try {
            String templateId = (String) requestData.get("templateId");
            String requestType = (String) requestData.get("requestType");
            String reason = (String) requestData.get("reason");
            String serialNo = (String) requestData.get("serialNo");

            // Get current user ID from authentication
            String username = authentication.getName();
            // For now, we'll need to get student ID from username
            // This should be improved to get from JWT token or user context

            Long studentId = getStudentIdFromUsername(username); // Implement this method

            CertificateRequest request = certificateRequestService.createRequest(
                templateId, requestType, reason, serialNo, studentId
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", request.getId());
            response.put("message", "Certificate request created successfully");
            response.put("status", request.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to create certificate request: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping
    public ResponseEntity<Page<CertificateRequest>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CertificateRequest> requests = certificateRequestService.getAllRequestsPaged(page, size);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/my")
    public ResponseEntity<List<CertificateRequest>> getMyRequests(Authentication authentication) {
        try {
            String username = authentication.getName();
            Long studentId = getStudentIdFromUsername(username);
            List<CertificateRequest> requests = certificateRequestService.getRequestsByStudent(studentId);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<CertificateRequest>> getRecentRequests(@RequestParam(defaultValue = "5") int size) {
        List<CertificateRequest> requests = certificateRequestService.getRecentRequests(size);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CertificateRequest>> getPendingRequests() {
        List<CertificateRequest> requests = certificateRequestService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> statusData) {
        try {
            String status = (String) statusData.get("status");
            Long staffId = statusData.get("staffId") != null ? Long.valueOf(statusData.get("staffId").toString()) : null;

            CertificateRequest updatedRequest = certificateRequestService.updateRequestStatus(id, status, staffId);

            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedRequest.getId());
            response.put("status", updatedRequest.getStatus());
            response.put("message", "Request status updated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to update request status: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Helper method - should be implemented properly
    private Long getStudentIdFromUsername(String username) {
        // Find student by username
        return studentRepository.findByUsername(username)
                .map(Student::getId)
                .orElseThrow(() -> new RuntimeException("Student not found for username: " + username));
    }
}