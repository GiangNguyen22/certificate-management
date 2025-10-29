package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserPubKeysRepository;
import com.example.demo.service.interfaces.UserKeyService;
import com.example.demo.entity.UserPublicKeys;
@Service
public class UserKeyServiceImpl implements UserKeyService{
    @Autowired
    private UserPubKeysRepository userPubKeysRepository;
    @Override
    public UserKeyService saveNewUserKey(String userId, String publicKey, String createdAt, String CryptoType) {
        UserPublicKeys newUserPublicKeys = new UserPublicKeys();
        newUserPublicKeys.setUserId(userId);
        newUserPublicKeys.setPublicKey(publicKey);
        newUserPublicKeys.setCreatedAt(createdAt);
        newUserPublicKeys.setCryptographyType(CryptoType);
        
        return (UserKeyService) userPubKeysRepository.save(newUserPublicKeys);
    }
}