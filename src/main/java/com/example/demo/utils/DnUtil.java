package com.example.demo.utils;
import org.bouncycastle.asn1.x500.X500Name;
import com.example.demo.entity.Staff;

//sinh ra DnName phuc vu custom thong tin cho  certificate

public class DnUtil {

    public static X500Name buildDnName(Staff staff) {
        // CN = Common Name (tên chính) => Họ tên sinh viên
        // OU = Organizational Unit => Lớp/ chuyen nganh
        // O  = Organization => Khoa / Trường
        // L  = Locality => Thành phố
        // ST = State/Province => Tỉnh / Thành phố
        // C  = Country => Quốc gia
        // EMAILADDRESS => Email sinh viên
        String dn = String.format(
                "CN=%s, OU=%s, ST=%s, C=%s, EMAILADDRESS=%s, UID=%s",
                staff.getFullName(),
                staff.getMajorName(),
                "Ha Noi",
                "Viet Nam",
                staff.getEmail(),
                staff.getStaffCode()
        );

        return new X500Name(dn);
    }
}

