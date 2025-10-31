package com.example.demo.entity;

import jakarta.persistence.*;
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
public class Staff extends User {

    private String name;
    @Column(name="staff_code", unique = true, nullable = false)
    private String staffCode;
    private String position;
    @Column(name = "major_name")
    private String majorName;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    
}

