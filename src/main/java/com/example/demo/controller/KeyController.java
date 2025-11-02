package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.UserPublicKeys;
import com.example.demo.service.KeyService;
import com.example.demo.service.interfaces.p12Service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/keys")
public class KeyController {

    private final KeyService keyService;
    private final p12Service p12service;

    public KeyController(KeyService keyService, p12Service p12service) {
        this.keyService = keyService;
        this.p12service = p12service;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateCertp12(@RequestBody Map<String, String> body) {
        String staffId = body.get("staffId");
        String adminId = body.get("adminId");
        try {
            String p12CertPath = p12service.generateP12(staffId);
            Map<String, String> data = Map.of("p12CertPath", p12CertPath);
            ApiResponse response = new ApiResponse(true, "SUCCESS", "P12 certificate generated successfully", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "ERROR", "Error generating P12 certificate: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserPublicKeys>> getAllKeys() {
        List<UserPublicKeys> keys = keyService.getAllKeys();
        return ResponseEntity.ok(keys);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadKey(@PathVariable Long id) throws Exception {
        Path keyFile = keyService.getKeyFile(id);

        Resource resource = new UrlResource(keyFile.toUri());
        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + keyFile.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }
}