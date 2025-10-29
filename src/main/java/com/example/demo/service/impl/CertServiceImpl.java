package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Student;
import com.example.demo.service.fillCertificate;
import com.example.demo.service.interfaces.CertService;
import com.example.demo.service.StudentService;

@Service
public class CertServiceImpl implements CertService {
    @Autowired
    private StudentService studentService;

    @Autowired
    private fillCertificate fillCert;

    @Override
    public String createCertificateForStudent(String studentCode) throws Exception {
        Student student = studentService.getStudentByStudentCode(studentCode);

        // validate GPA
        Double gpa = student.getGpa();
        if (gpa == null || gpa < 0.0 || gpa > 4.0) {
            throw new Exception("Invalid GPA for student with studentCode: " + studentCode);
        }

        // validate graduation status
        String status = student.getStatusSV();
        if (status == null || !"GRADUATED".equalsIgnoreCase(status.trim())) {
            throw new Exception("Student is not graduated, cannot create certificate: " + studentCode);
        }

        // generate the certificate PDF (path returned) — keep return type void per interface
        String pdfPath = fillCert.generateCertificate(studentCode);
        // TODO: persist certificate record or log the generated path
        return pdfPath;
    }

}

