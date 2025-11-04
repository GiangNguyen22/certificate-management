package com.example.demo.dto.response;

public class CertificateDTO {
    private Long id;
    private String certId;
    private String studentCode;
    private String issueAt;
    private String expireAt;
    private String status;
    private String serialNumber;
    public CertificateDTO() {
    }
    public CertificateDTO(Long id, String certId, String studentCode, String issueAt, String expireAt, String status,
            String serialNumber) {
        this.id = id;
        this.certId = certId;
        this.studentCode = studentCode;
        this.issueAt = issueAt;
        this.expireAt = expireAt;
        this.status = status;
        this.serialNumber = serialNumber;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCertId() {
        return certId;
    }
    public void setCertId(String certId) {
        this.certId = certId;
    }
    public String getStudentCode() {
        return studentCode;
    }
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }
    public String getIssueAt() {
        return issueAt;
    }
    public void setIssueAt(String issueAt) {
        this.issueAt = issueAt;
    }
    public String getExpireAt() {
        return expireAt;
    }
    public void setExpireAt(String expireAt) {
        this.expireAt = expireAt;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
}
