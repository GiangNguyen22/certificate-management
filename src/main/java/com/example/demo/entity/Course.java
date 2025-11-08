package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "course_name", nullable = false)
    private String courseName;
    @Column(name = "course_code", nullable = false, unique = true)
    private String courseCode;
    private String description;
    @Column(name = "staff_code", nullable = false)
    private String staffCode;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
}
