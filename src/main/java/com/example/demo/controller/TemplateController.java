package com.example.demo.controller;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.entity.Template;
import com.example.demo.service.TemplateService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllTemplates() {
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        response.setStatus("OK");
        response.setMessage("Templates retrieved successfully");
        response.setData(templateService.getAllTemplates());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-template")
    public ResponseEntity<ApiResponse> addTemplate(
            @RequestPart("template") Template template,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        ApiResponse response = new ApiResponse();
        Template savedTemplate = templateService.addTemplate(template, file);
        response.setSuccess(true);
        response.setStatus("OK");
        response.setMessage("Template added successfully");
        response.setData(savedTemplate);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{templateId}/delete")
    public ResponseEntity<ApiResponse> deleteTemplate(@PathVariable String templateId) {
        ApiResponse response = new ApiResponse();
        templateService.deleteTemplate(templateId);
        response.setSuccess(true);
        response.setStatus("OK");
        response.setMessage("Template deleted successfully");
        return ResponseEntity.ok(response);
    }
}
