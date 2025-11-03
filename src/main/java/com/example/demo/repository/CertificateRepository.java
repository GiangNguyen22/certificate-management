package com.example.demo.repository;

import com.example.demo.entity.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByStudentId(String studentId);
    Optional<Certificate> findByCertId(String certId);

    List<Certificate> findByStatus(String status);

    Page<Certificate> findByStudentId(String studentId, Pageable pageable);

}
