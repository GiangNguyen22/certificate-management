package com.example.demo.service;

import com.example.demo.entity.Staff;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.repository.StaffRepository;
import com.example.demo.repository.StudentRepositoryI;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StudentRepositoryI studentRepository;

    @Autowired
    private UserRepository userRepository;

    public Staff createStaff(Staff staff) throws Exception {
        // Check if username already exists (simplified check)
        List<User> existingUsers = userRepository.findAll();
        boolean usernameExists = existingUsers.stream()
                .anyMatch(user -> staff.getUsername().equals(user.getUsername()));

        if (usernameExists) {
            throw new Exception("Username already exists");
        }

        // Set default values
        staff.setStatus(true);
        staff.setDepartmentId(1); // Default department

        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<User> getAllAdmins() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> "ADMIN".equals(role.getName())))
                .toList();
    }
}