package com.example.demo.utils;

import java.io.File;
import java.io.FileOutputStream;

import org.springframework.web.multipart.MultipartFile;



public class TempFileUtil {
    // ========== CÁC PHƯƠNG THỨC LƯU FILE TẠM ==========

/**
 * Lưu MultipartFile thành file tạm thời
 */
public static String saveTempFile(MultipartFile file) throws Exception {
    // Tạo thư mục temp nếu chưa tồn tại
    String tempDir = System.getProperty("java.io.tmpdir");
    File tempDirectory = new File(tempDir);
    if (!tempDirectory.exists()) {
        tempDirectory.mkdirs();
    }
    
    // Tạo tên file duy nhất
    String timestamp = String.valueOf(System.currentTimeMillis());
    String originalFileName = file.getOriginalFilename();
    String fileExtension = getFileExtension(originalFileName);
    String fileName = "temp_" + timestamp + "_" + originalFileName;
    
    String filePath = tempDir + File.separator + fileName;
    
    // Lưu file
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
        fos.write(file.getBytes());
    }
    
    System.out.println("✅ Đã lưu file tạm: " + filePath);
    return filePath;
}

/**
 * Lưu MultipartFile với tên file tùy chỉnh
 */
public static String saveTempFile(MultipartFile file, String customFileName) throws Exception {
    String tempDir = System.getProperty("java.io.tmpdir");
    String filePath = tempDir + File.separator + customFileName;
    
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
        fos.write(file.getBytes());
    }
    
    return filePath;
}

/**
 * Lấy phần mở rộng của file
 */
public static String getFileExtension(String fileName) {
    if (fileName == null || !fileName.contains(".")) {
        return "";
    }
    return fileName.substring(fileName.lastIndexOf("."));
}

/**
 * Kiểm tra và tạo thư mục nếu không tồn tại
 */
public static void ensureDirectoryExists(String directoryPath) {
    File directory = new File(directoryPath);
    if (!directory.exists()) {
        directory.mkdirs();
    }
}
}
