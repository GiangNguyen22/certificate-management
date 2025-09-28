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
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepositoryI;

@Service
public class fillCertificate {

    @Autowired
    private StudentRepositoryI studentRepository;

    public String generateCertificate(String studentCode) throws Exception {
        Optional<Student> studentOpt = studentRepository.findByStudentCode(studentCode);
        if (studentOpt.isEmpty()) {
            throw new Exception("Student not found with studentCode: " + studentCode);
        }
        Student student = studentOpt.get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dobFormatted = student.getDob().format(formatter);
        String issuedDate = LocalDate.now().format(formatter);

        File outDir = new File("certificates");
        if (!outDir.exists()) outDir.mkdirs();
        String outputPath = outDir.getAbsolutePath() + "/" + student.getStudentCode() + "_certificate.pdf";

        // Đọc PDF template từ resources thành byte[]
        try (InputStream is = getClass().getResourceAsStream("/templates/Cert.pdf")) {
            if (is == null) {
                throw new FileNotFoundException("Template PDF not found at /templates/Cert.pdf");
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

                        // Name
                        cs.beginText();
                        cs.newLineAtOffset(330, 300);
                        cs.showText(student.getName());
                        cs.endText();

                        // Date of Birth
                        cs.beginText();
                        cs.newLineAtOffset(330, 278);
                        cs.showText(student.getDob().format(formatter));
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
                        cs.showText(student.getYear());
                        cs.endText();

                        // Classification
                        String classification = "Giỏi";
                        cs.beginText();
                        cs.newLineAtOffset(330, 223);
                        cs.showText(classification);
                        cs.endText();

                        // Certificate No
                        String certNo = "CERT-" + student.getStudentCode();
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
