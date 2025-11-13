package com.example.demo.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ResultId implements Serializable {
    private String studentCode;
    private String courseCode;

    public ResultId() {
    }

    public ResultId(String studentCode, String courseCode) {
        this.studentCode = studentCode;
        this.courseCode = courseCode;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
