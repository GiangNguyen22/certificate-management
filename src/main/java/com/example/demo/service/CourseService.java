package com.example.demo.service;

import com.example.demo.dto.request.CourseRequest;
import com.example.demo.entity.Course;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final StaffRepository staffRepository;

    public CourseService(CourseRepository courseRepository, StaffRepository staffRepository) {
        this.courseRepository = courseRepository;
        this.staffRepository = staffRepository;
    }

    public Course createCourse(CourseRequest request) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new DuplicateResourceException("Course code already exists: " + request.getCourseCode());
        }
        if(!staffRepository.existsByStaffCode(request.getStaffCode())) {
            throw new DuplicateResourceException("Staff code is not already exists: " + request.getStaffCode());
        }
        Course  course = new Course();
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
}
