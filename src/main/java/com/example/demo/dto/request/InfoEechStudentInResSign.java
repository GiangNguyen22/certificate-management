package com.example.demo.dto.request;

public class InfoEechStudentInResSign {
    private String studentCode;
    private String name;
    private String dob;
    private String majorName;
    private String timeStudied;
    private String xepLoai;

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMajorName() {
        return majorName;
    }

    public void setMajorName(String majorName) {
        this.majorName = majorName;
    }

   public String getTimeStudied(){
    return timeStudied;
   }

   public void setTimeStudied(String timeStudied) {
        this.timeStudied = timeStudied;
    }
    public void setXepLoai(String xepLoai) {
        this.xepLoai = xepLoai;
    }
    public String getXepLoai() {
        return xepLoai;
    }
}
