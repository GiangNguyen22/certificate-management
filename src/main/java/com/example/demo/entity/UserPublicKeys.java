package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "user_public_keys")
public class UserPublicKeys {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staff_code", nullable = false)
    private String staffCode;

    @Lob
    @Column(name = "public_key", columnDefinition = "TEXT", nullable = false)
    private String publicKey;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "cryptography_type")
    private String cryptographyType;
}

    