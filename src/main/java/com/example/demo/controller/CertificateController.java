package com.example.demo.controller;

import com.example.demo.entity.Certificate;
import com.example.demo.service.CertificateService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;


    @PostMapping("/cert")
    public ResponseEntity<String> addCertificate(@RequestParam String studentId, @RequestParam String password) throws Exception {
        certificateService.create(studentId, password);
        return new ResponseEntity<>("Certificate created successfully", HttpStatus.CREATED);
    }
    @PostMapping("/cert/sign")
    public ResponseEntity<Certificate> signCertificate(@RequestParam String certificateId, @RequestParam String staffId) throws Exception {
        Certificate cert = certificateService.signCertificate(certificateId, staffId);
        return ResponseEntity.ok(cert);
    }

    @GetMapping("/certificates")
    public ResponseEntity<Map<String, Object>> getAllCertificates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Certificate> certificatePage = certificateService.getAllCertificatesPaged(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", certificatePage.getContent());
            response.put("totalElements", certificatePage.getTotalElements());
            response.put("totalPages", certificatePage.getTotalPages());
            response.put("currentPage", page);
            response.put("size", size);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return empty response instead of error to prevent frontend crashes
            Map<String, Object> response = new HashMap<>();
            response.put("content", new java.util.ArrayList<>());
            response.put("totalElements", 0);
            response.put("totalPages", 0);
            response.put("currentPage", page);
            response.put("size", size);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyCertificates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            // Assuming username is the studentId for now - adjust based on your User entity structure
            Pageable pageable = PageRequest.of(page, size);
            Page<Certificate> certificatePage = certificateService.getCertificatesByStudentIdPaged(username, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", certificatePage.getContent());
            response.put("totalElements", certificatePage.getTotalElements());
            response.put("totalPages", certificatePage.getTotalPages());
            response.put("currentPage", page);
            response.put("size", size);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return empty response instead of error to prevent frontend crashes
            Map<String, Object> response = new HashMap<>();
            response.put("content", new java.util.ArrayList<>());
            response.put("totalElements", 0);
            response.put("totalPages", 0);
            response.put("currentPage", page);
            response.put("size", size);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificateById(@PathVariable String id) {
        try {
            Certificate certificate = certificateService.getCertificateById(id);
            return ResponseEntity.ok(certificate);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to load certificates: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getCertificatePdf(@PathVariable String id) {
        try {
            byte[] pdfBytes = certificateService.getCertificatePdf(id);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=\"certificate_" + id + ".pdf\"")
                    .body(pdfBytes);
        } catch (Exception e) {
            // Return a simple error message as text
            String errorMessage = "Certificate PDF not available. Error: " + e.getMessage();
            byte[] errorBytes = errorMessage.getBytes();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .body(errorBytes);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable String id) {
        try {
            byte[] pdfBytes = certificateService.getCertificatePdf(id);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"certificate_" + id + ".pdf\"")
                    .body(pdfBytes);
        } catch (Exception e) {
            String errorMessage = "Certificate PDF not available for download. Error: " + e.getMessage();
            byte[] errorBytes = errorMessage.getBytes();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .body(errorBytes);
        }
    }

    @GetMapping("/{id}/pdf-exists")
    public ResponseEntity<Map<String, Object>> checkCertificatePdfExists(@PathVariable String id) {
        try {
            boolean exists = certificateService.certificatePdfExists(id);
            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to check PDF existence: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/expiration-stats")
    public ResponseEntity<Map<String, Object>> getExpirationStats() {
        try {
            Map<String, Object> stats = certificateService.getExpirationStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to load expiration stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
