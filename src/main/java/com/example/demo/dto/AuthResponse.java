package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String username;
    private String role;
    private User user;
}