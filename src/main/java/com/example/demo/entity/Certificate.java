package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "certificate")
public class Certificate {
    @Id
    private String id;
    private String certId;
    private String templateId;
    private String studentId;
    private String issued_at;
    private String expire_at;
    private String status;
    private String serial_no;   // vd: 2025_IT_01234
    private String certificate; // Base64
    private String pdf_uri;
    private String pdf_sha256;


}
