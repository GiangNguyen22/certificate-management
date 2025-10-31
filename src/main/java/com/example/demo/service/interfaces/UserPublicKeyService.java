package com.example.demo.service.interfaces;
import com.example.demo.entity.UserPublicKeys;
import java.util.Optional;

public interface UserPublicKeyService {
    public UserPublicKeys insertKey(String id, String userId, String publicKey, String  createdAt, String cryptographyAlgorithm);
    public Optional<UserPublicKeys> getByUserId(String userId);
    public UserPublicKeys updateKey(String id, String userId, String publicKey, String  createdAt, String cryptographyAlgorithm);

}
