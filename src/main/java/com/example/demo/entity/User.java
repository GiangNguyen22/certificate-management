package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Data

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private boolean status;
    @Column(name="department_id")
    private int departmentId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
        joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)),
        inverseJoinColumns = @JoinColumn(name="role_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    )
    private Set<Role> roles = new HashSet<>();


    // Custom constructor removed — rely on Lombok-generated constructors
}
