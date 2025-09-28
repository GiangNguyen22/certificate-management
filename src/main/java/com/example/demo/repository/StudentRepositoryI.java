package com.example.demo.repository;

import com.example.demo.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepositoryI extends MongoRepository<Student, String> {
    Optional<Student> findByStudentCode(String studentCode);
}
