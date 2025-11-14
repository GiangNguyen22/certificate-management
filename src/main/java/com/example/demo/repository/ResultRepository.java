package com.example.demo.repository;

import com.example.demo.dto.ResultDTO;
import com.example.demo.entity.Result;
import com.example.demo.entity.ResultId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

        @Query(value = "SELECT " +
                        "r.student_code AS studentCode, " +
                        "u.full_name AS fullName, " +
                        "s.major_name AS majorName, " +
                        "s.class_name AS className, " +
                        "u.department_id AS departmentId, " +
                        "s.passed_english AS passedEnglish, " +
                        "s.gpa AS gpa, " +
                        "u.email AS email, " +
                        "r.course_code AS courseCode, " +
                        "r.score AS score, " +
                        "r.grade AS grade, " +
                        "r.semester AS semester, " +
                        "r.time_studied AS timeStudied, " +
                        "c.course_name AS courseName " +
                        "FROM result r " +
                        "INNER JOIN student s ON r.student_code = s.student_code " +
                        "INNER JOIN course c ON r.course_code = c.course_code " +
                        "INNER JOIN user u ON s.user_id = u.id", countQuery = "SELECT COUNT(*) FROM result r " +
                                        "INNER JOIN student s ON r.student_code = s.student_code " +
                                        "INNER JOIN course c ON r.course_code = c.course_code " +
                                        "INNER JOIN user u ON s.user_id = u.id", nativeQuery = true)
        Page<ResultDTO> getResultList(Pageable pageable);

        @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
                        "FROM Result r WHERE r.course.courseCode = ?1 AND r.student.studentCode = ?2")
        boolean existsByCourseCodeAndStudentCode(String courseCode, String studentCode);

        @Query("SELECT r.student.studentCode as studentCode, " +
                        "r.student.fullName as fullName, " +
                        "r.student.majorName as majorName, " +
                        "r.student.className as className, " +
                        "r.student.departmentId as departmentId, " +
                        "r.student.passedEnglish as passedEnglish, " +
                        "r.student.gpa as gpa, " +
                        "r.student.email as email, " +
                        "r.course.courseCode as courseCode, " +
                        "r.score as score, " +
                        "r.grade as grade, " +
                        "r.semester as semester, " +
                        "r.course.courseName as courseName, " +
                        "r.timeStudied as timeStudied " +
                        "FROM Result r " +
                        "WHERE r.student.studentCode = :studentCode")
        List<ResultDTO> findByStudentStudentCode(String studentCode);

        @Query(value = "SELECT " +
                        "r.student_code AS studentCode, " +
                        "u.full_name AS fullName, " +
                        "s.major_name AS majorName, " +
                        "s.class_name AS className, " +
                        "r.score AS score, " +
                        "r.grade AS grade, " +
                        "r.semester AS semester, " +
                        "r.time_studied AS timeStudied " +
                        "FROM result r " +
                        "INNER JOIN student s ON r.student_code = s.student_code " +
                        "INNER JOIN user u ON s.user_id = u.id " +
                        "WHERE r.course_code = :courseCode", nativeQuery = true)
        List<ResultDTO> findByCourseCourseCode(String courseCode);

}