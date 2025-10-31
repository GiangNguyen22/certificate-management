package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "student_file")
public class StudentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "file_name")
    private String fileName;
    @Column(name="file_path")
    private String filePath;
    private boolean signed;
    @Column(name = "issue_at")
    private LocalDateTime issueAt;
    @OneToOne
    @JoinColumn(name = "student_request_id", referencedColumnName = "id")
    private StudentRequest studentRequest;
}
