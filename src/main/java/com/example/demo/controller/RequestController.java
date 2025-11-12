package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.VerifyResult;
import com.example.demo.dto.request.InfoEechStudentInResSign;
import com.example.demo.dto.request.SignCertRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.repository.UserPubKeysRepository;
import com.example.demo.utils.DigitalSignatureUtil;
import com.example.demo.utils.ProcessSignUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.utils.TempFileUtil;

import jakarta.validation.Valid;

import com.example.demo.service.CertificateRequestService;

//import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {
    @Autowired
    CertificateRequestService certRequestService;

    // Giam doc thuc hien ky VAN BANG, CHUNG CHI
    @PostMapping(value = "/sign-certificate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signCertificate(
            @Valid @ModelAttribute SignCertRequest signCertRequest) {
        String p12FilePath = null;
                try {
            MultipartFile p12File = signCertRequest.getP12File();
            p12FilePath = TempFileUtil.saveTempFile(p12File);
            String templateId = signCertRequest.getTemplateId();
            String staffCode = signCertRequest.getStaffCode();
            String keyStorePassword = signCertRequest.getKeystorePass();
        
            List<InfoEechStudentInResSign> students = signCertRequest.getStudents();
            for (InfoEechStudentInResSign student : students) {
                ProcessSignUtil.completeSign(
                    templateId, student.getStudentCode(),
                    staffCode, p12FilePath,
                    keyStorePassword,staffCode, student                    

                );
            }
            TempFileUtil.deleteTempFile(p12FilePath);
           
            ApiResponse response = new ApiResponse(true, "SUCCESS",
                    "All certificates signed successfully", null);
            return ResponseEntity.ok(response);
                }
                catch (Exception e) {
                    ApiResponse response = new ApiResponse(false, "ERROR", 
                        e.getMessage(), null);
                    return ResponseEntity.status(500).body(response);
                }
            }
    // Hoc sinh tao yeu cau lay Van Bang gui cho Giam Doc ky
    /* 
        @PostMapping("/{studentcode}/signrequest")
    public ResponseEntity<?> signRequest(@PathVariable("studentcode") String studentCode,
            @RequestBody(required = false) Map<String, String> body) {
        // Kiểm tra body
        if (body == null || !body.containsKey("templateId") || !body.containsKey("type")) {
            ApiResponse error = new ApiResponse(false, "BAD_REQUEST", "Missing required fields: templateId and type");
            return ResponseEntity.badRequest().body(error);
        } else {
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

                ApiResponse response = new ApiResponse(true, "SUCCESS", "Certificate request signed successfully",
                        data);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error signing certificate request: " + e.getMessage());
            }
        }

    }
    */
 

    @Autowired
    UserPubKeysRepository userPKRepo;

   @PostMapping(value = "/{studentcode}/verifydiploma", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> verifyDiploma(
        @PathVariable("studentcode") String studentCode,
        @RequestParam("diplomaFile") MultipartFile diplomaFile) {
    
    String pathtempFile = null;
    try {
        pathtempFile = TempFileUtil.saveTempFile(diplomaFile);
        VerifyResult verifyResult = DigitalSignatureUtil.verifySignature(pathtempFile, userPKRepo);
        
        ApiResponse response = new ApiResponse(true, "SUCCESS", 
            "Xác minh thành công", verifyResult);
        return ResponseEntity.ok(response);
        
    } catch (Exception e) {
        ApiResponse response = new ApiResponse(false, "ERROR", 
            "Lỗi xác minh: " + e.getMessage(), null);
        return ResponseEntity.status(500).body(response);
    } finally {
        if (pathtempFile != null) {
            TempFileUtil.deleteTempFile(pathtempFile);
        }
    }
}
}