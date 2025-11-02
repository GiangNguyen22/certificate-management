package com.example.demo.dto;

import java.util.Date;

public class VerifyResult {
    private String signerName, reason, subjectDN, issuerDN;
    private Date signDate;
    private boolean signatureValid, coversWholeDocument, valid;
    private Integer chainLength;
    private Boolean timeStampValid;
    private String message;
private boolean publicKeyValid;
    // Getters and setters...
    public String getSignerName() { return signerName; }
    public void setSignerName(String v) { this.signerName = v; }
    public Date getSignDate() { return signDate; }
    public void setSignDate(Date v) { this.signDate = v; }
    public String getReason() { return reason; }
    public void setReason(String v) { this.reason = v; }
    public String getSubjectDN() { return subjectDN; }
    public void setSubjectDN(String v) { this.subjectDN = v; }
    public String getIssuerDN() { return issuerDN; }
    public void setIssuerDN(String v) { this.issuerDN = v; }
    public Integer getChainLength() { return chainLength; }
    public void setChainLength(Integer v) { this.chainLength = v; }
    public boolean isSignatureValid() { return signatureValid; }
    public void setSignatureValid(boolean v) { this.signatureValid = v; }
    public boolean isCoversWholeDocument() { return coversWholeDocument; }
    public void setCoversWholeDocument(boolean v) { this.coversWholeDocument = v; }
    public Boolean isTimeStampValid() { return timeStampValid; }
    public void setTimeStampValid(Boolean v) { this.timeStampValid = v; }
   
    public boolean isValid() { return valid; }
    public void setValid(boolean v) { this.valid = v; }
    public String getMessage() { return message; }
    public void setMessage(String v) { this.message = v; }
    public boolean isPublicKeyValid() { return publicKeyValid; }
    public void setPublicKeyValid(boolean v) { this.publicKeyValid = v; }
}
