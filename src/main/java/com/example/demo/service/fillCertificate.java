package com.example.demo.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Date;
import com.example.demo.entity.Student;
import com.example.demo.entity.Template;
import com.example.demo.repository.StudentRepositoryI;
import com.example.demo.service.interfaces.TemplateService;
import com.example.demo.dto.request.InfoEechStudentInResSign;

@Service
public class fillCertificate {

    @Autowired
    private StudentRepositoryI studentRepository;
    @Autowired
    private TemplateService templateService;

//     public String generateCertificate(String studentCode, String templateId, InfoEechStudentInResSign studentInfo)
//             throws Exception {
//         Optional<Student> studentOpt = studentRepository.findByStudentCode(studentCode);
//         if (studentOpt.isEmpty()) {
//             throw new Exception("Student not found with studentCode: " + studentCode);
//         }
//         // Student student = studentOpt.get();
//         Template template = templateService.getTemplateById(templateId);
//         String nameTemplate = template.getName();
//         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//         String issuedDate = LocalDate.now().format(formatter);

//         File outDir = new File("certificates");
//         if (!outDir.exists())
//             outDir.mkdirs();
//         String outputPath = outDir.getAbsolutePath() + "/" + studentInfo.getStudentCode() + "_certificate.pdf";

//         // Đọc PDF template từ resources thành byte[]
//         try (InputStream is = getClass().getResourceAsStream("/templates/" + nameTemplate)) {
//             if (is == null) {
//                 throw new FileNotFoundException("Template PDF not found at /templates/" + nameTemplate);
//             }
//             byte[] pdfBytes = is.readAllBytes();
//             // vi doc file tu resources nen: Inputstream -> byte[]->Loader.loadPDF(byte[])
//             try (PDDocument document = Loader.loadPDF(pdfBytes)) {
//                 PDPage page = document.getPage(0);
//                 LocalDate today = LocalDate.now();
//                 // Load font từ resources
//                 try (InputStream fontStream = getClass().getResourceAsStream("/fonts/NotoSans-Italic.ttf");
//                         // load stamp from resources
//                         InputStream stampStream = getClass().getResourceAsStream("/stamps/stamp.png");
//                         // load sign from resources
//                         InputStream signStream = getClass().getResourceAsStream("/signs/signature_demo.png")) {
//                     if (fontStream == null) {
//                         throw new FileNotFoundException("Font file not found at /fonts/NotoSans-Italic.ttf");
//                     }
//                     PDType0Font font = PDType0Font.load(document, fontStream);

//                     // load stamps-> con dau
//                     PDImageXObject stampImage = PDImageXObject.createFromByteArray(document, stampStream.readAllBytes(),
//                             "stamp");
//                     PDImageXObject signImage = PDImageXObject.createFromByteArray(document, signStream.readAllBytes(),
//                             "signature_demo");
//                     try (PDPageContentStream cs = new PDPageContentStream(
//                             document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

//                         cs.setFont(font, 14);

//                         // Name - Get from User entity (parent class)
//                         cs.beginText();
//                         cs.newLineAtOffset(330, 300);
//                         cs.showText(studentInfo.getName());
//                         cs.endText();

//                         // Date of Birth - Get from User entity (parent class)
//                         cs.beginText();
//                         cs.newLineAtOffset(330, 278);
//                         cs.showText(new Date(studentInfo.getDob()).toLocaleString());
//                         cs.endText();

//                         // Student Code
//                         // cs.beginText();
//                         // cs.newLineAtOffset(150, 460);
//                         // cs.showText("Student Code: " + student.getStudentCode());
//                         // cs.endText();

//                         // Major
//                         // cs.beginText();
//                         // cs.newLineAtOffset(150, 440);
//                         // cs.showText("Major: " + student.getMajorName());
//                         // cs.endText();

//                         // Graduation Year
//                         cs.beginText();
//                         cs.newLineAtOffset(330, 250);
//                         cs.showText(String.valueOf(today.getYear()));
//                         cs.endText();

//                         // Classification - Use xepLoai from database
//                         String classification = studentInfo.getXepLoai() != null && !studentInfo.getXepLoai().isEmpty()
//                                 ? studentInfo.getXepLoai()
//                                 : "Giỏi"; // Default fallback
//                         cs.beginText();
//                         cs.newLineAtOffset(330, 223);
//                         cs.showText(classification);
//                         cs.endText();

//                         // Certificate No
//                         String certNo = "CERT-" + studentInfo.getStudentCode();
//                         cs.beginText();
//                         cs.newLineAtOffset(230, 125);
//                         cs.showText(certNo);
//                         cs.endText();

//                         // Issued Date
//                         cs.beginText();
//                         cs.newLineAtOffset(230, 86);
//                         cs.showText(issuedDate);
//                         cs.endText();

//                         // get time now

//                         // day
//                         cs.beginText();
//                         cs.newLineAtOffset(578, 173);
//                         cs.showText(String.valueOf(today.getDayOfMonth()));
//                         cs.endText();
//                         // month
//                         cs.beginText();
//                         cs.newLineAtOffset(645, 173);
//                         cs.showText(String.valueOf(today.getMonthValue()));
//                         cs.endText();
//                         // year
//                         cs.beginText();
//                         cs.newLineAtOffset(700, 173);
//                         cs.showText(String.valueOf(today.getYear()));
//                         cs.endText();
//                         // Draw stamp
//                         cs.drawImage(stampImage, 560, 30, 100, 100);
//                         // Draw signature
//                         cs.drawImage(signImage, 560, 25, 100, 50);

//                     }
//                 }

