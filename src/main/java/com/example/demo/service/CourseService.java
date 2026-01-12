package com.example.demo.service;

import com.example.demo.dto.request.CourseRequest;
import com.example.demo.entity.Course;
import com.example.demo.entity.Student;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.StaffRepository;
import com.example.demo.specifications.StudentSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final StaffRepository staffRepository;

    public CourseService(CourseRepository courseRepository, StaffRepository staffRepository) {
        this.courseRepository = courseRepository;
        this.staffRepository = staffRepository;
    }

    public Course getCourseByCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found with code: " + courseCode));
    }

    public Course createCourse(CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new DuplicateResourceException("Course code already exists: " + request.getCourseCode());
        }
        if (!staffRepository.existsByStaffCode(request.getStaffCode())) {
            throw new DuplicateResourceException("Staff code is not already exists: " + request.getStaffCode());
        }
        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setStaffCode(request.getStaffCode());
        course.setStartDate(request.getStartDate());
        course.setEndDate(request.getEndDate());

        return courseRepository.save(course);

    }

    public Course updateCourse(String courseCode, CourseRequest request) {
        Course existingCourse = courseRepository.findByCourseCode((courseCode))
                .orElseThrow(() -> new ResourceNotFoundEx("Course not found with code: " + courseCode));

        Optional.ofNullable(request.getCourseName()).ifPresent(existingCourse::setCourseName);
        Optional.ofNullable(request.getDescription()).ifPresent(existingCourse::setDescription);
        Optional.ofNullable(request.getStaffCode()).ifPresent(existingCourse::setStaffCode);
        Optional.ofNullable(request.getStartDate()).ifPresent(existingCourse::setStartDate);
        Optional.ofNullable(request.getEndDate()).ifPresent(existingCourse::setEndDate);

        return courseRepository.save(existingCourse);
    }

    public void deleteCourse(String courseCode) {
        Course existingCourse = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new ResourceNotFoundEx("Course not found with code: " + courseCode));
        courseRepository.delete(existingCourse);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course findById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundEx("Course not found with id: " + id));
    }

    public Page<Course> getAllCourses(Pageable pageable) {
        return courseRepository.getAllCourses(pageable);
    }

    public Page<Course> searchCourse(String studentCode, String name, Pageable pageable) {
        return courseRepository.searchByCourseCodeOrName(studentCode, name, pageable);
    }
}