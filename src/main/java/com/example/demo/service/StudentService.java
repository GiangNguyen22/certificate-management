package com.example.demo.service;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepositoryI;
import org.springframework.beans.factory.annotation.Autowired;
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
        student.setName("Doan");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = LocalDate.now().format(formatter); // "09/25/2025"
        LocalDate dob = LocalDate.parse(formattedDate, formatter);
        student.setDob(dob);
        student.setStudentCode("11111111");
        student.setMajorName("CNTT");
        student.setYear("2022-2028");
        studentRepository.save(student);
    }

    public Optional<Student> getStudent(String id){
        return studentRepository.findById(id);
    }
    // public Optional<Student> getStudentbyStudentCode(String studentCode){
    //     return studentRepository.findByStudentCode(studentCode);
    // }

}
