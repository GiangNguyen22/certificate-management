package com.example.demo.repository;

import com.example.demo.dto.StaffProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import com.example.demo.entity.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    // Custom query methods (if needed) can be defined here
    Optional<Staff> findByStaffCode(@Param("staffCode") String staffCode);
    Optional<Staff> findByUsername(String username);
    boolean existsByStaffCode(String staffCode);
    @Query(value = "SELECT s.staff_code as staffCode, s.name FROM staff s", nativeQuery = true)
    List<StaffProjection> getStaffList();
}
