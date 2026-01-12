    package com.example.demo.controller;

    import com.example.demo.dto.request.CourseRequest;
    import com.example.demo.dto.response.ApiResponse;
    import com.example.demo.entity.Course;
    import com.example.demo.entity.Student;
    import com.example.demo.service.CourseService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.data.domain.Page;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.data.domain.Pageable;

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

        @GetMapping("/{courseCode}")
        public ResponseEntity<ApiResponse> getCourseByCode(@PathVariable String courseCode) {
            ApiResponse response = new ApiResponse();
            try {
                Course course = courseService.getCourseByCode(courseCode);
                if (course != null) {
                    response.setSuccess(true);
                    response.setStatus("OK");
                    response.setMessage("Course retrieved successfully");
                    response.setData(course);
                    return ResponseEntity.ok(response);
                } else {
                    response.setSuccess(false);
                    response.setStatus("NOT_FOUND");
                    response.setMessage("Course not found with code: " + courseCode);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } catch (Exception e) {
                response.setSuccess(false);
                response.setStatus("ERROR");
                response.setMessage("Error retrieving course: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
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
        public ResponseEntity<ApiResponse> updateCourse(@PathVariable String courseCode,
                @RequestBody CourseRequest request) {
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

        @GetMapping("/search")
        public ResponseEntity<ApiResponse> searchCourse(@RequestParam(required = false) String courseCode,
                                                          @RequestParam(required = false) String name,
                                                          Pageable pageable) {
            Page<Course> courses = courseService.searchCourse(courseCode, name,  pageable);
            ApiResponse response = new ApiResponse(true, "Search courses successfully", "SUCCESS", courses);
            return ResponseEntity.ok(response);
        }
    }