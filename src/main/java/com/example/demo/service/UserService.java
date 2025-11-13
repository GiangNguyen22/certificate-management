package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.Staff;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.*;
import com.example.demo.service.interfaces.p12Service;
import org.apache.coyote.BadRequestException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

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

    @Autowired
    private p12Service p12Service;
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private CertificateRequestRepository certificateRequestRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DepartmentRepository departmentRepository;


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
        // Normalize username search
        username = username.trim();

        // --- Check student ---
        Student student = studentRepository.findByUsername(username).orElse(null);
        if (student != null) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", student.getId());
            profile.put("username", student.getUsername());
            profile.put("name", student.getFullName() != null ? student.getFullName() : "");
            profile.put("email", student.getEmail() != null ? student.getEmail() : "");
            profile.put("phone", null); // Students may not have phone
            profile.put("role", "STUDENT");
            profile.put("studentCode", student.getStudentCode() != null ? student.getStudentCode() : "");
            profile.put("major", student.getMajorName() != null ? student.getMajorName() : "");
            profile.put("startYear", student.getStartYear() != null ? student.getStartYear() : "");
            profile.put("xepLoai", student.getXepLoai() != null ? student.getXepLoai() : "");
            profile.put("createdAt", null);
            profile.put("lastLogin", null);
            return profile;
        }

        // --- Check staff ---
        Staff staff = staffRepository.findByUsername(username).orElse(null);
        if (staff != null) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", staff.getId());
            profile.put("username", staff.getUsername());
            profile.put("name", staff.getFullName() != null ? staff.getFullName() : "");
            profile.put("email", staff.getEmail() != null ? staff.getEmail() : "");
            profile.put("phone", staff.getPhone() != null ? staff.getPhone() : "");
            profile.put("role", "STAFF");
            profile.put("staffCode", staff.getStaffCode() != null ? staff.getStaffCode() : "");
            profile.put("department", staff.getDepartmentId() > 0 ? "Department " + staff.getDepartmentId() : "");
            profile.put("createdAt", null);
            profile.put("lastLogin", null);
            return profile;
        }

        // --- Check admin ---
        User user = userRepository.findByUsername(username);
        if (user != null && user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()))) {
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("name", user.getFullName() != null ? user.getFullName() : "");
            profile.put("email", user.getEmail() != null ? user.getEmail() : "");
            profile.put("phone", user.getPhone() != null ? user.getPhone() : "");
            profile.put("role", "ADMIN");
            profile.put("createdAt", null);
            profile.put("lastLogin", null);
            return profile;
        }

        throw new RuntimeException("User not found: " + username);
    }

    public Map<String, Object> updateUserProfile(String username, Map<String, Object> profileData) {
        username = username.trim();

        // --- Update student ---
        Student student = studentRepository.findByUsername(username).orElse(null);
        if (student != null) {
            if (profileData.containsKey("name")) student.setFullName((String) profileData.get("name"));
            if (profileData.containsKey("email")) student.setEmail((String) profileData.get("email"));
            if (profileData.containsKey("major")) student.setMajorName((String) profileData.get("major"));
            if (profileData.containsKey("startYear")) student.setStartYear((String) profileData.get("startYear"));
            if (profileData.containsKey("xepLoai")) student.setXepLoai((String) profileData.get("xepLoai"));
            studentRepository.save(student);
            return getUserProfile(username);
        }

        // --- Update staff ---
        Staff staff = staffRepository.findByUsername(username).orElse(null);
        if (staff != null) {
            if (profileData.containsKey("name")) staff.setFullName((String) profileData.get("name"));
            if (profileData.containsKey("email")) staff.setEmail((String) profileData.get("email"));
            if (profileData.containsKey("phone")) staff.setPhone((String) profileData.get("phone"));
            staffRepository.save(staff);
            return getUserProfile(username);
        }

        // --- Update admin ---
        User user = userRepository.findByUsername(username);
        if (user != null && user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()))) {
            if (profileData.containsKey("name")) user.setFullName((String) profileData.get("name"));
            if (profileData.containsKey("email")) user.setEmail((String) profileData.get("email"));
            if (profileData.containsKey("phone")) user.setPhone((String) profileData.get("phone"));
            userRepository.save(user);
            return getUserProfile(username);
        }

        throw new RuntimeException("User not found: " + username);
    }

    // ================= Stats (optional) =================
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalStaff = staffRepository.findAll().stream().filter(Staff::isStatus).count();
        long totalStudents = studentRepository.findAll().stream()
                .filter(s -> s.getStatusSV() != null && ("ACTIVE".equalsIgnoreCase(s.getStatusSV()) || "GRADUATED".equalsIgnoreCase(s.getStatusSV())))
                .count();
        long totalAdmins = userRepository.findAll().stream()
                .filter(u -> u.getRoles() != null && u.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName())))
                .count();

        stats.put("totalUsers", totalAdmins + totalStaff + totalStudents);
        stats.put("totalStudents", totalStudents);
        stats.put("totalStaff", totalStaff);
        stats.put("totalAdmins", totalAdmins);
        return stats;
    }

    public p12Service getP12Service() {
        return p12Service;
    }

    @Transactional
    public void deleteStudentByStudentCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode).orElseThrow(() ->
                                new ResourceNotFoundEx("Not found student with studentCode "+ studentCode));
        if(certificateRepository.existsByStudentId(studentCode) || certificateRequestRepository.existsByStudentRequestId(studentCode)){
            throw new IllegalStateException("StudentCode đang được sử dụng ở bảng Certificate hoặc CertificateRequest");
        }
        certificateRepository.deleteByStudentId(studentCode);
        certificateRequestRepository.deleteByStudentRequestId(studentCode);
        studentRepository.delete(student);
    }

    public User createUserWithDefaultPassword(RegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }

        User user;
        if("STUDENT".equals(request.getRole())){
            Student student = new Student();
            student.setUsername(request.getUsername());
            student.setPassword(passwordEncoder.encode("123456"));
            Role role = roleRepository.findByName(request.getRole()).orElseThrow(() -> new ResourceNotFoundEx("Role not found"));
            student.setRoles(Set.of(role));
            user = student;
        }else if("STAFF".equals(request.getRole())){
            Staff staff = new Staff();
            staff.setUsername(request.getUsername());
            staff.setPassword(passwordEncoder.encode("123456"));
            Role role = roleRepository.findByName(request.getRole()).orElseThrow(() -> new ResourceNotFoundEx("Role not found"));
            staff.setRoles(Set.of(role));
            user = staff;
        }else{
            User newUser = new User();
            newUser.setUsername(request.getUsername());
            newUser.setPassword(passwordEncoder.encode("123456"));
            Role role = roleRepository.findByName(request.getRole()).orElseThrow(() -> new ResourceNotFoundEx("Role not found"));
            newUser.setRoles(Set.of(role));
            user = newUser;
        }
        return userRepository.save(user);

    }

    @Transactional
    public void updatePassword(String username, String oldPassword, String newPassword) throws BadRequestException {
            User user = userRepository.findByUsername(username);
            if(user!=null){
                String oldPwd = oldPassword.trim();
                String newPwd = newPassword.trim();

                if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
                    throw new BadRequestException("Old password is incorrect");
                }

                if (passwordEncoder.matches(newPwd, user.getPassword())) {
                    throw new BadRequestException("New password cannot be the same as old password");
                }


                user.setPassword(passwordEncoder.encode(newPwd));
                userRepository.save(user);
            }else{
                throw new ResourceNotFoundEx("User not found");
            }
    }

    @Transactional
    public ApiResponse importStudents(MultipartFile file) {
        ApiResponse response = new ApiResponse();
        List<String[]> errorRows = new ArrayList<>();

        int successCount = 0;
        int errorCount = 0;

        Set<String> useNameInFile = new HashSet<>();

        try(InputStream is = file.getInputStream()){
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet =  workbook.getSheetAt(0);
            for(Row row : sheet ){
                if(row.getRowNum() == 0) continue;
//                String studentCode = row.getCellString(row.getCell(0));
//                String fullName = row.getCellString(row.getCell(1));
//                LocalDate dob = row.getCellDate(row.getCell(2));
//                String email = row.getCellString(row.getCell(3));
//                String phone = row.getCellString(row.getCell(4));
//                Integer departmentId = row.getCellInteger(row.getCell(5));

                List<String> errors = new ArrayList<>();
//
//                if(studentCode == null || studentCode.trim().isEmpty()){
//                    errors.add("Student code is empty");
//                }
//                if(fullName == null || fullName.trim().isEmpty()){
//                    errors.add("Full name is empty");
//                }
//                if(dob != null && dob.isAfter(LocalDate.now())){
//                    errors.add("Dob can not be in future");
//                }
//
//                if(departmentId == null || !departmentRepository.existsById(departmentId)){
//                    errors.add("Invalid department");
//                }




            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to import "+ e.getMessage());
        }

        return new ApiResponse();


    }
}