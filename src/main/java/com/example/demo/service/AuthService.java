package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Role;
import com.example.demo.entity.Student;
import com.example.demo.entity.Staff;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.StudentRepositoryI;
import com.example.demo.repository.StaffRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepositoryI studentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public User register(RegisterRequest request) throws Exception {
    // Chuẩn hóa role từ frontend (có thể gửi "student", "staff", "admin")
    String role = request.getRole().toUpperCase(); // "student" -> "STUDENT"

    // Validate role hợp lệ
    if (!role.equals("STUDENT") && !role.equals("STAFF") && !role.equals("ADMIN")) {
        throw new Exception("Role không hợp lệ: " + request.getRole());
    }

    // Check if username already exists
    if (userRepository.findByUsername(request.getUsername()) != null) {
        throw new Exception("Username already exists");
    }

    // Check if student code already exists for students
    if ("STUDENT".equals(role) && request.getStudentCode() != null) {
        if (studentRepository.findByStudentCode(request.getStudentCode()).isPresent()) {
            throw new Exception("Student code already exists");
        }
    }

    // Check if staff code already exists for staff
    if ("STAFF".equals(role) && request.getStaffCode() != null) {
        if (staffRepository.findByStaffCode(request.getStaffCode()).isPresent()) {
            throw new Exception("Staff code already exists");
        }
    }

    User user = null;

    if ("STUDENT".equals(role)) {
        Student student = new Student();
        student.setUsername(request.getUsername());
        student.setPassword(passwordEncoder.encode(request.getPassword()));
        student.setFullName(request.getName());
        student.setEmail(request.getEmail());
        student.setDob(request.getDob());
        student.setStudentCode(request.getStudentCode());
        student.setMajorName(request.getMajorName());
        student.setStartYear(request.getYear());
        student.setXepLoai(request.getXepLoai()); 
        student.setGpa(0.0);
        student.setPassedEnglish(false);
        student.setStatusSV("ACTIVE");
        student.setStatus(true);
        student.setDepartmentId(1); 

        Set<Role> roles = new HashSet<>();
        Role studentRole = roleRepository.findByName(role)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(role);
                    newRole.setDescription("Student Role");
                    return roleRepository.save(newRole);
                });
        roles.add(studentRole);
        student.setRoles(roles);

        user = studentRepository.save(student);
        System.out.println("Student registered successfully: " + user.getUsername());

    } else if ("STAFF".equals(role)) {
        Staff staff = new Staff();
        staff.setUsername(request.getUsername());
        staff.setPassword(passwordEncoder.encode(request.getPassword()));
        staff.setFullName(request.getName());
        staff.setEmail(request.getEmail());
        staff.setDob(request.getDob());
        staff.setStaffCode(request.getStaffCode());
        staff.setName(request.getName());
        staff.setStatus(true);
        staff.setDepartmentId(1); 

        Set<Role> roles = new HashSet<>();
        Role staffRole = roleRepository.findByName(role)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(role);
                    newRole.setDescription("Staff Role");
                    return roleRepository.save(newRole);
                });
        roles.add(staffRole);
        staff.setRoles(roles);

        user = staffRepository.save(staff);
        System.out.println("Staff registered successfully: " + user.getUsername());

    } else if ("ADMIN".equals(role)) {
        User admin = new User();
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setFullName(request.getName());
        admin.setEmail(request.getEmail());
        admin.setDob(request.getDob());
        admin.setStatus(true);
        admin.setDepartmentId(1); 

        Set<Role> roles = new HashSet<>();
        Role adminRole = roleRepository.findByName(role)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(role);
                    newRole.setDescription("Admin Role");
                    return roleRepository.save(newRole);
                });
        roles.add(adminRole);
        admin.setRoles(roles);

        user = userRepository.save(admin);
        System.out.println("Admin registered successfully: " + user.getUsername());
    }

    return user;
}

    
    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        // Check if user account is active (status = true)
        if (!user.isStatus()) {
            throw new RuntimeException("Account is inactive. Please contact administrator.");
        }
        
        return user;
    }

    // CustomUserDetailsService is redundant since AuthService already implements UserDetailsService
    // Remove this method and use CustomUserDetailsService directly in SecurityConfig
}