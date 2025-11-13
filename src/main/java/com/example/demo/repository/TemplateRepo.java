package com.example.demo.repository;

import com.example.demo.entity.Template;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepo extends CrudRepository<Template, String> {
}
