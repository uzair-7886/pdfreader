package com.example;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFHandler {

    public String readPDF(String filePath) {
        StringBuilder pdfText = new StringBuilder();
        
        File file = new File(filePath);
        try (PDDocument document = Loader.loadPDF(file)) {
            if (!document.isEncrypted()) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                pdfText.append(pdfStripper.getText(document));
                // System.out.println("read successfully inside");
            } else {
                System.out.println("The PDF document is encrypted.");
            }
        } catch (IOException e) {
            System.err.println("Error reading the PDF file: " + e.getMessage());
        }
        // System.out.println("read successfully outside...");
        return pdfText.toString();
    }
}