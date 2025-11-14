package com.example.demo.controller;

import com.example.demo.dto.request.CourseRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Course;
import com.example.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllCourses(Pageable pageable) {
        ApiResponse response = new ApiResponse();
        response.setData(courseService.getAllCourses(pageable));
        response.setMessage("Courses retrieved successfully");
        response.setStatus("OK");
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createCourse(@RequestBody CourseRequest request) {
        ApiResponse response = new ApiResponse();
        Course saveCourse = courseService.createCourse(request);
        response.setMessage("Course created successfully");
        response.setStatus("OK");
        response.setSuccess(true);
        response.setData(saveCourse);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{courseCode}/update")
    public ResponseEntity<ApiResponse> updateCourse(@PathVariable String courseCode, @RequestBody CourseRequest request) {
        ApiResponse response = new ApiResponse();
        Course updatedCourse = courseService.updateCourse(courseCode, request);
        response.setMessage("Course updated successfully");
        response.setSuccess(true);
        response.setStatus("OK");
        response.setData(updatedCourse);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{courseCode}/delete")
    public ResponseEntity<ApiResponse> deleteCourse(@PathVariable String courseCode) {
        ApiResponse response = new ApiResponse();
        courseService.deleteCourse(courseCode);
        response.setMessage("Course deleted successfully");
        response.setSuccess(true);
        response.setStatus("OK");
        return ResponseEntity.ok(response);
    }



}
