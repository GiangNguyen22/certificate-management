package com.example.demo.service;

import com.example.demo.entity.Template;
import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.TemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {
    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public Template addTemplate(Template template, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {

                String fileName = LocalDate.now() + "_" + file.getOriginalFilename();

                Path uploadPath = Paths.get("src/main/resources/templates");

                // Tạo thư mục nếu chưa tồn tại
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Lưu file
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                template.setFilePath("templates/" + fileName);


            } catch (IOException e) {
                throw new RuntimeException("Failed to store file: " + e.getMessage());
            }
        }
        return templateRepository.save(template);

    }

    public void deleteTemplate(String templateId) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundEx("Template not found with id: " + templateId));

        if (template.getFilePath() != null && !template.getFilePath().isEmpty()) {
            try {
                Path filePath = Paths.get("src/main/resources/" + template.getFilePath());

                // Nếu file tồn tại thì xóa
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + e.getMessage());
            }
        }

        templateRepository.delete(template);
    }

    // public List<Template> getAllTemplates() {
    //     return templateRepository.findAll();
    // }

    public Template updateTemplate(String templateId, Template templateData) {
        Template existingTemplate = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundEx("Template not found with id: " + templateId));

        Optional.ofNullable(templateData.getName()).ifPresent(existingTemplate::setName);
        Optional.ofNullable(templateData.getDescription()).ifPresent(existingTemplate::setDescription);
        Optional.ofNullable(templateData.getType()).ifPresent(existingTemplate::setType);
        Optional.ofNullable(templateData.getFilePath()).ifPresent(existingTemplate::setFilePath);

        return templateRepository.save(existingTemplate);
    }
     public List<Template> getAllTemplates() {
        return (List<Template>) templateRepository.findAll();
    }
}