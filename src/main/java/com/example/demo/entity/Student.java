package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;


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
    @Column(name="gpa")
    private Double gpa;
    @Column(name="passed_English")
    private boolean passedEnglish;
    @Column(name="status_sv")
    private String statusSV;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "student")
    private List<StudentRequest> requests;

    // Use Lombok-generated constructors; removed manual constructor that used UUID
}