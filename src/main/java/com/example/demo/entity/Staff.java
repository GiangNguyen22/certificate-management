package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "staff")
@PrimaryKeyJoinColumn(name = "user_id")
public class Staff extends User {

    private String name;
    @Column(name="staff_code", unique = true, nullable = false)
    private String staffCode;
    private String position;
    private String majorName;
    
}

