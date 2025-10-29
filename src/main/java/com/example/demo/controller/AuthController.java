package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Student;
import com.example.demo.entity.Staff;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) throws Exception {
        User user = authService.register(request);
        UserDetails userDetails = authService.loadUserByUsername(user.getUsername());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        AuthResponse response = new AuthResponse();
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUsername(user.getUsername());
        response.setRole(user.getRoles().iterator().next().getName());
        response.setUser(user);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        User user = authService.authenticate(request.getUsername(), request.getPassword());
        UserDetails userDetails = authService.loadUserByUsername(user.getUsername());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        AuthResponse response = new AuthResponse();
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUsername(user.getUsername());
        response.setRole(user.getRoles().iterator().next().getName());
        response.setUser(user);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            UserDetails userDetails = authService.loadUserByUsername(username);

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String newAccessToken = jwtService.generateAccessToken(userDetails);
                String newRefreshToken = jwtService.generateRefreshToken(userDetails);

                AuthResponse response = new AuthResponse();
                response.setToken(newAccessToken);
                response.setRefreshToken(newRefreshToken);
                response.setUsername(username);
                response.setRole(userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));

                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.badRequest().build();
    }
}