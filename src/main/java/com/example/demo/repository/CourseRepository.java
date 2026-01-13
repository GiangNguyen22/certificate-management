package com.example.demo.repository;

import com.example.demo.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

     boolean existsByCourseCode(String courseCode);

    Optional<Course> findByCourseCode(String courseCode);

    @Query("SELECT c FROM Course c")
    Page<Course> getAllCourses(Pageable pageable);

    @Query(value = "SELECT * FROM course c WHERE c.course_code LIKE %:courseCode% OR c.course_name LIKE %:name%", nativeQuery = true)
    Page<Course> searchByCourseCodeOrName(String courseCode, String name, Pageable pageable);

    boolean existsByStaffCode(String staffCode);
}