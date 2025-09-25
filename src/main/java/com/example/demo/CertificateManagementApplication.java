package com.example.demo;

import com.example.demo.entity.Student;
import com.example.demo.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@SpringBootApplication
public class CertificateManagementApplication {

	public static void main(String[] args) {

//		SpringApplication.run(CertificateManagementApplication.class, args);

		ApplicationContext context = SpringApplication.run(CertificateManagementApplication.class, args);

		// Lấy bean StudentService đã được Spring quản lý
		StudentService studentService = context.getBean(StudentService.class);

		// Gọi phương thức
//		studentService.createStudent();
//		Optional<Student> s = studentService.getStudent("68d55b6ae404c5e89c6851de");
//		System.out.println(s.toString());
	}

}
