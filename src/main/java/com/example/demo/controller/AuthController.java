package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import com.example.demo.service.CustomUserDetailsService;
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

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) throws Exception {
        User user = authService.register(request);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        AuthResponse response = new AuthResponse();
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUsername(user.getUsername());
        response.setRole(user.getRoles().iterator().next().getName());

        // Map user data properly for frontend
        if (user instanceof com.example.demo.entity.Student) {
            com.example.demo.entity.Student student = (com.example.demo.entity.Student) user;
            response.setUserId(student.getId());
            response.setUser(student);
            response.setStudentCode(student.getStudentCode());
            response.setStartYear(student.getStartYear());
            response.setXepLoai(student.getXepLoai());
        } else if (user instanceof com.example.demo.entity.Staff) {
            com.example.demo.entity.Staff staff = (com.example.demo.entity.Staff) user;
            response.setUserId(staff.getId());
            response.setUser(staff);
            response.setStaffCode(staff.getStaffCode());
        } else {
            response.setUserId(user.getId());
            response.setUser(user);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        User user = authService.authenticate(request.getUsername(), request.getPassword());
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        AuthResponse response = new AuthResponse();
        response.setToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setUsername(user.getUsername());
        response.setRole(user.getRoles().iterator().next().getName());

        // Map user data properly for frontend
        if (user instanceof com.example.demo.entity.Student) {
            com.example.demo.entity.Student student = (com.example.demo.entity.Student) user;
            response.setUserId(student.getId());
            response.setUser(student);
            response.setStudentCode(student.getStudentCode());
            response.setStartYear(student.getStartYear());
            response.setXepLoai(student.getXepLoai());
        } else if (user instanceof com.example.demo.entity.Staff) {
            com.example.demo.entity.Staff staff = (com.example.demo.entity.Staff) user;
            response.setUserId(staff.getId());
            response.setUser(staff);
            response.setStaffCode(staff.getStaffCode());
        } else {
            response.setUserId(user.getId());
            response.setUser(user);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);

        if (username != null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

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