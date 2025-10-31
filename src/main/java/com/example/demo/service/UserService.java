package com.example.demo.service;

import com.example.demo.entity.Staff;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.repository.StaffRepository;
import com.example.demo.repository.StudentRepositoryI;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StudentRepositoryI studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Staff createStaff(Staff staff) throws Exception {
        // Check if username already exists (simplified check)
        List<User> existingUsers = userRepository.findAll();
        boolean usernameExists = existingUsers.stream()
                .anyMatch(user -> staff.getUsername().equals(user.getUsername()));

        if (usernameExists) {
            throw new Exception("Username already exists");
        }

        // Encode password if not already encoded
        if (staff.getPassword() != null && !staff.getPassword().startsWith("$2a$")) {
            staff.setPassword(passwordEncoder.encode(staff.getPassword()));
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

    public Map<String, Object> getUserProfile(String username) {
        // Try to find as student first
        Student student = studentRepository.findByUsername(username).orElse(null);
        if (student != null) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", student.getId());
            profile.put("username", student.getUsername());
            profile.put("name", student.getFullName());
            profile.put("email", student.getEmail());
            profile.put("phone", null); // Students may not have phone
            profile.put("role", "STUDENT");
            profile.put("studentCode", student.getStudentCode());
            profile.put("major", student.getMajorName());
            profile.put("createdAt", null);
            profile.put("lastLogin", null);
            return profile;
        }

        // Try to find as staff
        Staff staff = staffRepository.findByUsername(username).orElse(null);
        if (staff != null) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", staff.getId());
            profile.put("username", staff.getUsername());
            profile.put("name", staff.getFullName());
            profile.put("email", staff.getEmail());
            profile.put("phone", staff.getPhone());
            profile.put("role", "STAFF");
            profile.put("staffCode", staff.getStaffCode());
            profile.put("department", "Department " + staff.getDepartmentId()); // Simplified
            profile.put("createdAt", null);
            profile.put("lastLogin", null);
            return profile;
        }

        // Try to find as admin user
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("name", user.getFullName());
            profile.put("email", user.getEmail());
            profile.put("phone", user.getPhone());
            profile.put("role", "ADMIN");
            profile.put("createdAt", null);
            profile.put("lastLogin", null);
            return profile;
        }

        throw new RuntimeException("User not found: " + username);
    }

    public Map<String, Object> updateUserProfile(String username, Map<String, Object> profileData) {
        // Try to find as student first
        Student student = studentRepository.findByUsername(username).orElse(null);
        if (student != null) {
            if (profileData.containsKey("name")) {
                student.setFullName((String) profileData.get("name"));
            }
            if (profileData.containsKey("email")) {
                student.setEmail((String) profileData.get("email"));
            }
            // Note: Students may not have phone field, so we skip it
            studentRepository.save(student);
            return getUserProfile(username);
        }

        // Try to find as staff
        Staff staff = staffRepository.findByUsername(username).orElse(null);
        if (staff != null) {
            if (profileData.containsKey("name")) {
                staff.setFullName((String) profileData.get("name"));
            }
            if (profileData.containsKey("email")) {
                staff.setEmail((String) profileData.get("email"));
            }
            if (profileData.containsKey("phone")) {
                staff.setPhone((String) profileData.get("phone"));
            }
            staffRepository.save(staff);
            return getUserProfile(username);
        }

        // Try to find as admin user
        User user = userRepository.findByUsername(username);
        if (user != null) {
            if (profileData.containsKey("name")) {
                user.setFullName((String) profileData.get("name"));
            }
            if (profileData.containsKey("email")) {
                user.setEmail((String) profileData.get("email"));
            }
            userRepository.save(user);
            return getUserProfile(username);
        }

        throw new RuntimeException("User not found: " + username);
    }

    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count() + staffRepository.count() + studentRepository.count());
        stats.put("totalStudents", studentRepository.count());
        stats.put("totalStaff", staffRepository.count());
        stats.put("totalAdmins", userRepository.count());
        return stats;
    }
}