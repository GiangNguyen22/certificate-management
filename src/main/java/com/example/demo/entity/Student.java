package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;
import java.util.HashSet;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@PrimaryKeyJoinColumn(name = "user_id") 

public class Student extends User {

    @Column(name = "student_code", unique = true)
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
    @Column(name="xep_loai")
    private String xepLoai;


     @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "student")
    private List<StudentRequest> requests;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "student_course",
            joinColumns = @JoinColumn(name = "student_code", referencedColumnName = "student_code"),
            inverseJoinColumns = @JoinColumn(name = "course_code", referencedColumnName = "course_code"))
    private Set<Course> courses = new HashSet<>();
}