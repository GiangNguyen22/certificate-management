package com.example.demo.controller;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.request.ChangePasswordRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Staff;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.service.StudentService;
import com.example.demo.service.UserService;
import org.apache.coyote.BadRequestException;
import org.apache.coyote.Response;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.demo.service.interfaces.p12Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private p12Service p12Service;
    @Autowired
    private StudentService studentService;

    @PostMapping("/staff")
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) throws Exception {
        Staff createdStaff = userService.createStaff(staff);
        return ResponseEntity.ok(createdStaff);
    }

    @PostMapping("/staff/{staffCode}/generate-key")
    public ResponseEntity<ByteArrayResource> generateStaffKey(@PathVariable String staffCode) {
        try {
            ByteArrayResource resource = p12Service.generateP12(staffCode);
            String filename = staffCode + ".p12";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/staff")
    public ResponseEntity<List<Staff>> getAllStaff() {
        List<Staff> staff = userService.getAllStaff();
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/paged")
    public ResponseEntity<ApiResponse> getStudents(Pageable pageable) {
        ApiResponse response = new ApiResponse();
        response.setData(studentService.getAllStudents(pageable));
        response.setMessage("Students retrieved successfully");
        response.setStatus("OK");
        response.setSuccess(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            Student student = userService.getAllStudents().stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Student not found: " + e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> admins = userService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            Map<String, Object> profile = userService.getUserProfile(username);

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to load user profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @RequestBody Map<String, Object> profileData,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            Map<String, Object> updatedProfile = userService.updateUserProfile(username, profileData);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("profile", updatedProfile);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to update profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> stats = userService.getUserStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to load user stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {
        User user = userService.createUserWithDefaultPassword(request);
        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "role", request.getRole()));
    }

    @PutMapping("/student/{studentCode}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse> updateStudent(@PathVariable String studentCode,
            @RequestBody Map<String, Object> updateData) {
        try {
            userService.updateStudentByStudentCode(studentCode, updateData);
            ApiResponse response = new ApiResponse(true, "Update student successfully", "SUCCESS", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Update student failed: " + e.getMessage(), "ERROR", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/student/{studentCode}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteStudent(@PathVariable String studentCode) {
        try {
            userService.deleteStudentByStudentCode(studentCode);
            ApiResponse response = new ApiResponse(true, "Delete student successfully", "SUCCESS", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Delete student failed: " + e.getMessage(), "ERROR", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/staff/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteStaff(@PathVariable String username) {
        try {
            userService.deleteStaffByUsername(username);
            ApiResponse response = new ApiResponse(true, "Delete staff successfully", "SUCCESS", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Delete staff failed: " + e.getMessage(), "ERROR", null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/search")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse> searchStudents(@RequestParam(required = false) String studentCode,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String grade,
            Pageable pageable) {
        Page<Student> students = studentService.searchStudents(studentCode, name, grade, pageable);
        ApiResponse response = new ApiResponse(true, "Search students successfully", "SUCCESS", students);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/student/change-password")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody ChangePasswordRequest request,
            Authentication authentication) throws BadRequestException {
        String username = authentication.getName();
        userService.updatePassword(username, request.getOldPassword(), request.getNewPassword());
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setStatus("OK");
        response.setMessage("Password updated successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/staff/{username}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateStaffStatus(@PathVariable String username,
            @RequestBody Map<String, Object> statusData) {
        try {
            userService.updateStaffStatus(username, (Boolean) statusData.get("status"));
            ApiResponse response = new ApiResponse(true, "Update staff status successfully", "SUCCESS", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Update staff status failed: " + e.getMessage(), "ERROR",
                    null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PatchMapping("/student/{studentCode}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateStudentStatus(@PathVariable String studentCode,
            @RequestBody Map<String, Object> statusData) {
        try {
            userService.updateStudentStatus(studentCode, (Boolean) statusData.get("status"));
            ApiResponse response = new ApiResponse(true, "Update student status successfully", "SUCCESS", null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Update student status failed: " + e.getMessage(), "ERROR",
                    null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{studentCode}/courses/{courseCode}/enroll")
    public ResponseEntity<ApiResponse> enrollStudentInCourse(@PathVariable String studentCode,
            @PathVariable String courseCode) {

        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setStatus("OK");
        response.setData(userService.enrollStudentInCourse(studentCode, courseCode));
        response.setMessage("Student enrolled in course successfully");
        return ResponseEntity.ok(response);

    }

    @GetMapping("student/{studentCode}/courses")
    public ResponseEntity<ApiResponse> getCoursesByStudentCode(@PathVariable String studentCode) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setStatus("OK");
        response.setData(userService.getCoursesByStudentCode(studentCode));
        response.setMessage("Get courses by student code successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/import/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            ApiResponse response = userService.importStudents(file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse errorResponse = new ApiResponse(false, "Import failed: " + e.getMessage(), "ERROR", null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/export/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportStudents() {
        try {
            System.out.println("=== EXPORT STUDENTS CONTROLLER CALLED ===");
            String filePath = userService.exportStudentsToExcel();
            System.out.println("Export completed, file path: " + filePath);

            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            byte[] fileContent = java.nio.file.Files.readAllBytes(path);
            System.out.println("File read successfully, size: " + fileContent.length + " bytes");

            // Delete the file after reading (optional cleanup)
            boolean deleted = java.nio.file.Files.deleteIfExists(path);
            System.out.println("File cleanup: " + (deleted ? "successful" : "failed"));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students_export.xlsx")
                    .header(HttpHeaders.CONTENT_TYPE,
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(fileContent);
        } catch (Exception e) {
            System.err.println("=== EXPORT STUDENTS ERROR ===");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/download-import-errors")
    public ResponseEntity<byte[]> downloadImportErrors(@RequestParam String filePath) {
        try {
            System.out.println("=== DOWNLOAD IMPORT ERRORS ===");
            System.out.println("File path: " + filePath);

            // Decode URL
            String decodedPath = java.net.URLDecoder.decode(filePath, "UTF-8");
            System.out.println("Decoded path: " + decodedPath);

            java.nio.file.Path path = java.nio.file.Paths.get(decodedPath);
            System.out.println("Absolute path: " + path.toAbsolutePath());

            if (!java.nio.file.Files.exists(path)) {
                System.err.println("❌ File not found: " + path.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = java.nio.file.Files.readAllBytes(path);
            String filename = path.getFileName().toString();

            System.out.println("✅ File found: " + filename);
            System.out.println("File size: " + fileContent.length + " bytes");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileContent);

        } catch (Exception e) {
            System.err.println("❌ Download error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
