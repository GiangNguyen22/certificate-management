package com.example.demo.service.interfaces;

import com.example.demo.entity.UserPublicKeys;

public interface UserKeyService {
    /**
     * Save a new user public key record.
     * @return the saved UserPublicKeys entity
     */
    UserPublicKeys saveNewUserKey(String staffCode, String publicKey, String createdAt, String CryptoType);
}
