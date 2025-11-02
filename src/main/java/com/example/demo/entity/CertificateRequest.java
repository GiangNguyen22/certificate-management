package com.example.demo.entity;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "certificate_request")
public class CertificateRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "request_code", unique = true, nullable = false)
    private String requestCode;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
     
    @Column(name = "status", nullable = false)
    private String status;


    @Column(name = "student_id", nullable = false)
    private String studentRequestId;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_code")
    private String studentCode;

    @ManyToOne
    @JoinColumn(name = "staff_approved_id")
    private Staff staffApproved;

    @Column(name = "staff_approved_id", insertable = false, updatable = false)
    private Long staffApprovedId;

    @ManyToOne
    @JoinColumn(name = "director_approved_id")
    private Staff directorApproved;

    @Column(name = "director_approved_id", insertable = false, updatable = false)
    private Long directorApprovedId;

    @Column(name = "admin_notes")
    private String adminNotes;

    @Column(name = "director_notes")
    private String directorNotes;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "director_reviewed_at")
    private LocalDateTime directorReviewedAt;

}