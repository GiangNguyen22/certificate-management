package com.example.demo.dto.request;


import lombok.Data;

import java.time.LocalDate;

@Data
public class CourseRequest {
    private String courseName;
    private String courseCode;
    private String description;
    private String staffCode;
    private LocalDate startDate;
    private LocalDate endDate;
}
