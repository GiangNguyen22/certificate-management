package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.StaffProjection;
import com.example.demo.entity.Course;
import com.example.demo.entity.Role;
import com.example.demo.entity.Staff;
import com.example.demo.entity.Student;
import com.example.demo.entity.User;
import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.*;
import com.example.demo.service.interfaces.p12Service;
import org.apache.coyote.BadRequestException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.example.demo.dto.response.ApiResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StudentRepositoryI studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;
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
        staff.setDepartmentId(1);// Default department

        return staffRepository.save(staff);
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public List<StaffProjection> getStaffList(){
        return staffRepository.getStaffList();
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
            profile.put("gender", student.getGender() != null ? student.getGender() : "");
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
            profile.put("gender", staff.getGender() != null ? staff.getGender() : "");
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
            profile.put("gender", user.getGender() != null ? user.getGender() : "");
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
            if (profileData.containsKey("name"))
                student.setFullName((String) profileData.get("name"));
            if (profileData.containsKey("email"))
                student.setEmail((String) profileData.get("email"));
            if (profileData.containsKey("gender"))
                student.setGender((String) profileData.get("gender"));
            if (profileData.containsKey("major"))
                student.setMajorName((String) profileData.get("major"));
            if (profileData.containsKey("startYear"))
                student.setStartYear((String) profileData.get("startYear"));
            if (profileData.containsKey("xepLoai"))
                student.setXepLoai((String) profileData.get("xepLoai"));
            studentRepository.save(student);
            return getUserProfile(username);
        }

        // --- Update staff ---
        Staff staff = staffRepository.findByUsername(username).orElse(null);
        if (staff != null) {
            if (profileData.containsKey("name"))
                staff.setFullName((String) profileData.get("name"));
            if (profileData.containsKey("email"))
                staff.setEmail((String) profileData.get("email"));
            if (profileData.containsKey("gender"))
                staff.setGender((String) profileData.get("gender"));
            if (profileData.containsKey("phone"))
                staff.setPhone((String) profileData.get("phone"));
            staffRepository.save(staff);
            return getUserProfile(username);
        }

        // --- Update admin ---
        User user = userRepository.findByUsername(username);
        if (user != null && user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()))) {
            if (profileData.containsKey("name"))
                user.setFullName((String) profileData.get("name"));
            if (profileData.containsKey("email"))
                user.setEmail((String) profileData.get("email"));
            if (profileData.containsKey("gender"))
                user.setGender((String) profileData.get("gender"));
            if (profileData.containsKey("phone"))
                user.setPhone((String) profileData.get("phone"));
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
                .filter(s -> s.getStatusSV() != null && ("ACTIVE".equalsIgnoreCase(s.getStatusSV())
                        || "GRADUATED".equalsIgnoreCase(s.getStatusSV())))
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
    public void updateStudentByStudentCode(String studentCode, Map<String, Object> updateData) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundEx("Not found student with studentCode " + studentCode));

        // Update fields if provided
        if (updateData.containsKey("fullName")) {
            student.setFullName((String) updateData.get("fullName"));
        }
        if (updateData.containsKey("xepLoai")) {
            student.setXepLoai((String) updateData.get("xepLoai"));
        }
        if (updateData.containsKey("email")) {
            student.setEmail((String) updateData.get("email"));
        }
        if (updateData.containsKey("majorName")) {
            student.setMajorName((String) updateData.get("majorName"));
        }
        if (updateData.containsKey("className")) {
            student.setClassName((String) updateData.get("className"));
        }
        if (updateData.containsKey("gpa")) {
            Object gpaValue = updateData.get("gpa");
            if (gpaValue instanceof Number) {
                student.setGpa(((Number) gpaValue).doubleValue());
            } else if (gpaValue instanceof String) {
                try {
                    student.setGpa(Double.parseDouble((String) gpaValue));
                } catch (NumberFormatException e) {
                    // Ignore invalid GPA values
                }
            }
        }
        if (updateData.containsKey("startYear")) {
            student.setStartYear((String) updateData.get("startYear"));
        }
        if (updateData.containsKey("passedEnglish")) {
            Object passedEnglishValue = updateData.get("passedEnglish");
            if (passedEnglishValue instanceof Boolean) {
                student.setPassedEnglish((Boolean) passedEnglishValue);
            } else if (passedEnglishValue instanceof String) {
                student.setPassedEnglish(Boolean.parseBoolean((String) passedEnglishValue));
            }
        }
        // if (updateData.containsKey("statusSV")) {
        // student.setStatusSV((String) updateData.get("statusSV"));
        // }
        if (updateData.containsKey("status")) {
            Object statusValue = updateData.get("status");
            if (statusValue instanceof Boolean) {
                student.setStatus((Boolean) statusValue);
            } else if (statusValue instanceof String) {
                student.setStatus(Boolean.parseBoolean((String) statusValue));
            }
        }
        if (updateData.containsKey("gender")) {
            student.setGender((String) updateData.get("gender"));
        }
        if (updateData.containsKey("dob")) {
            Object dobValue = updateData.get("dob");
            if (dobValue instanceof LocalDate) {
                student.setDob((LocalDate) dobValue);
            } else if (dobValue instanceof String) {
                try {
                    student.setDob(LocalDate.parse((String) dobValue));
                } catch (Exception e) {
                    // Ignore invalid date format
                }
            }
        }

        studentRepository.save(student);
    }

    @Transactional
    public void deleteStudentByStudentCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundEx("Not found student with studentCode " + studentCode));
        if (certificateRepository.existsByStudentId(studentCode)
                || certificateRequestRepository.existsByStudentRequestId(studentCode)) {
            throw new IllegalStateException("StudentCode đang được sử dụng ở bảng Certificate hoặc CertificateRequest");
        }
        certificateRepository.deleteByStudentId(studentCode);
        certificateRequestRepository.deleteByStudentRequestId(studentCode);
        studentRepository.delete(student);
    }

    @Transactional
    public void deleteStaffByUsername(String username) {
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundEx("Not found staff with username " + username));
        staffRepository.delete(staff);
    }

    public User createUserWithDefaultPassword(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user;
        if ("STUDENT".equals(request.getRole())) {
            Student student = new Student();
            student.setUsername(request.getUsername());

            // Use provided password or default
            String passwordToEncode = request.getPassword() != null && !request.getPassword().trim().isEmpty()
                    ? request.getPassword()
                    : "123456";
            student.setPassword(passwordEncoder.encode(passwordToEncode));

            // Set basic user fields - RegisterRequest has 'name' not 'fullName'
            student.setFullName(request.getName() != null ? request.getName() : request.getUsername());
            student.setEmail(request.getEmail() != null ? request.getEmail() : "");
            student.setGender(request.getGender() != null ? request.getGender() : "");

            // Set student-specific fields
            student.setStudentCode(request.getStudentCode());
            student.setMajorName(request.getMajorName());
            student.setStartYear(request.getYear());
            student.setXepLoai(request.getXepLoai());
            student.setStatusSV("ACTIVE");
            student.setGpa(0.0);
            student.setPassedEnglish(false);

            Set<Role> roles = new HashSet<>();
            Role studentRole = roleRepository.findByName(request.getRole())
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(request.getRole());
                        newRole.setDescription("Student Role");
                        return roleRepository.save(newRole);
                    });
            roles.add(studentRole);
            student.setRoles(roles);

            user = student;
            studentRepository.save(student);
        } else if ("STAFF".equals(request.getRole())) {
            Staff staff = new Staff();
            staff.setUsername(request.getUsername());

            // Use provided password or default
            String passwordToEncode = request.getPassword() != null && !request.getPassword().trim().isEmpty()
                    ? request.getPassword()
                    : "123456";
            staff.setPassword(passwordEncoder.encode(passwordToEncode));

            staff.setFullName(request.getName() != null ? request.getName() : request.getUsername());
            staff.setEmail(request.getEmail() != null ? request.getEmail() : "");
            staff.setGender(request.getGender() != null ? request.getGender() : "");
            staff.setStatus(true);
            staff.setDepartmentId(1);// Default department
            staff.setName(request.getName() != null ? request.getName() : request.getUsername());
            staff.setStaffCode(request.getStaffCode());


            Set<Role> roles = new HashSet<>();
            Role staffRole = roleRepository.findByName(request.getRole())
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(request.getRole());
                        newRole.setDescription("Staff Role");
                        return roleRepository.save(newRole);
                    });
            roles.add(staffRole);
            staff.setRoles(roles);
            user = staff;
            staffRepository.save(staff);
        } else {
            User newUser = new User();
            newUser.setUsername(request.getUsername());

            // Use provided password or default
            String passwordToEncode = request.getPassword() != null && !request.getPassword().trim().isEmpty()
                    ? request.getPassword()
                    : "123456";
            newUser.setPassword(passwordEncoder.encode(passwordToEncode));

            newUser.setFullName(request.getName() != null ? request.getName() : request.getUsername());
            newUser.setEmail(request.getEmail() != null ? request.getEmail() : "");

            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName(request.getRole())
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(request.getRole());
                        newRole.setDescription("ADMIN Role");
                        return roleRepository.save(newRole);
                    });
            roles.add(userRole);
            newUser.setRoles(roles);
            user = newUser;
        }
        return userRepository.save(user);

    }

    @Transactional
    public void updatePassword(String username, String oldPassword, String newPassword) throws BadRequestException {
        User user = userRepository.findByUsername(username);
        if (user != null) {
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
        } else {
            throw new ResourceNotFoundEx("User not found");
        }
    }

    @Transactional
    public void updateStaffStatus(String username, boolean status) {
        Staff staff = staffRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundEx("Not found staff with username " + username));
        staff.setStatus(status);
        staffRepository.save(staff);
    }

    @Transactional
    public void updateStudentStatus(String studentCode, boolean status) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundEx("Not found student with studentCode " + studentCode));
        student.setStatus(status);
        studentRepository.save(student);
    }

    @Transactional
    public ApiResponse importStudents(MultipartFile file) {
        ApiResponse response = new ApiResponse();

        try {
            System.out.println("=== STARTING IMPORT ===");
            System.out.println("File: " + file.getOriginalFilename() + ", Size: " + file.getSize());

            List<String[]> errorRows = new ArrayList<>();
            int successCount = 0;
            int errorCount = 0;
            Set<String> studentCodeInFile = new HashSet<>();

            try (InputStream is = file.getInputStream()) {
                Workbook workbook = WorkbookFactory.create(is);
                Sheet sheet = workbook.getSheetAt(0);
                System.out.println("Total rows: " + (sheet.getLastRowNum() + 1));

                for (Row row : sheet) {
                    if (row.getRowNum() == 0)
                        continue;

                    System.out.println("--- Processing row " + (row.getRowNum() + 1) + " ---");

                    String studentCode = getCellString(row.getCell(0));
                    String fullName = getCellString(row.getCell(1));

                    System.out.println("StudentCode: '" + studentCode + "', FullName: '" + fullName + "'");

                    List<String> errors = new ArrayList<>();

                    if (studentCode == null || studentCode.trim().isEmpty()) {
                        errors.add("Mã sinh viên là bắt buộc");
                    }
                    if (fullName == null || fullName.trim().isEmpty()) {
                        errors.add("Họ tên là bắt buộc");
                    }

                    if (!errors.isEmpty()) {
                        System.out.println("❌ Validation errors: " + errors);
                        errorCount++;
                        errorRows.add(new String[] {
                                String.valueOf(row.getRowNum() + 1),
                                studentCode, fullName, "", "", "",
                                String.join("; ", errors)
                        });
                        continue;
                    }

                    try {
                        Student student = studentRepository.findByStudentCode(studentCode).orElse(null);

                        if (student == null) {
                            System.out.println("🆕 Creating new student: " + studentCode);
                            student = new Student();
                            student.setStudentCode(studentCode);
                            student.setUsername(studentCode);
                            student.setPassword(passwordEncoder.encode("123456"));

                            Set<Role> roles = new HashSet<>();
                            Role studentRole = roleRepository.findByName("STUDENT")
                                    .orElseThrow(() -> new RuntimeException("STUDENT role not found"));
                            roles.add(studentRole);
                            student.setRoles(roles);
                        } else {
                            System.out.println("📝 Updating existing student: " + studentCode);
                        }

                        // Cập nhật thông tin
                        student.setFullName(fullName);
                        student.setDob(getCellDate(row.getCell(2)));
                        student.setEmail(getCellString(row.getCell(3)));
                        student.setPhone(getCellString(row.getCell(4)));
                        student.setMajorName(getCellString(row.getCell(5)));
                        student.setClassName(getCellString(row.getCell(6)));

                        Double gpa = getCellDouble(row.getCell(7));
                        student.setGpa(gpa != null ? gpa : 0.0);

                        student.setStartYear(getCellString(row.getCell(8)));
                        student.setXepLoai(getCellString(row.getCell(9)));

                        Boolean passedEnglish = getCellBoolean(row.getCell(10));
                        student.setPassedEnglish(passedEnglish != null ? passedEnglish : false);

                        String statusSV = getCellString(row.getCell(11));
                        student.setStatusSV(statusSV != null ? statusSV : "ACTIVE");

                        student.setStatus(true);

                        // Test save
                        System.out.println("💾 Saving student...");
                        studentRepository.save(student);
                        successCount++;
                        System.out.println("✅ Saved successfully: " + studentCode);

                    } catch (Exception e) {
                        System.err.println("💥 ERROR in row " + (row.getRowNum() + 1) + ": " + e.getMessage());
                        e.printStackTrace();

                        errorCount++;
                        errorRows.add(new String[] {
                                String.valueOf(row.getRowNum() + 1),
                                studentCode, fullName, "", "", "",
                                "Lỗi: " + e.getMessage()
                        });
                    }
                }

                workbook.close();

                String errorFilePath = null;
                if (!errorRows.isEmpty()) {
                    errorFilePath = exportErrorFile(errorRows);
                }

                System.out.println("=== IMPORT COMPLETED ===");
                System.out.println("✅ Success: " + successCount + ", ❌ Errors: " + errorCount);

                response.setSuccess(true);
                response.setStatus("OK");
                response.setMessage("Import completed");
                Map<String, Object> data = new HashMap<>();
                data.put("successCount", successCount);
                data.put("errorCount", errorCount);
                if (errorFilePath != null) {
                    data.put("errorFilePath", errorFilePath);
                }
                response.setData(data);

                return response;

            }
        } catch (Exception e) {
            System.err.println("💥💥 IMPORT FAILED: " + e.getMessage());
            e.printStackTrace();
            response.setSuccess(false);
            response.setStatus("ERROR");
            response.setMessage("Import failed: " + e.getMessage());
            return response;
        }
    }

    // Thêm các helper methods mới
    private Double getCellDouble(Cell cell) {
        if (cell == null)
            return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }
        try {
            return Double.parseDouble(cell.getStringCellValue().trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean getCellBoolean(Cell cell) {
        if (cell == null)
            return null;
        if (cell.getCellType() == CellType.BOOLEAN) {
            return cell.getBooleanCellValue();
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue() == 1;
        }
        if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue().trim();
            return "1".equals(value) || "true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value);
        }
        return null;
    }

    // Sửa method exportErrorFile để hiển thị đầy đủ thông tin
    private String exportErrorFile(List<String[]> errorRows) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Errors");

        // Header chi tiết hơn
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Dòng");
        header.createCell(1).setCellValue("Mã SV");
        header.createCell(2).setCellValue("Họ Tên");
        header.createCell(3).setCellValue("Ngày Sinh");
        header.createCell(4).setCellValue("Email");
        header.createCell(5).setCellValue("SĐT");
        header.createCell(6).setCellValue("Lỗi");

        // Dữ liệu lỗi
        int rowIdx = 1;
        for (String[] rowData : errorRows) {
            Row row = sheet.createRow(rowIdx++);
            for (int i = 0; i < rowData.length; i++) {
                row.createCell(i).setCellValue(rowData[i] != null ? rowData[i] : "");
            }
        }

        // Auto-size columns
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }

        // Tạo folder nếu chưa tồn tại
        String dirPath = "uploads/errors/";
        new File(dirPath).mkdirs();

        // File path
        String filename = "error_import_" + LocalDate.now() + ".xlsx";
        String filePath = dirPath + filename;

        // Ghi file
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
        }

        workbook.close();
        System.out.println("=== ERROR FILE EXPORTED ===");
        System.out.println("File saved at: " + filePath);
        return filePath;
    }

    private String getCellString(Cell cell) {
        if (cell == null)
            return null;
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        }
        return null;
    }

    private LocalDate getCellDate(Cell cell) {
        if (cell == null)
            return null;
        try {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getCellInteger(Cell cell) {
        if (cell == null)
            return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        try {
            return Integer.parseInt(cell.getStringCellValue().trim());
        } catch (Exception e) {
            return null;
        }
    }

    public Student enrollStudentInCourse(String studentCode, String courseCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        student.getCourses().add(course);
        return studentRepository.save(student);
    }

    public Set<Course> getCoursesByStudentCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return student.getCourses();
    }

    @Transactional
    public String exportStudentsToExcel() {
        System.out.println("=== STARTING EXPORT STUDENTS TO EXCEL ===");
        List<Student> students = studentRepository.findAll();
        System.out.println("Found " + students.size() + " students to export");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Mã Sinh Viên");
        headerRow.createCell(1).setCellValue("Họ Tên");
        headerRow.createCell(2).setCellValue("Ngày Sinh");
        headerRow.createCell(3).setCellValue("Email");
        headerRow.createCell(4).setCellValue("Số Điện Thoại");
        headerRow.createCell(5).setCellValue("Ngành Học");
        headerRow.createCell(6).setCellValue("Lớp");
        headerRow.createCell(7).setCellValue("GPA");
        headerRow.createCell(8).setCellValue("Năm Nhập Học");
        headerRow.createCell(9).setCellValue("Xếp Loại");
        headerRow.createCell(10).setCellValue("Đã Thi Đậu Tiếng Anh");
        headerRow.createCell(11).setCellValue("Trạng Thái");

        // Fill data rows
        int rowNum = 1;
        for (Student student : students) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getStudentCode() != null ? student.getStudentCode() : "");
            row.createCell(1).setCellValue(student.getFullName() != null ? student.getFullName() : "");
            row.createCell(2).setCellValue(student.getDob() != null ? student.getDob().toString() : "");
            row.createCell(3).setCellValue(student.getEmail() != null ? student.getEmail() : "");
            row.createCell(4).setCellValue(student.getPhone() != null ? student.getPhone() : "");
            row.createCell(5).setCellValue(student.getMajorName() != null ? student.getMajorName() : "");
            row.createCell(6).setCellValue(student.getClassName() != null ? student.getClassName() : "");
            row.createCell(7).setCellValue(student.getGpa() != null ? String.valueOf(student.getGpa()) : "");
            row.createCell(8).setCellValue(student.getStartYear() != null ? student.getStartYear() : "");
            row.createCell(9).setCellValue(student.getXepLoai() != null ? student.getXepLoai() : "");
            row.createCell(10).setCellValue(student.isPassedEnglish() ? "1" : "0");
            row.createCell(11).setCellValue(student.getStatusSV() != null ? student.getStatusSV() : "INACTIVE");
        }

        // Auto-size columns
        for (int i = 0; i < 12; i++) {
            sheet.autoSizeColumn(i);
        }

        // Save file
        String dirPath = "exports/";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create exports directory");
            }
        }

        String filename = "students_export_" + LocalDate.now() + "_" + System.currentTimeMillis() + ".xlsx";
        String filePath = dirPath + filename;

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export students: " + e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                // Ignore
            }
        }

        return filePath;
    }

}