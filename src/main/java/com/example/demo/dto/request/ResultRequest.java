package com.example.demo.dto.request;

import lombok.Data;

@Data
public class ResultRequest {
    private String studentCode;
    private String courseCode;
    private Double score;
    private String grade;
    private String semester;
    private String timeStudied;
}