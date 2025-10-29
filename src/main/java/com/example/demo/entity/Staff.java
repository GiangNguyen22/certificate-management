package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@PrimaryKeyJoinColumn(name = "user_id")

public class Staff extends User {

    private String name;
    @Column(name="staff_code")
    private String staffCode;
}

