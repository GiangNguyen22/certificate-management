package com.example.demo.repository;

import com.example.demo.entity.UserP12File;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserP12FileRepository extends JpaRepository<UserP12File, Long> {
    Optional<UserP12File> findByAlias(String alias);
}
