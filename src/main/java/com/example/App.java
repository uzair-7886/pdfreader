package com.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App 
{
    public static void main( String[] args )
    {
        PDFHandler pdfHandler = new PDFHandler();
        
        String pdfFilePath = "/home/uzair/Desktop/pdf-reader/pdfreader/src/main/resources/static/eac/eki/elec.pdf";
        String pdfText = pdfHandler.readPDF(pdfFilePath);
        
        extractElectricityBillInformation(pdfText);
    }

    private static void extractElectricityBillInformation(String text) {
        String amountDuePattern = "Πληρωτέο μέχρι\\s*€([\\d.,]+)";
        String dueDatePattern = "Πληρωτέο μέχρι\\s*(\\d{2}/\\d{2}/\\d{4})";
        String billingPeriodPattern = "Περίοδος Κατανάλωσης\\s*(\\d{2}/\\d{2}/\\d{4} - \\d{2}/\\d{2}/\\d{4})";
    
        String numbersPattern = "(\\d{3}\\s*\\d{3}\\s*\\d{4}\\s*\\d?)\\s*(\\d{6})\\s*(\\d{3}\\s*\\d{3}\\s*\\d{4}\\s*\\d)";
    
        Pattern amountPattern = Pattern.compile(amountDuePattern);
        Pattern datePattern = Pattern.compile(dueDatePattern);
        Pattern billingPeriodPatternCompiled = Pattern.compile(billingPeriodPattern);
        Pattern numbersPatternCompiled = Pattern.compile(numbersPattern);
    
        Matcher amountMatcher = amountPattern.matcher(text);
        Matcher dateMatcher = datePattern.matcher(text);
        Matcher billingPeriodMatcher = billingPeriodPatternCompiled.matcher(text);
        Matcher numbersMatcher = numbersPatternCompiled.matcher(text);
    
        extractAndPrint("Amount Due", amountMatcher, "€");
        extractAndPrint("Due Date", dateMatcher, "");
        extractAndPrint("Billing Period", billingPeriodMatcher, "");
        
        if (numbersMatcher.find()) {
            System.out.println("Building Number: " + numbersMatcher.group(1).replaceAll("\\s", ""));
            System.out.println("Meter Number: " + numbersMatcher.group(2));
            System.out.println("Account Number: " + numbersMatcher.group(3).replaceAll("\\s", ""));
        } else {
            System.out.println("Account, Meter, and Building numbers not found.");
        }
    }
    
    private static void extractAndPrint(String label, Matcher matcher, String prefix) {
        if (matcher.find()) {
            System.out.println(label + ": " + prefix + matcher.group(1).trim());
        } else {
            System.out.println(label + " not found.");
        }
    }
}