package com.example.demo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.UserPublicKeys;

import java.util.Optional;
@Repository
public interface UserPubKeysRepository extends JpaRepository<UserPublicKeys, Long> {
    
    Optional<UserPublicKeys> findByUserId(String userId);

    
    
}
    

