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

    // public static void main(String[] args) {
    //     EmailReader reader = new EmailReader();
    //     String email = "uzairk7886@outlook.com";
    //     String accessToken = "eyJ0eXAiOiJKV1QiLCJub25jZSI6InFqOW1EQ1hHNzhWeFk3RGN6eUl0NWJtWXpLam92NXBKSk9sOW9qaTU3anciLCJhbGciOiJSUzI1NiIsIng1dCI6IjNQYUs0RWZ5Qk5RdTNDdGpZc2EzWW1oUTVFMCIsImtpZCI6IjNQYUs0RWZ5Qk5RdTNDdGpZc2EzWW1oUTVFMCJ9.eyJhdWQiOiIwMDAwMDAwMy0wMDAwLTAwMDAtYzAwMC0wMDAwMDAwMDAwMDAiLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC8yOTc0OWVmNi0zZDYxLTRjYmEtOTQ2Ny0zYWU3ZTQ2NDAxZWUvIiwiaWF0IjoxNzI5MDgyNjUxLCJuYmYiOjE3MjkwODI2NTEsImV4cCI6MTcyOTA4NzExOSwiYWNjdCI6MCwiYWNyIjoiMSIsImFpbyI6IkFZUUFlLzhZQUFBQWRyRU5kSVJhRHVyczlrV3lMRTBDMzdwcWx1VWhNaDR3VjZRaG5TblZBSnl1dFl4RHhvQ083c1NaNGZWMmhzUjFka0s3Vm56c1o4Vzgzd1J4T1dRUHRaZnFmSnVuNXZCSHdtcnZLdjY1allVVU9oMmJ4bDhZNUNLVnU3dTE2a3c4dWxCRW0zMGVuUjJXUi91RXdZVzhBRHJ6SncrdWRaOTRvMVRjb295ckprND0iLCJhbHRzZWNpZCI6IjE6bGl2ZS5jb206MDAwM0JGRkU4ODBGNEUwMiIsImFtciI6WyJwd2QiLCJtZmEiXSwiYXBwX2Rpc3BsYXluYW1lIjoiUmVhZE91dGxvb2tFbWFpbHMiLCJhcHBpZCI6Ijc4ZGNlMjFhLTliYWYtNDBlYy1iNzg5LTM5ZDhjYTlkYzVjYiIsImFwcGlkYWNyIjoiMSIsImVtYWlsIjoidXphaXJrNzg4NkBvdXRsb29rLmNvbSIsImZhbWlseV9uYW1lIjoiS2hhbiIsImdpdmVuX25hbWUiOiJVemFpciIsImlkcCI6ImxpdmUuY29tIiwiaWR0eXAiOiJ1c2VyIiwiaXBhZGRyIjoiMTExLjY4Ljk3LjE5NiIsIm5hbWUiOiJVemFpciBLaGFuIiwib2lkIjoiYWFjNmM2YzYtMTBmNC00N2YzLTgzNDgtMGRjMTg2MjFkODAxIiwicGxhdGYiOiI4IiwicHVpZCI6IjEwMDMyMDAzRTJEOTU3RTgiLCJyaCI6IjAuQVVFQjlwNTBLV0U5dWt5VVp6cm41R1FCN2dNQUFBQUFBQUFBd0FBQUFBQUFBQUJDQVJrLiIsInNjcCI6Ik1haWwuUmVhZFdyaXRlIE1haWwuU2VuZCBwcm9maWxlIG9wZW5pZCBlbWFpbCIsInNpZ25pbl9zdGF0ZSI6WyJrbXNpIl0sInN1YiI6IlYxNmZieFBsRTMyTVBrbFBLRFZjY3ZZbVJrQ3JVeTg2THZJODF3ZGc5WUUiLCJ0ZW5hbnRfcmVnaW9uX3Njb3BlIjoiRVUiLCJ0aWQiOiIyOTc0OWVmNi0zZDYxLTRjYmEtOTQ2Ny0zYWU3ZTQ2NDAxZWUiLCJ1bmlxdWVfbmFtZSI6ImxpdmUuY29tI3V6YWlyazc4ODZAb3V0bG9vay5jb20iLCJ1dGkiOiI1RFM4bUNCQktrV3hMRnNqWjZNdEFBIiwidmVyIjoiMS4wIiwid2lkcyI6WyI2MmU5MDM5NC02OWY1LTQyMzctOTE5MC0wMTIxNzcxNDVlMTAiLCJiNzlmYmY0ZC0zZWY5LTQ2ODktODE0My03NmIxOTRlODU1MDkiXSwieG1zX2lkcmVsIjoiMSAyNiIsInhtc19zdCI6eyJzdWIiOiJPNXNDQUpZMjB2X0RlRUdDYktVSklIa19NenhZZTdldzhwTjlEcWNZVnNnIn0sInhtc190Y2R0IjoxNzI4OTk3Mzk0fQ.WDRJLiR0h-688ul456nA29Uq20TOwHs32Spt4sd2caTU9ShAqOxATmHQJqglT-qWoV_ol1HcV429rEOH2W1wBYjS3B6ORWrXRrFx8rZjA20CdXjbod8XBKsMOTFKYeB_HjRQ5WTCHgoDxck-SCEuSxB9ERUGNH-l4cavOgy2AH-ww67NlS7dx7G7YKymMfBK1UNNpJdoGkV5uiizj1Jpf2eMBLehZ8iObVxyvs54BE_JlSjG3pElX4wk-T3J07Lkrd0wwQoUxC_f00mbBZacS8nGC8b4jD2mTy-j6Q6nleJVY3_hO1KwLksQwTzpLOzcqD8MGWHq6FzOfcD7ZdBcIA\",\"refresh_token\":\"0.AUEB9p50KWE9ukyUZzrn5GQB7hri3Hivm-xAt4k52MqdxctCARk.AgABAwEAAADW6jl31mB3T7ugrWTT8pFeAwDs_wUA9P8BGQjR6KOcZrykaGMkxWi0rGF3VYRvmk67eQBjNQsfbUTxYtWrQDjddmkjX4dyrTc8piFboAqgQ4VxuvigMm7LDyf_UwGA3OvrK5Jr_FphJ3LAi7yq0u1NcB3z3oWVepLWaURluDgIrCZEoPZR8wR_mBYT2HBjpTqaDTyukKl3bpdxDCTA6hB7KC3oz40Ts_D0w9QkK0CWd1_C7GcMqZcjJlBHVxWtEibCZ9rJHCu5PkLHXs1EX8u34PV0DBlZORnXWkHZssViX_YpITkxe3O8O2YqbXsuEl6csyteHa4ulJMOfx-_pg4DxiGPtLVJSKPoJFV6E3kHMAs5KExIlBkoqianAjWcXW44nPwudZ6Sn98xvdXQNp02h5rPRxloVbaQD-gtTGm7ykCRabDb3JvAZsykpHc-RFnyu-7c9mhoC5s5ABvRV_xD3vWs90OJ4RJSFg6khGlrKuZSMqAHNz5qRz6Jv70_unlCOsurX7rdgtZQqzHG1rVtanteYPg4BKWDzNm6JYIQ9h5gmQcRxXfowcrN3w3YVf8Zp3GoYIMLr3G0ejIYEXYnTcu_p-TPhMZg3YCu5UtzdlgXToMxMtL6RZ_Gy2f06VDHzd3RHPuQpwKQGqFZKBOPoIkRjCJKcfl8SXAfUl8KqvIdjSYgkozfO33Y09BAxx2zaylGsM6urCOrPgDLF4kBtzSRiFxE-mE7vkGUw76zyo2QRPKD5mYbg5OJOUsNwjSzGWN1P4_n8OXzor0v3lNipwLOemCp3zu_FfNzBbYADPi-dKiLVuYUCLTDNNxfzh9e7jdHAUtmvJbBgi3zr6DmfZlulLAC7QfSb8iSNomG3ZWzb5C8UClTzgBMsXjx4LAQMc55zfnmEyz6VX9VH3qn9-BiZK9irNLuQtHnS9-uVpumf4TYI8ok69Tdd8qhj0L26OA3xs5Y8gsgdfw5XxvxQrBnN9gN_boG0ZDh_hFiCc2h2ZdNDmFHOAKYi-mDvp2KwC62lTsjTVXErmCyX9COOMRoBqKo49mcPQ-bi_2sk2Lpde8BqlYCAhVq9CDrUsfK8MkXn_S5lm3QHYemDxwB5pGx2jWOGFIJtzePIKk-DriW6leI7bjAjza55X_Pugu7tpN_HaZjNco8GoAVSaYOY_--OWKqR_UuR6PCVMtzVjOMXPn04Rw9uypnBeD2bEQheChu--Bg2aWjyfAY-QyuA7wV34FXLdNitWOT-7tUQF3zy5OHAqARQSGWF5e0wpnlxD05EVrnqbTxfMXTimC5k2hosFbZwhBM8TB88KZWYgqHtAUbzWdjMde7wZ43_TBw5pH6FmZ5Ibg6fbebEphYtFZwYEr7QpJNLUESKK5ZmLI9YxKyTN8XLfnUTv5ee-9GzdzuGpuNnXltZiWk13goZBQ1rucx4Ek08eBOiqvehKgF6hAUSuJJZvBSKK1i8SolBPOvdcLOippwO6YAQQefkNsupQ-ioETw2LlQhGu7gRzQUSUM6j-YlUgJfnSDroEymTfWXQXSnIk5zmt00yjX2r6263tU0cEpGfpdP1-3";
    //     reader.readEmailSubjects(email, accessToken);
    // }

    private static void processFolder(String folderPath, PDFHandler pdfHandler, List<Map<String, String>> allBillData, String billType) throws IOException {
        List<String> buildingFolders = listBuildingFolders(folderPath);
        
        for (String buildingFolder : buildingFolders) {
            File folder = new File(buildingFolder);
            File[] files = folder.listFiles((dir, name) -> {
                if (billType.equals("eac")) {
                    return name.toLowerCase().endsWith(".pdf") && Character.isDigit(name.charAt(0));
                } else {
                    return name.toLowerCase().endsWith(".pdf") && name.startsWith("WaterBoard_");
                }
            });
            
            if (files != null && files.length > 0) {
                String pdfFilePath = files[0].getAbsolutePath();
                String pdfText = pdfHandler.readPDF(pdfFilePath);
                Map<String, String> billData;
                
                if (billType.equals("eac")) {
                    billData = extractElectricityBillInformation(pdfText);
                } else {
                    billData = extractWaterBillInformation(pdfText);
                }
                
                billData.put("Building", folder.getName());
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