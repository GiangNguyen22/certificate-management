// package com.example.demo.controller;

// import com.example.demo.entity.Staff;

// import com.example.demo.entity.UserPublicKeys;
// import com.example.demo.repository.UserRepository;
// import com.example.demo.service.KeyService;
// import com.example.demo.service.interfaces.p12Service;
// import com.example.demo.repository.StaffRepository;
// import org.springframework.core.io.ByteArrayResource;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.UrlResource;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import java.nio.file.Path;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;

// @RestController
// @RequestMapping("/api/keys")
// public class KeyController {

//     private final KeyService keyService;
//     private final p12Service p12service;
//     private final UserRepository userRepository;
//     private final StaffRepository staffRepository;

//     public KeyController(KeyService keyService, p12Service p12service, UserRepository userRepository,
//             StaffRepository staffRepository) {
//         this.keyService = keyService;
//         this.p12service = p12service;
//         this.userRepository = userRepository;
//         this.staffRepository = staffRepository;
//     }

//     @GetMapping
//     public ResponseEntity<List<UserPublicKeys>> getAllKeys() {
//         List<UserPublicKeys> keys = keyService.getAllKeys();
//         return ResponseEntity.ok(keys);
//     }

//     @GetMapping("/staff-codes")
//     public ResponseEntity<List<Map<String, String>>> getStaffCodes() {
//         List<Staff> staffList = staffRepository.findAll();
//         List<Map<String, String>> staffCodes = staffList.stream()
//                 .map(staff -> {
//                     Map<String, String> map = new java.util.HashMap<>();
//                     map.put("code", staff.getStaffCode());
//                     map.put("name", staff.getName() != null ? staff.getName() : "N/A");
//                     map.put("position", staff.getPosition() != null ? staff.getPosition() : "N/A");
//                     return map;
//                 })
//                 .collect(java.util.stream.Collectors.toList());
//         return ResponseEntity.ok(staffCodes);
//     }

//     // tạo cho 1 staff
//     @PostMapping("/generate/{staffCode}")
//     public ResponseEntity<ByteArrayResource> generateP12ForStaff(@PathVariable String staffCode) {
//         try {
//             String normalizedCode = staffCode.trim().toUpperCase();

//             System.out.println("🔍 Input staffCode = [" + normalizedCode + "]");
//             System.out.println("📋 All staff in DB:");
//             List<Staff> allStaff = staffRepository.findAll();
//             System.out.println("Total staff found: " + allStaff.size());
//             allStaff.forEach(s -> System.out.println(" - [" + s.getStaffCode() + "] - user_id: " + s.getId()));

//             Optional<Staff> staffOpt = staffRepository.findByStaffCode(normalizedCode);
//             System.out.println("Query result for '" + normalizedCode + "': " + staffOpt.isPresent());
//             if (staffOpt.isPresent()) {
//                 System.out.println("Found staff: " + staffOpt.get().getStaffCode());
//             }

//             Staff staff = staffOpt.orElseThrow(() -> new RuntimeException("Staff not found with code: " + normalizedCode));

//             ByteArrayResource resource = p12service.generateP12(staff.getStaffCode());
//             String filename = staff.getStaffCode() + ".p12";

//             return ResponseEntity.ok()
//                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                     .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                     .body(resource);

//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     @PostMapping("/generate/id/{userId}")
//     public ResponseEntity<ByteArrayResource> generateP12ForStaffById(@PathVariable Long userId) {
//         try {
//             System.out.println("🔍 Looking for staff with user_id = " + userId);

//             Optional<Staff> staffOpt = staffRepository.findById(userId);
//             if (!staffOpt.isPresent()) {
//                 throw new RuntimeException("Staff not found with user_id: " + userId);
//             }

//             Staff staff = staffOpt.get();
//             System.out.println("Found staff: " + staff.getStaffCode() + " with user_id: " + staff.getId());

//             ByteArrayResource resource = p12service.generateP12(staff.getStaffCode());
//             String filename = staff.getStaffCode() + ".p12";

//             return ResponseEntity.ok()
//                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
//                     .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                     .body(resource);

//         } catch (Exception e) {
//             e.printStackTrace();
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     @GetMapping("/{id}/download")
//     public ResponseEntity<Resource> downloadKey(@PathVariable Long id) throws Exception {
//         Path keyFile = keyService.getKeyFile(id);

//         Resource resource = new UrlResource(keyFile.toUri());
//         if (resource.exists() || resource.isReadable()) {
//             return ResponseEntity.ok()
//                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + keyFile.getFileName() + "\"")
//                     .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                     .body(resource);
//         } else {
//             throw new RuntimeException("Could not read the file!");
//         }
//     }
// }
package com.example.demo.controller;

import com.example.demo.entity.Staff;

import com.example.demo.entity.UserPublicKeys;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.KeyService;
import com.example.demo.service.interfaces.p12Service;
import com.example.demo.repository.StaffRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/keys")
public class KeyController {

    private final KeyService keyService;
    private final p12Service p12service;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    public KeyController(KeyService keyService, p12Service p12service, UserRepository userRepository,
            StaffRepository staffRepository) {
        this.keyService = keyService;
        this.p12service = p12service;
        this.userRepository = userRepository;
        this.staffRepository = staffRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserPublicKeys>> getAllKeys() {
        List<UserPublicKeys> keys = keyService.getAllKeys();
        return ResponseEntity.ok(keys);
    }

    @GetMapping("/staff-codes")
    public ResponseEntity<List<Map<String, String>>> getStaffCodes() {
        List<Staff> staffList = staffRepository.findAll();
        List<Map<String, String>> staffCodes = staffList.stream()
                .map(staff -> {
                    Map<String, String> map = new java.util.HashMap<>();
                    map.put("code", staff.getStaffCode());
                    map.put("name", staff.getName() != null ? staff.getName() : "N/A");
                    map.put("position", staff.getPosition() != null ? staff.getPosition() : "N/A");
                    return map;
                })
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(staffCodes);
    }

    // tạo cho 1 staff
    // KeyController.java (thêm method)
    @PostMapping("/generate/{staffCode}")
    public ResponseEntity<ByteArrayResource> generateP12ForStaff(@PathVariable String staffCode) {
        if(staffCode == null || staffCode.isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        try {
            ByteArrayResource resource = p12service.generateP12(staffCode);
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

    @PostMapping("/generate/id/{userId}")
    public ResponseEntity<ByteArrayResource> generateP12ForStaffById(@PathVariable Long userId) {
        try {
            System.out.println("🔍 Looking for staff with user_id = " + userId);

            Optional<Staff> staffOpt = staffRepository.findById(userId);
            if (!staffOpt.isPresent()) {
                throw new RuntimeException("Staff not found with user_id: " + userId);
            }

            Staff staff = staffOpt.get();
            System.out.println("Found staff: " + staff.getStaffCode() + " with user_id: " + staff.getId());

            ByteArrayResource resource = p12service.generateP12(staff.getStaffCode());
            String filename = staff.getStaffCode() + ".p12";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadKey(@PathVariable Long id) throws Exception {
        Path keyFile = keyService.getKeyFile(id);

        Resource resource = new UrlResource(keyFile.toUri());
        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + keyFile.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }
}