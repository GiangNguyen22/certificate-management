package com.example.demo.repository;

import com.example.demo.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;


@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByUserId(Long userId);
    Optional<Certificate> findByCertId(String certId);
}
