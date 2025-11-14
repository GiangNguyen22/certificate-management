package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity

public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "cert_id")
    private String certId;
    @Column(name = "template_id")
    private String templateId;
    @Column(name = "student_id")
    private String studentId;
    @Column(name = "user_sign_id")
    private String userSignedId;
    private String issued_at;
    private String expire_at;
    private String status;
    private String serial_no;   
    private String pdf_uri;
    private String pdf_sha256;
    private String course_code;
}

