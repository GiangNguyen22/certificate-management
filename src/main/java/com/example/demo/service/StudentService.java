package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepositoryI;
import com.example.demo.specifications.StudentSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class StudentService{
    @Autowired
    private StudentRepositoryI studentRepository;

    public void createStudent(){
        Student student = new Student();
        student.setUsername("Nguyen");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = LocalDate.now().format(formatter); // "09/25/2025"
        LocalDate dob = LocalDate.parse(formattedDate, formatter);
        student.setDob(dob);
        student.setStudentCode("11111111");
        student.setMajorName("CNTT");
        student.setStartYear("2022-2028");
        studentRepository.save(student);
    }

    public Optional<Student> getStudent(Long id){
        return studentRepository.findById(id);
    }


    public Student getStudentByStudentCode(String studentCode) throws Exception {
        Optional<Student> studentOpt = studentRepository.findByStudentCode(studentCode);
        if (studentOpt.isEmpty()) {
            throw new Exception("Student not found with studentCode: " + studentCode);
        }
        return studentOpt.get();
    }

    public Page<Student> searchStudents(String studentCode, String name, String grade, Pageable pageable){
        Specification<Student> spec = StudentSpecification.hasStudentCode(studentCode)
                .or(StudentSpecification.hasNameLike(name))
                .or(StudentSpecification.hasGrade(grade));
        return studentRepository.findAll(spec, pageable);
    }

    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }
}