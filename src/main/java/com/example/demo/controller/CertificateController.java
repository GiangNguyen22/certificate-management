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
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to load certificates: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }



    @GetMapping("/certificates/expiration-stats")
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
