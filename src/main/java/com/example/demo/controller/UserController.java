package com.example.demo.controller;

import com.example.demo.entity.Staff;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/staff")
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) throws Exception {
        Staff createdStaff = userService.createStaff(staff);
        return ResponseEntity.ok(createdStaff);
    }

    @GetMapping("/staff")
    public ResponseEntity<List<Staff>> getAllStaff() {
        List<Staff> staff = userService.getAllStaff();
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> admins = userService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }
}