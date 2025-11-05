package com.example.demo.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.UserPublicKeys;

import java.util.Optional;
@Repository
public interface UserPubKeysRepository extends JpaRepository<UserPublicKeys, Long> {

    Optional<UserPublicKeys> findByStaffCode(String staffCode);

    @Query("SELECT u.publicKey FROM UserPublicKeys u WHERE u.staffCode = :staffCode")
    String findPublicKeyByStaffCode(@Param("staffCode") String staffCode);


}
    

