package com.example.demo.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignCertRequest {
    @NotNull @Min(1) private String templateId;
    @NotNull private String staffCode;
    @NotNull private String alias;
    @NotNull private String keystorePass;
    @NotNull private MultipartFile p12File;
    @NotNull private String courseCode;
    @NotEmpty @Size(max = 500) 
    @Valid 
    private List<InfoEechStudentInResSign> students;
    
}
