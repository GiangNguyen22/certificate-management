package com.example.demo.controller;

import com.example.demo.dto.ResultDTO;
import com.example.demo.dto.request.ResultRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Result;
import com.example.demo.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createResult(@RequestBody ResultRequest resultRequest) {
        ApiResponse response = new ApiResponse();
        Result result = resultService.createResult(resultRequest);
        response.setSuccess(true);
        response.setStatus("OK");
        response.setMessage("Create result successfully");
        response.setData(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getResults(Pageable pageable) {
        ApiResponse response = new ApiResponse();
        Page<ResultDTO> results =  resultService.getResults(pageable);
        response.setSuccess(true);
        response.setStatus("OK");
        response.setMessage("Get results successfully");
        response.setData(results);
        return ResponseEntity.ok(response);
    }

     @GetMapping("/student/{studentCode}/courses")
    public ResponseEntity<ApiResponse> getStudentCourses(@PathVariable String studentCode) {
        ApiResponse response = new ApiResponse();
        try {
            List<ResultDTO> courses = resultService.getCoursesByStudentCode(studentCode);
            response.setSuccess(true);
            response.setStatus("OK");
            response.setMessage("Get student courses successfully");
            response.setData(courses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setStatus("ERROR");
            response.setMessage("Error getting student courses: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/course/{courseCode}/students")
public ResponseEntity<ApiResponse> getStudentsByCourseCode(@PathVariable String courseCode) {
    ApiResponse response = new ApiResponse();
    try {
        List<ResultDTO> students = resultService.getStudentsByCourseCode(courseCode);
        response.setSuccess(true);
        response.setStatus("OK");
        response.setMessage("Students retrieved successfully");
        response.setData(students);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.setSuccess(false);
        response.setStatus("ERROR");
        response.setMessage("Error retrieving students: " + e.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}
}