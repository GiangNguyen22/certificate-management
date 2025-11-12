package com.example.demo.repository;

import com.example.demo.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    boolean existsByCourseCode(String courseCode);

    Optional<Course> findByCourseCode(String courseCode);

}
