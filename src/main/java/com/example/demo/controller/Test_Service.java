package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.VerifyResult;
import com.example.demo.dto.request.CertificateSignDTO;
import com.example.demo.dto.request.InfoEechStudentInResSign;
import com.example.demo.dto.request.SignCertRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.repository.UserPubKeysRepository;
import com.example.demo.utils.DigitalSignatureUtil;
import com.example.demo.utils.ProcessSignUtil;
import com.example.demo.utils.TempFileUtil;

import jakarta.validation.Valid;

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
    
    /*
     * 
     */
    @PostMapping(
    value = "/sign",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
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
}
