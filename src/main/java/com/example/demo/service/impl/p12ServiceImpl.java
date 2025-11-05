package com.example.demo.service.impl;

import com.example.demo.entity.Staff;
import com.example.demo.repository.StaffRepository;
import com.example.demo.service.CertificateService;
import com.example.demo.service.interfaces.p12Service;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class p12ServiceImpl implements p12Service {
    private final StaffRepository staffRepository;
    private final CertificateService certificateService;

    public p12ServiceImpl(StaffRepository staffRepository, CertificateService certificateService) {
        this.staffRepository = staffRepository;
        this.certificateService = certificateService;
    }

    @Override
    public ByteArrayResource generateP12(String staffCode) {
       Staff staff = staffRepository.findByStaffCode(staffCode)
               .orElseThrow(() -> new RuntimeException("Staff not found with staff code: " + staffCode));
         
        if (staff == null) {
            throw new RuntimeException("Staff not found with staff code: " + staffCode);
        }else {
        

        try {
            String alias = staff.getStaffCode() != null && !staff.getStaffCode().isBlank()
            ? staff.getStaffCode()
            : ("staff-" + staff.getId());
            // Generate secure random password
            String demoPassword = "123456";

            String p12string = certificateService.create(staffCode, demoPassword);
            byte[] p12Bytes = Base64.getDecoder().decode(p12string);

            return new ByteArrayResource(p12Bytes);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Failed to generate p12 for user : "+staffCode + " " + ex.getMessage(),
                    ex);
        }
    }
}
}