package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
public class UserRole {
 @DBRef
        private Role role;
        
        private String roleName; // Denormalized
        
        private LocalDateTime assignedAt;
        
        @DBRef
        private User assignedBy;
        
        private LocalDateTime expiresAt;
}
