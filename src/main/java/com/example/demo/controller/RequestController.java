package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.utils.ProcessSignUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.utils.TempFileUtil;
import com.example.demo.service.CertificateRequestService;

import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {
    private final CertificateRequestService certificateRequestService;
    
    // Constructor injection for the service
    public RequestController(CertificateRequestService certificateRequestService) {
        this.certificateRequestService = certificateRequestService;
    }
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
    @PostMapping("/{studentcode}/signrequest")
        public ResponseEntity<?> signRequest(@PathVariable("studentcode") String studentCode, @AuthenticationPrincipal User user, @RequestBody(required = false) Map<String, String> body) {
            // Kiểm tra body
        if (body == null || !body.containsKey("templateId") || !body.containsKey("type")) {
            ApiResponse error = new ApiResponse(false, "BAD_REQUEST", "Missing required fields: templateId and type");
            return ResponseEntity.badRequest().body(error);
        }else{
            try {
                
                String templateId = body.get("templateId");
                String requestCode = UUID.randomUUID().toString();
                String type = body.get("type");
                String status = "PENDING";
                certificateRequestService.createRequest(templateId, requestCode, type, status, studentCode);
                // Tạo data dưới dạng Map (thay vì class riêng)
                Map<String, Object> data = new HashMap<>();
                data.put("requestCode", requestCode);
                data.put("studentCode", studentCode);
                data.put("type", type);
                data.put("status", status);

                ApiResponse response = new ApiResponse(true, "SUCCESS", "Certificate request signed successfully", data);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error signing certificate request: " + e.getMessage());
            }
        } 

        }
    }