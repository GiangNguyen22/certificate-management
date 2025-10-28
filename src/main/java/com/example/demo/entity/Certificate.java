package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String certId;
    private String templateId;
    private String studentId;
    private String issued_at;
    private String expire_at;
    private String status;
    private String serial_no;   // vd: 2025_IT_01234
    private String certificate; // Base64
    private String alias; //ten dinh danh cho certificate, mac dinh cho la studentCode di
    private String password; // password for certificate bao ve keystore pkcs12 va private key
    private String pdf_uri;
    private String pdf_sha256;

}
