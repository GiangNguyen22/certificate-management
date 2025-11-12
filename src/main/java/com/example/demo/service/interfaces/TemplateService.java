package com.example.demo.service.interfaces;

import com.example.demo.entity.Template;

public interface TemplateService {
    Template getTemplateById(String templateId) throws Exception;
}
