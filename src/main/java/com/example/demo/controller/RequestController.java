package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.VerifyResult;
import com.example.demo.dto.request.CertificateSignDTO;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserPubKeysRepository;
import com.example.demo.utils.DigitalSignatureUtil;
import com.example.demo.utils.ProcessSignUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.utils.TempFileUtil;

import jakarta.validation.Valid;

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
    @Autowired
    CertificateRequestService certRequestService;
    //Giam doc thuc hien ky yeu cau van bang cua hoc sinh
    @PostMapping(value = "/sign", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signRequest(@Valid @ModelAttribute CertificateSignDTO requestDTO) {
        String p12Path = null;
        System.out.println("Request Code: " + requestDTO.getRequestCode());
        System.out.println("Student Code: " + requestDTO.getStudentCode());
        System.out.println("Staff Code: " + requestDTO.getStaffCode());
        System.out.println("Keystore Password: " + requestDTO.getKeystorePass());
        System.out.println("Alias: " + requestDTO.getAlias());
        
        String statusRequest = certRequestService.findStatusByRequestCode(requestDTO.getRequestCode());
        if("APPROVED".equals(statusRequest)){
                    try{
            MultipartFile p12File = requestDTO.getP12File();
            p12Path = TempFileUtil.saveTempFile(p12File);
            String signedPath = ProcessSignUtil.completeSign(
                requestDTO.getRequestCode(),
                requestDTO.getStudentCode(),
                requestDTO.getStaffCode(),
                p12Path,
                requestDTO.getKeystorePass(),
                requestDTO.getAlias()

            );
             
            Map<String, String> data = new HashMap<>();
            data.put("signedFilePath", signedPath);
            ApiResponse response = new ApiResponse(true, "SUCCESS", "Request signed successfully", data);

            return ResponseEntity.ok(response);

        }
        catch(Exception e){
            e.printStackTrace();
            ApiResponse response = new ApiResponse(false, "ERROR", "Error signing request: " + e.getMessage(), null);
            return ResponseEntity.status(500).body(response);
        }
        finally{
            if(p12Path != null){
                TempFileUtil.deleteTempFile(p12Path);
            }
        }

        }
        else{
            ApiResponse response = new ApiResponse(false, "INVALID_REQUEST", "Request is INVALID status", null);
            return ResponseEntity.badRequest().body(response);
        }


    }
    //Hoc sinh tao yeu cau lay Van Bang gui cho Giam Doc ky
    @PostMapping("/{studentcode}/signrequest")
        public ResponseEntity<?> signRequest(@PathVariable("studentcode") String studentCode, @RequestBody(required = false) Map<String, String> body) {
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
    // @ostMapping("/{studentcode}/veryfydiploma")}")
    //     public ResponseEntity<?> verifyDiploma(@PathVariable("studentcode") String studentCode, @RequestParam MultipartFile diplomaFile) {
    //         try {
    //             Boolean isValid = certificateRequestService.verifyDiploma(diplomaFile, studentCode);
    //             Map<String, Object> response = new HashMap<>();
    //             response.put("studentCode", studentCode);
    //             response.put("isValid", isValid);

    //             return ResponseEntity.ok(response);
    //         } catch (Exception e) {
    //             e.printStackTrace();
    //             return ResponseEntity.status(500).body("Error verifying diploma: " + e.getMessage());
    //         }
    //     }
    @Autowired
    UserPubKeysRepository userPKRepo;
    @PostMapping("/{studentcode}/verifydiploma")
    public ResponseEntity<?> verifyDiploma(@PathVariable("studentcode") String studentCode, @RequestParam MultipartFile diplomaFile) throws Exception {
        String pathtempFile = TempFileUtil.saveTempFile(diplomaFile);
        try {
            VerifyResult verifyResult = DigitalSignatureUtil.verifySignature(pathtempFile, userPKRepo);
            ApiResponse response = new ApiResponse(true, "SUCCESS", "Certificate verify successfully", verifyResult);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            ApiResponse response = new ApiResponse(false, "ERROR", "Error verifying diploma: " ,null);
            return ResponseEntity.status(500).body(response);
        } finally {
            TempFileUtil.deleteTempFile(pathtempFile);
        }
    }

}