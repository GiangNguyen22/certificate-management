package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity

public class Student extends User {

    @Column(name = "student_code")
    private String studentCode;
    @Column(name = "major_name")
    private String majorName;
    @Column(name="class_name")
    private String className;
    @Column(name="start_year")
    private String startYear;
    private String status;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private List<StudentRequest> requests;

    public Student(UUID id, String username, String password, String fullName, String email, String phone, boolean status, int departmentId, Set<Role> roles, String studentCode, String majorName, String className, String startYear, String status1) {
        super(id, username, password, fullName, email, phone, status, departmentId, roles);
        this.studentCode = studentCode;
        this.majorName = majorName;
        this.className = className;
        this.startYear = startYear;
        this.status = status1;
    }
}
