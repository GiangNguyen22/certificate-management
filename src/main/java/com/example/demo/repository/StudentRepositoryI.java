package com.example.demo.repository;

import com.example.demo.entity.Student;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepositoryI extends JpaRepository<Student, String> {
    Optional<Student> findByStudentCode(String studentCode);
}
