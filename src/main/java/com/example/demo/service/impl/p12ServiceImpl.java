package com.example.demo.service.impl;

import com.example.demo.entity.Staff;
import com.example.demo.repository.StaffRepository;
import com.example.demo.service.CertificateService;
import com.example.demo.service.interfaces.p12Service;
import org.springframework.stereotype.Service;
import java.nio.file.Path;

@Service
public class p12ServiceImpl implements p12Service {
    private final StaffRepository staffRepository;
    private final CertificateService certificateService;

    public p12ServiceImpl(StaffRepository staffRepository, CertificateService certificateService) {
        this.staffRepository = staffRepository;
        this.certificateService = certificateService;
    }

    /**
     * Generate a PKCS#12 keystore for the given staff user id and write it to disk under ./keycert/.
     * This method uses a fixed demo password for the keystore ("123456").
     * In production you should accept a password parameter and protect secrets appropriately.
     */
    @Override
    public String generateP12(String staffCode) {
        Staff staff = staffRepository.findByStaffCode(staffCode);
        if (staff == null) {
            throw new RuntimeException("Staff not found with staff code: " + staffCode);
        }
        else{

            try {
        // alias will be staff code if available, otherwise fallback to id
        String alias = staff.getStaffCode() != null && !staff.getStaffCode().isBlank()
            ? staff.getStaffCode()
            : ("staff-" + staff.getId());

        // Use the Spring-managed CertificateService to create a PKCS#12.
        // CertificateService.create(staffId, password) expects a password param; use a demo password for smoke-tests.
        String demoPassword = "123456";
        Path pathp12=  certificateService.create(staff.getStaffCode(), demoPassword);
        return pathp12.toString();
        
            
            // Save to db
            
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate p12 for user " + staffCode, ex);
        }
        }


    }
}
