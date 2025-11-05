package com.example.demo.controller;

import com.example.demo.entity.CertificateRequest;
import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepositoryI;
import com.example.demo.service.CertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping
    public ResponseEntity<Page<CertificateRequest>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CertificateRequest> requests = certificateRequestService.getAllRequestsPaged(page, size);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            // Return empty page if there's an error
            return ResponseEntity.ok(Page.empty());
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<List<CertificateRequest>> getRecentRequests(@RequestParam(defaultValue = "5") int size) {
        try {
            List<CertificateRequest> requests = certificateRequestService.getRecentRequests(size);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<CertificateRequest>> getPendingRequests() {
        try {
            List<CertificateRequest> requests = certificateRequestService.getPendingRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.ok(new java.util.ArrayList<>());
        }
    }

  @GetMapping("/my")
public ResponseEntity<List<CertificateRequest>> getMyRequests() {
    try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Student student = studentRepository.findByUsername(username).orElse(null);
        // Tìm student từ username
        if (student == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        List<CertificateRequest> requests = certificateRequestService.getRequestsByStudent(student.getStudentCode());
        return ResponseEntity.ok(requests);
    } catch (Exception e) {
        return ResponseEntity.ok(new ArrayList<>());
    }
}
    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody Map<String, Object> requestData) {
        try {
            String templateId = String.valueOf(requestData.get("templateId"));
            String requestCode = String.valueOf(requestData.get("requestCode"));
            String type = String.valueOf(requestData.get("type"));
            String status = String.valueOf(requestData.get("status"));
            String studentId = String.valueOf(requestData.get("studentId"));

            // Validate required fields
            if (studentId == null || studentId.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Student ID is required");
                return ResponseEntity.badRequest().body(error);
            }

            String result = certificateRequestService.createRequest(templateId, requestCode, type, status, studentId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Request created successfully");
            response.put("requestCode", result);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create request: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Object> statusData) {
        try {
            String status = (String) statusData.get("status");
            Long staffId = statusData.get("staffId") != null ? Long.valueOf(statusData.get("staffId").toString())
                    : null;

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

}