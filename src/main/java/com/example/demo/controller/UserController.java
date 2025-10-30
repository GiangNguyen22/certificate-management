package com.example.demo.controller;

import com.example.demo.entity.Staff;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/staff")
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) throws Exception {
        Staff createdStaff = userService.createStaff(staff);
        return ResponseEntity.ok(createdStaff);
    }

    @GetMapping("/staff")
    public ResponseEntity<List<Staff>> getAllStaff() {
        List<Staff> staff = userService.getAllStaff();
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> admins = userService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            Map<String, Object> profile = userService.getUserProfile(username);

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to load user profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @RequestBody Map<String, Object> profileData,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Map<String, Object> updatedProfile = userService.updateUserProfile(username, profileData);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("profile", updatedProfile);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> stats = userService.getUserStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to load user stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}