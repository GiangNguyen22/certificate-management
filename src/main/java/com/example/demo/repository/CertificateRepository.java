package com.example.demo.repository;

import com.example.demo.entity.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findByStudentId(String studentId);
    Optional<Certificate> findByCertId(String certId);

    List<Certificate> findByStatus(String status);

    Page<Certificate> findByStudentId(String studentCode, Pageable pageable);
    
    // SỬA LẠI: Thay vì trả về Certificate, trả về Optional<Certificate>
 @Query("SELECT c FROM Certificate c WHERE TRIM(c.certId) = TRIM(:certId) AND TRIM(c.studentId) = TRIM(:studentId)")
    Optional<Certificate> findByCertIdAndStudentId(@Param("certId") String certId, 
                                                   @Param("studentId") String studentId);    
    
    @Query("SELECT c FROM Certificate c WHERE c.certId LIKE %:certId% AND c.studentId LIKE %:studentId%")
    Certificate findByCertIdContainingAndStudentIdContaining(@Param("certId") String certId, @Param("studentId") String studentId);

    boolean existsByStudentId(String studentCode);

    void deleteByStudentId(String studentCode);
}