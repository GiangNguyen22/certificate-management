package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role;
    private String name;
    private LocalDate dob;
    private String studentCode;
    private String majorName;
    private String year;
    private String xepLoai;
    private String staffCode;
}