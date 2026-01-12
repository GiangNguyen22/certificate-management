package com.example.demo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;



@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "User Management API",
        version = "1.0",
        description = "API for managing users with Swagger documentation"
    )
)
public class CertificateManagementApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(CertificateManagementApplication.class, args);
    }
}