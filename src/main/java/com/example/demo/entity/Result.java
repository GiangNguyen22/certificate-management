package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data

public class Result {
    @EmbeddedId
    private ResultId resultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentCode")
    @JoinColumn(name="student_code", referencedColumnName = "student_code")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseCode")
    @JoinColumn(name = "course_code", referencedColumnName = "course_code")
    private Course course;

    private Double score;
    private String grade;
    private String semester;
    private String timeStudied;

}
