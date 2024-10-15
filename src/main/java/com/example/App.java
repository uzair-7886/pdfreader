package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        PDFHandler pdfHandler = new PDFHandler();
        String baseFolder = "/home/uzair/Desktop/pdf-reader/pdfreader/src/main/resources/static/";
        
        try {
            List<Map<String, String>> allBillData = new ArrayList<>();

            processFolder(baseFolder + "eac", pdfHandler, allBillData, "eac");

            processFolder(baseFolder + "nwb", pdfHandler, allBillData, "nwb");

            generateExcelReport(allBillData, baseFolder + "utility_bills_report.xlsx");
            System.out.println("Excel report generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFolder(String folderPath, PDFHandler pdfHandler, List<Map<String, String>> allBillData, String billType) throws IOException {
        List<String> buildingFolders = listBuildingFolders(folderPath);
        
        for (String buildingFolder : buildingFolders) {
            String pdfFilePath = buildingFolder + "/" + (billType.equals("eac") ? "elec.pdf" : "water.pdf");
            File pdfFile = new File(pdfFilePath);
            
            if (pdfFile.exists()) {
                String pdfText = pdfHandler.readPDF(pdfFilePath);
                Map<String, String> billData;
                
                if (billType.equals("eac")) {
                    billData = extractElectricityBillInformation(pdfText);
                } else {
                    billData = extractWaterBillInformation(pdfText);
                }
                
                billData.put("Building", new File(buildingFolder).getName());
                billData.put("Bill Type", billType.toUpperCase());
                
                // Fill missing fields with "NA"
                String[] requiredFields = {"Building", "Amount Due", "Due Date", "Billing Period", "Building Number", "Meter Number", "Account Number", "Bill Type"};
                for (String field : requiredFields) {
                    if (!billData.containsKey(field)) {
                        billData.put(field, "NA");
                    }
                }
                
                allBillData.add(billData);
            }
        }
    }

    private static List<String> listBuildingFolders(String baseFolder) throws IOException {
        return Files.list(Paths.get(baseFolder))
                .filter(Files::isDirectory)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    private static Map<String, String> extractElectricityBillInformation(String text) {
        Map<String, String> billData = new HashMap<>();

        String amountDuePattern = "Πληρωτέο μέχρι\\s*€([\\d.,]+)";
        String dueDatePattern = "Πληρωτέο μέχρι\\s*(\\d{2}/\\d{2}/\\d{4})";
        String billingPeriodPattern = "Περίοδος Κατανάλωσης\\s*(\\d{2}/\\d{2}/\\d{4} - \\d{2}/\\d{2}/\\d{4})";
        String numbersPattern = "(\\d{3}\\s*\\d{3}\\s*\\d{4}\\s*\\d?)\\s*(\\d{6})\\s*(\\d{3}\\s*\\d{3}\\s*\\d{4}\\s*\\d)";

        extractAndAdd(billData, "Amount Due", text, amountDuePattern, "€");
        extractAndAdd(billData, "Due Date", text, dueDatePattern, "");
        extractAndAdd(billData, "Billing Period", text, billingPeriodPattern, "");

        Pattern numbersPatternCompiled = Pattern.compile(numbersPattern);
        Matcher numbersMatcher = numbersPatternCompiled.matcher(text);

        if (numbersMatcher.find()) {
            billData.put("Building Number", numbersMatcher.group(1).replaceAll("\\s", ""));
            billData.put("Meter Number", numbersMatcher.group(2));
            billData.put("Account Number", numbersMatcher.group(3).replaceAll("\\s", ""));
        }

        return billData;
    }

    private static Map<String, String> extractWaterBillInformation(String text) {
        Map<String, String> billData = new HashMap<>();
    
        String billInfoPattern = "(\\d+\\s+\\d+\\s+\\d+)\\s+(\\d{2}/\\d{2}-\\s*\\d{2}/\\d{2})\\s+(\\d+\\.\\d+)";
        String dueDatePattern = "(\\d{2}/\\d{2}/(\\d{4}))";
    
        Pattern billInfoPatternCompiled = Pattern.compile(billInfoPattern);
        Matcher billInfoMatcher = billInfoPatternCompiled.matcher(text);
    
        if (billInfoMatcher.find()) {
            billData.put("Account Number", billInfoMatcher.group(1).replaceAll("\\s", ""));
            String billingPeriodWithoutYear = billInfoMatcher.group(2);
            billData.put("Amount Due", "€" + billInfoMatcher.group(3));
    
            int endOfMatch = billInfoMatcher.end();
            String remainingText = text.substring(endOfMatch);
            Pattern dueDatePatternCompiled = Pattern.compile(dueDatePattern);
            Matcher dueDateMatcher = dueDatePatternCompiled.matcher(remainingText);
    
            if (dueDateMatcher.find()) {
                String dueDate = dueDateMatcher.group(1);
                billData.put("Due Date", dueDate);
    
                String year = dueDateMatcher.group(2);
    
                String[] periodParts = billingPeriodWithoutYear.split("-");
                String startDate = periodParts[0].trim();
                String endDate = periodParts[1].trim();
    
                String startYear = year;
                String endYear = year;
                if (Integer.parseInt(endDate.split("/")[1]) < Integer.parseInt(startDate.split("/")[1])) {
                    endYear = String.valueOf(Integer.parseInt(year) + 1);
                }
    
                String fullBillingPeriod = startDate + "/" + startYear + " - " + endDate + "/" + endYear;
                billData.put("Billing Period", fullBillingPeriod);
            }
        }
    
        return billData;
    }
    
    private static void extractAndAdd(Map<String, String> billData, String label, String text, String patternString, String prefix) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            billData.put(label, prefix + matcher.group(1).trim());
        }
    }

    private static void generateExcelReport(List<Map<String, String>> allBillData, String outputPath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Billing Information");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"Building", "Amount Due", "Due Date", "Billing Period", "Building Number", "Meter Number", "Account Number", "Bill Type"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowNum = 1;
            for (Map<String, String> billData : allBillData) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(billData.getOrDefault(columns[i], "NA"));
                }
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                workbook.write(outputStream);
            }
        }
    }
}