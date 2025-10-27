package com.example.demo.controller;

import com.example.demo.entity.Certificate;
import com.example.demo.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/cert")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @PostMapping
    public ResponseEntity<Certificate> addCertificate(@RequestParam String studentId, @RequestParam String password) throws Exception {
        Certificate cert = certificateService.create(studentId, password);
        return new ResponseEntity<>(cert, HttpStatus.CREATED);
    }

    @PostMapping("/saveFile")
    public ResponseEntity<Path> saveFile(@RequestParam String studentId) throws Exception {
        Path tempFile = certificateService.exportP12ToFile(studentId);
        return ResponseEntity.ok(tempFile);
    }
}
