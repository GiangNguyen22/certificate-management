package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.utils.ProcessSignUtil;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.utils.TempFileUtil;


@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {
    @PostMapping("/sign")
    public ResponseEntity<?> signRequest(@RequestParam String studentcode, @RequestParam String staffcode, @RequestParam MultipartFile p12File,@RequestParam String alias, @RequestParam String keystorepass) {
        try{

            String pathP12 = TempFileUtil.saveTempFile(p12File);
            String signedPath = ProcessSignUtil.completeSign(studentcode, staffcode, pathP12, keystorepass, alias);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("signedFilePath", signedPath);
            System.out.println("✅ Request signed successfully. Signed file path: " + signedPath);
            return ResponseEntity.ok(response);


            
    
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error signing request: " + e.getMessage());
            return ResponseEntity.status(500).body("Error signing request: " + e.getMessage());
        }
        
    }
    
}
