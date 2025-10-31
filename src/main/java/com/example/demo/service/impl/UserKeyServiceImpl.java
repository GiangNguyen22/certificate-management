package com.example.demo.service.impl;

import com.example.demo.entity.Staff;
import com.example.demo.exceptions.ResourceNotFoundEx;
import com.example.demo.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserPubKeysRepository;
import com.example.demo.service.interfaces.UserKeyService;
import com.example.demo.entity.UserPublicKeys;
@Service
public class UserKeyServiceImpl implements UserKeyService{
    @Autowired
    private UserPubKeysRepository userPubKeysRepository;
    @Autowired
    private StaffRepository staffRepository;

    @Override
    public UserPublicKeys saveNewUserKey(String userId, String publicKey, String createdAt, String CryptoType) {
        Staff staff = staffRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new ResourceNotFoundEx("Not found staff with id: " + userId));


        UserPublicKeys newUserPublicKeys = new UserPublicKeys();
        newUserPublicKeys.setStaff(staff);
        newUserPublicKeys.setPublicKey(publicKey);
        newUserPublicKeys.setCreatedAt(createdAt);
        newUserPublicKeys.setCryptographyType(CryptoType);
        
        return userPubKeysRepository.save(newUserPublicKeys);
    }
}