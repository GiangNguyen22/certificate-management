package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.Template;
import java.util.List;
import java.util.Optional;


public interface TemplateRepository extends JpaRepository<Template, String> {
    Optional<Template> findById(String templateId);
    
}
