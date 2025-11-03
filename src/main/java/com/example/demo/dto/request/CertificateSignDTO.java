package com.example.demo.dto.request;

import org.springframework.web.multipart.MultipartFile;

public class CertificateSignDTO{
    private String StudentCode;
    private String RequestCode;
    private String StaffCode;
    private String KeystorePass;
    private String Alias;
    private MultipartFile p12File;

    public String getRequestCode() {
        return RequestCode;
    }

    public void setRequestCode(String requestCode) {
        RequestCode = requestCode;
    }

    public String getStudentCode() {
        return StudentCode;
    }
    public void setStudentCode(String studentCode) {
        StudentCode = studentCode;
    }
    public String getStaffCode() {
        return StaffCode;
    }
    public void setStaffCode(String staffCode) {
        StaffCode = staffCode;
    }
    public String getKeystorePass() {
        return KeystorePass;
    }
    public void setKeystorePass(String keystorePass) {
        KeystorePass = keystorePass;
    }
    public String getAlias() {
        return Alias;
    }
    public void setAlias(String alias) {
        Alias = alias;
    }
    public MultipartFile getP12File() {
        return p12File;
    }
    public void setP12File(MultipartFile p12File) {
        this.p12File = p12File;
    }


}