package com.example.demo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    // Custom query methods (if needed) can be defined here
    Staff findByStaffCode(String staffCode);
    java.util.Optional<Staff> findByUsername(String username);
}
