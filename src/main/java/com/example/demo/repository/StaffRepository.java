package com.example.demo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import com.example.demo.entity.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    // Custom query methods (if needed) can be defined here
    Optional<Staff> findByStaffCode(@Param("staffCode") String staffCode);
    Optional<Staff> findByUsername(String username);
    boolean existsByStaffCode(String staffCode);
}
