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

    @GetMapping
    public ResponseEntity<Page<CertificateRequest>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CertificateRequest> requests = certificateRequestService.getAllRequestsPaged(page, size);
        return ResponseEntity.ok(requests);
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

   
}