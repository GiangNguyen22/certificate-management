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
    public String generateCertificate(String studentCode, String templateId, InfoEechStudentInResSign studentInfo) throws Exception {
        Optional<Student> studentOpt = studentRepository.findByStudentCode(studentCode);
        if (studentOpt.isEmpty()) {
            throw new Exception("Student not found with studentCode: " + studentCode);
        }
        //Student student = studentOpt.get();
        Template template = templateService.getTemplateById(templateId);
        String nameTemplate = template.getName();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String issuedDate = LocalDate.now().format(formatter);

        File outDir = new File("certificates");
        if (!outDir.exists()) outDir.mkdirs();
        String outputPath = outDir.getAbsolutePath() + "/" + studentInfo.getStudentCode() + "_certificate.pdf";

        // Đọc PDF template từ resources thành byte[]
        try (InputStream is = getClass().getResourceAsStream("/templates/"+nameTemplate)) {
            if (is == null) {
                throw new FileNotFoundException("Template PDF not found at /templates/"+nameTemplate);
            }
            byte[] pdfBytes = is.readAllBytes();
            //vi doc file tu resources nen: Inputstream -> byte[]->Loader.loadPDF(byte[])
            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDPage page = document.getPage(0);

                // Load font từ resources
                try (InputStream fontStream = getClass().getResourceAsStream("/fonts/NotoSans-Italic.ttf");
                    //load stamp from resources
                    InputStream stampStream = getClass().getResourceAsStream("/stamps/stamp.png");
                    //load sign from resources
                    InputStream signStream = getClass().getResourceAsStream("/signs/signature_demo.png")
                ) {
                    if (fontStream == null) {
                        throw new FileNotFoundException("Font file not found at /fonts/NotoSans-Italic.ttf");
                    }
                    PDType0Font font = PDType0Font.load(document, fontStream);

                    //load stamps-> con dau
                    PDImageXObject stampImage = PDImageXObject.createFromByteArray(document, stampStream.readAllBytes(),"stamp");
                    PDImageXObject signImage = PDImageXObject.createFromByteArray(document, signStream.readAllBytes(),"signature_demo");
                    try (PDPageContentStream cs = new PDPageContentStream(
                            document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                        cs.setFont(font, 14);

                        // Name - Get from User entity (parent class)
                        cs.beginText();
                        cs.newLineAtOffset(330, 300);
                        cs.showText(studentInfo.getName());
                        cs.endText();

                        // Date of Birth - Get from User entity (parent class)
                        cs.beginText();
                        cs.newLineAtOffset(330, 278);
                        cs.showText(studentInfo.getDob());
                        cs.endText();

                        // Student Code
                        // cs.beginText();
                        // cs.newLineAtOffset(150, 460);
                        // cs.showText("Student Code: " + student.getStudentCode());
                        // cs.endText();

                        // Major
                        // cs.beginText();
                        // cs.newLineAtOffset(150, 440);
                        // cs.showText("Major: " + student.getMajorName());
                        // cs.endText();

                        // Graduation Year
                        cs.beginText();
                        cs.newLineAtOffset(330, 250);
                        cs.showText(studentInfo.getTimeStudied());
                        cs.endText();

                        // Classification - Use xepLoai from database
                        String classification = studentInfo.getXepLoai() != null && !studentInfo.getXepLoai().isEmpty()
                            ? studentInfo.getXepLoai()
                            : "Giỏi"; // Default fallback
                        cs.beginText();
                        cs.newLineAtOffset(330, 223);
                        cs.showText(classification);
                        cs.endText();

                        // Certificate No
                        String certNo = "CERT-" + studentInfo.getStudentCode();
                        cs.beginText();
                        cs.newLineAtOffset(230, 125);
                        cs.showText(certNo);
                        cs.endText();

                        // Issued Date
                        cs.beginText();
                        cs.newLineAtOffset(230, 86);
                        cs.showText(issuedDate);
                        cs.endText();
                        
                        //get time now
                        LocalDate today = LocalDate.now();
                        //day
                        cs.beginText();
                        cs.newLineAtOffset(578, 173);
                        cs.showText(String.valueOf(today.getDayOfMonth()));
                        cs.endText();
                        //month
                        cs.beginText();
                        cs.newLineAtOffset(645,173);
                        cs.showText(String.valueOf(today.getMonthValue()));
                        cs.endText();
                        //year
                        cs.beginText();
                        cs.newLineAtOffset(700, 173);
                        cs.showText(String.valueOf(today.getYear()));
                        cs.endText();
                        //Draw stamp
                        cs.drawImage(stampImage, 560, 30, 100, 100);
                        //Draw signature
                        cs.drawImage(signImage, 560, 25, 100, 50);


                    }
                }

                document.save(outputPath);
            }
        }
        return outputPath;
    }
}
