package com.careerlens.api.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ResumeTextExtractorService {

    public String extractText(MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException(
                    "Resume file is empty"
            );
        }

        String fileName =
                file.getOriginalFilename();

        if (fileName == null ||
                fileName.trim().isEmpty()) {

            throw new RuntimeException(
                    "Invalid resume file name"
            );
        }

        String lowerFileName =
                fileName.toLowerCase();

        // PDF
        if (lowerFileName.endsWith(".pdf")) {
            return extractPdfText(file);
        }

        // DOCX
        if (lowerFileName.endsWith(".docx")) {
            return extractDocxText(file);
        }

        throw new RuntimeException(
                "Only PDF and DOCX files are supported"
        );
    }

    private String extractPdfText(
            MultipartFile file) throws IOException {

        byte[] fileBytes =
                file.getBytes();

        try (PDDocument document =
                     Loader.loadPDF(fileBytes)) {

            PDFTextStripper stripper =
                    new PDFTextStripper();

            return stripper.getText(document);
        }
    }

    private String extractDocxText(
            MultipartFile file) throws IOException {

        StringBuilder text =
                new StringBuilder();

        try (InputStream inputStream =
                     file.getInputStream();

             XWPFDocument document =
                     new XWPFDocument(inputStream)) {

            document.getParagraphs()
                    .forEach(paragraph -> {

                        text.append(
                                paragraph.getText()
                        );

                        text.append("\n");
                    });

            // Extract text from tables also
            document.getTables()
                    .forEach(table -> {

                        table.getRows()
                                .forEach(row -> {

                                    row.getTableCells()
                                            .forEach(cell -> {

                                                text.append(
                                                        cell.getText()
                                                );

                                                text.append(" ");
                                            });

                                    text.append("\n");
                                });
                    });
        }

        return text.toString();
    }
}