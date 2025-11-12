package com.example.demo.service.impl;

import com.example.demo.entity.Template;
import com.example.demo.repository.TemplateRepository;
import com.example.demo.service.interfaces.TemplateService;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService {
    private final TemplateRepository templateRepository;

    public TemplateServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public Template getTemplateById(String templateId) throws Exception {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new Exception("Template not found with id: " + templateId));
    }
}
