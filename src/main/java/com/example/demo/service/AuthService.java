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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService implements UserDetailsService {

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

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public User register(RegisterRequest request) throws Exception {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new Exception("Username already exists");
        }

        User user = null;

        if ("student".equals(request.getRole())) {
            Student student = new Student();
            student.setUsername(request.getUsername());
            student.setPassword(passwordEncoder.encode(request.getPassword()));
            student.setFullName(request.getName());
            student.setEmail(request.getEmail());
            student.setDob(request.getDob());
            student.setStudentCode(request.getStudentCode());
            student.setMajorName(request.getMajorName());
            student.setStartYear(request.getYear());
            student.setGpa(0.0); // Default GPA
            student.setPassedEnglish(false); // Default
            student.setStatusSV("ACTIVE"); // Default
            student.setStatus(true);
            student.setDepartmentId(1); // Default department

            // Set roles
            Set<Role> roles = new HashSet<>();
            Role studentRole = roleRepository.findByName("STUDENT");
            if (studentRole == null) {
                studentRole = new Role();
                studentRole.setName("STUDENT");
                studentRole.setDescription("Student Role");
                roleRepository.save(studentRole);
            }
            roles.add(studentRole);
            student.setRoles(roles);

            user = studentRepository.save(student);
        } else if ("staff".equals(request.getRole())) {
            Staff staff = new Staff();
            staff.setUsername(request.getUsername());
            staff.setPassword(passwordEncoder.encode(request.getPassword()));
            staff.setFullName(request.getName());
            staff.setEmail(request.getEmail());
            staff.setDob(request.getDob());
            staff.setStaffCode(request.getStaffCode());
            staff.setName(request.getName());
            staff.setStatus(true);
            staff.setDepartmentId(1); // Default department

            // Set roles
            Set<Role> roles = new HashSet<>();
            Role staffRole = roleRepository.findByName("STAFF");
            if (staffRole == null) {
                staffRole = new Role();
                staffRole.setName("STAFF");
                staffRole.setDescription("Staff Role");
                roleRepository.save(staffRole);
            }
            roles.add(staffRole);
            staff.setRoles(roles);

            user = staffRepository.save(staff);
        } else if ("admin".equals(request.getRole())) {
            User admin = new User();
            admin.setUsername(request.getUsername());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setFullName(request.getName());
            admin.setEmail(request.getEmail());
            admin.setDob(request.getDob());
            admin.setStatus(true);
            admin.setDepartmentId(1); // Default department

            // Set roles
            Set<Role> roles = new HashSet<>();
            Role adminRole = roleRepository.findByName("ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setName("ADMIN");
                adminRole.setDescription("Admin Role");
                roleRepository.save(adminRole);
            }
            roles.add(adminRole);
            admin.setRoles(roles);

            user = userRepository.save(admin);
        }

        return user;
    }

    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return customUserDetailsService.loadUserByUsername(username);
    }
}