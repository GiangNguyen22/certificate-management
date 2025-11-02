package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.VerifyResult;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserPubKeysRepository;
import com.example.demo.utils.DigitalSignatureUtil;
import com.example.demo.utils.TempFileUtil;

import ch.qos.logback.core.util.FileUtil;

@RestController
@RequestMapping("/api/test-service")
public class Test_Service {
    @Autowired
    UserPubKeysRepository userPKRepo;

    @PostMapping("/pdf-extract")
    public ResponseEntity<?> extractPdfSignature(@RequestParam String studentCode,
            @RequestParam MultipartFile diplomaFile) throws Exception {
        String pathtempFile = TempFileUtil.saveTempFile(diplomaFile);
        try {
            System.out.println("✅ Temporary file saved at: " + pathtempFile);
            VerifyResult verifyResult = DigitalSignatureUtil.verifySignature(pathtempFile, userPKRepo);
            ApiResponse response = new ApiResponse(true, "SUCCESS", "Certificate verify successfully", verifyResult);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error extracting signature: " + e.getMessage());
        } finally {
            TempFileUtil.deleteTempFile(pathtempFile);
        }

    }
}