    //                 document.save(outputPath);
//             }
//         }
//         return outputPath;
//     }
// }
    public String generateCertificate(String studentCode, String templateId, InfoEechStudentInResSign studentInfo)
            throws Exception {
        Optional<Student> studentOpt = studentRepository.findByStudentCode(studentCode);
        if (studentOpt.isEmpty()) {
            throw new Exception("Student not found with studentCode: " + studentCode);
        }

        Template template = templateService.getTemplateById(templateId);
        String nameTemplate = template.getName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String issuedDate = LocalDate.now().format(formatter);

        File outDir = new File("certificates");
        if (!outDir.exists())
            outDir.mkdirs();
        String outputPath = outDir.getAbsolutePath() + "/" + studentInfo.getStudentCode() + "_certificate.pdf";

        // Đọc PDF template từ resources thành byte[]
        try (InputStream is = getClass().getResourceAsStream("/templates/" + nameTemplate)) {
            if (is == null) {
                throw new FileNotFoundException("Template PDF not found at /templates/" + nameTemplate);
            }
            byte[] pdfBytes = is.readAllBytes();

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDPage page = document.getPage(0);
                LocalDate today = LocalDate.now();

                // Load font từ resources
                try (InputStream fontStream = getClass().getResourceAsStream("/fonts/NotoSans-Italic.ttf");
                     InputStream stampStream = getClass().getResourceAsStream("/stamps/stamp.png");
                     InputStream signStream = getClass().getResourceAsStream("/signs/signature_demo.png")) {

                    if (fontStream == null) {
                        throw new FileNotFoundException("Font file not found at /fonts/NotoSans-Italic.ttf");
                    }

                    PDType0Font font = PDType0Font.load(document, fontStream);

                    // Load images
                    PDImageXObject stampImage = null;
                    PDImageXObject signImage = null;

                    if (stampStream != null) {
                        stampImage = PDImageXObject.createFromByteArray(document, stampStream.readAllBytes(), "stamp");
                    }
                    if (signStream != null) {
                        signImage = PDImageXObject.createFromByteArray(document, signStream.readAllBytes(), "signature_demo");
                    }

                    // SỬA: TÁCH RIÊNG TỪNG TEXT BLOCK - MỖI BLOCK CÓ CONTENT STREAM RIÊNG

                    // Block 1: Tên sinh viên
                    try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                        cs.setFont(font, 14);
                        cs.beginText();
                        cs.newLineAtOffset(330, 300);
                        cs.showText(studentInfo.getName());
                        cs.endText();
                    }

                    // Block 2: Ngày sinh (XỬ LÝ AN TOÀN)
                    try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                        cs.setFont(font, 14);
                        cs.beginText();
                        cs.newLineAtOffset(330, 278);
                        String dobText = studentInfo.getDob();
                        if (dobText != null && !dobText.isEmpty()) {
                            try {
                                // Format: yyyy-MM-dd -> dd/MM/yyyy
                                LocalDate dobDate = LocalDate.parse(dobText);
                                DateTimeFormatter dobFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                cs.showText(dobDate.format(dobFormatter));
                            } catch (Exception e) {
                                cs.showText(dobText); // Fallback: hiển thị nguyên bản
                            }
                        }
                        cs.endText();
                    }

                    // Block 3: Năm tốt nghiệp
                    try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                        cs.setFont(font, 14);
                        cs.beginText();
                        cs.newLineAtOffset(330, 250);
                        cs.showText(String.valueOf(today.getYear()));
                        cs.endText();
                    }

                    // Block 4: Xếp loại
                    try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                        cs.setFont(font, 14);
                        cs.beginText();
                        cs.newLineAtOffset(330, 223);
                        String classification = studentInfo.getXepLoai() != null && !studentInfo.getXepLoai().isEmpty()
                                ? studentInfo.getXepLoai()
                                : "Giỏi";
                        cs.showText(classification);
                        cs.endText();
                    }

                    // Block 5: Số chứng chỉ
                    try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                        cs.setFont(font, 14);
                        cs.beginText();
                        cs.newLineAtOffset(230, 125);
                        String certNo = "CERT-" + studentInfo.getStudentCode();
                        cs.showText(certNo);
                        cs.endText();
                    }

                    // Block 6: Ngày cấp
                    try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                        cs.setFont(font, 14);
                        cs.beginText();
                        cs.newLineAtOffset(230, 86);
                        cs.showText(issuedDate);
                        cs.endText();
                    }

                    // Block 7: Ngày (trong ngày tháng năm)
                    try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                        cs.setFont(font, 14);
                        cs.beginText();
                        cs.newLineAtOffset(578, 173);
                        cs.showText(String.valueOf(today.getDayOfMonth()));
                        cs.endText();
                    }

                    // Block 8: Tháng (trong ngày tháng năm)
                    try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                        cs.setFont(font, 14);
                        cs.beginText();
                        cs.newLineAtOffset(645, 173);
                        cs.showText(String.valueOf(today.getMonthValue()));
                        cs.endText();
                    }

                    // Block 9: Năm (trong ngày tháng năm)
                    try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                        cs.setFont(font, 14);
                        cs.beginText();
                        cs.newLineAtOffset(700, 173);
                        cs.showText(String.valueOf(today.getYear()));
                        cs.endText();
                    }

                    // Block 10: Vẽ images (con dấu và chữ ký)
                    if (stampImage != null || signImage != null) {
                        try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                            if (stampImage != null) {
                                cs.drawImage(stampImage, 560, 30, 100, 100);
                            }
                            if (signImage != null) {
                                cs.drawImage(signImage, 560, 25, 100, 50);
                            }
                        }
                    }

                } catch (Exception e) {
                    throw new RuntimeException("Error loading resources: " + e.getMessage(), e);
                }

                document.save(outputPath);
            }
        }
        return outputPath;
    }
}