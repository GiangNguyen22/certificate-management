package com.example.demo.repository;

import com.example.demo.entity.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {

    List<CertificateRequest> findByStudentRequestId(String studentId);
    List<CertificateRequest> findByStatus(String status);
    @Query("SELECT r.status FROM CertificateRequest r WHERE r.id = :requestId")
    String findStatusById(@Param("requestId") Long requestId);
    // @Query("SELECT cr FROM CertificateRequest cr WHERE cr.student.id = :studentId ORDER BY cr.createdAt DESC")
    // List<CertificateRequest> findByStudentId(@Param("studentId") Long studentId);

    // @Query("SELECT cr FROM CertificateRequest cr ORDER BY cr.createdAt DESC")
    // List<CertificateRequest> findAllOrderByCreatedAtDesc();

    // @Query("SELECT cr FROM CertificateRequest cr WHERE cr.status = :status ORDER BY cr.createdAt DESC")
    // List<CertificateRequest> findByStatus(@Param("status") String status);

    // @Query("SELECT cr FROM CertificateRequest cr WHERE cr.status IN :statuses ORDER BY cr.createdAt DESC")
    // List<CertificateRequest> findByStatuses(@Param("statuses") List<String> statuses);

    // @Query("SELECT cr FROM CertificateRequest cr WHERE cr.student.id = :studentId AND cr.status = :status ORDER BY cr.createdAt DESC")
    // List<CertificateRequest> findByStudentIdAndStatus(@Param("studentId") Long studentId, @Param("status") String status);
    
}