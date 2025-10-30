package com.example.demo.controller;

import com.example.demo.entity.UserPublicKeys;
import com.example.demo.service.KeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/keys")
public class KeyController {

    @Autowired
    private KeyService keyService;

    @PostMapping("/generate")
    public ResponseEntity<UserPublicKeys> generateKeyPair(@RequestParam String userId, @RequestParam String password) throws Exception {
        UserPublicKeys keyPair = keyService.generateKeyPair(userId, password);
        return ResponseEntity.ok(keyPair);
    }

    @GetMapping
    public ResponseEntity<List<UserPublicKeys>> getAllKeys() {
        List<UserPublicKeys> keys = keyService.getAllKeys();
        return ResponseEntity.ok(keys);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadKey(@PathVariable Long id) throws Exception {
        Path keyFile = keyService.getKeyFile(id);

        Resource resource = new UrlResource(keyFile.toUri());
        if (resource.exists() || resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + keyFile.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    }
}