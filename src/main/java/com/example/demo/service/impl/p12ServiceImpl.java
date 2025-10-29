package com.example.demo.service.impl;

import com.example.demo.entity.Staff;
import com.example.demo.repository.StaffRepository;
import com.example.demo.service.CertificateService;
import com.example.demo.service.interfaces.p12Service;
import com.example.demo.utils.KeyUtil;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import com.example.demo.service.CertificateService;
@Service
public class p12ServiceImpl implements p12Service {
    private final StaffRepository staffRepository;

    public p12ServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    /**
     * Generate a PKCS#12 keystore for the given staff user id and write it to disk under ./keycert/.
     * This method uses a fixed demo password for the keystore ("123456").
     * In production you should accept a password parameter and protect secrets appropriately.
     */
    @Override
    public void generateP12(String staffCode) {
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

            // generate base64-encoded PKCS12
            CertificateService certService = new CertificateService();
            certService.create(staff.getStaffCode(), alias);      
            System.out.println("[p12Service] Generated keystore for staff staffCode=" + staffCode + " at: ");
            
            // Save to db
            
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate p12 for user " + staffCode, ex);
        }
        }


    }
}
