package com.example.demo.specifications;

import com.example.demo.entity.Student;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {
    public static Specification<Student> hasStudentCode(String studentCode){
        return (root, query, criteriaBuilder) -> {
            if(studentCode == null || studentCode.isEmpty()){
                return null;
            }
            return criteriaBuilder.equal(root.get("studentCode"), studentCode);
        };
    }

    public static Specification<Student> hasNameLike(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fullName")), "%"+ name.toLowerCase() + "%");
            };
    }

    public static Specification<Student> hasGrade(String grade) {
        return (root, query, criteriaBuilder) -> {
            if (grade == null || grade.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("xepLoai")), "%"+ grade.toLowerCase() + "%");
        };
    }

}
