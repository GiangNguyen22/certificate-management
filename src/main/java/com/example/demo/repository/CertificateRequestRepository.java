package com.example.demo.repository;

import com.example.demo.entity.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {

    List<CertificateRequest> findByStudentRequestId(String studentId);
    List<CertificateRequest> findByStatus(String status);
    // @Query("SELECT r.status FROM CertificateRequest r WHERE r.id = :requestId")
    // String findStatusById(@Param("requestId") Long requestId);
    Optional<CertificateRequest> findByRequestCode(String requestCode);
     @Query("SELECT r.status FROM CertificateRequest r WHERE r.requestCode = :requestCode")
    String findStatusByRequestCode(@Param("requestCode") String requestCode);

    boolean existsByStudentRequestId(String studentCode);

    void deleteByStudentRequestId(String studentCode);
}