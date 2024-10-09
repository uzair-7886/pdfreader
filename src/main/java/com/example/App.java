package com.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        PDFHandler pdfHandler = new PDFHandler();
        
        String pdfFilePath = "/home/uzair/Desktop/pdf-reader/pdfreader/src/main/resources/static/eac/eki/elec.pdf";

        String pdfText = pdfHandler.readPDF(pdfFilePath);
        
        // System.out.println(pdfText);

        extractElectricityBillInformation(pdfText);
    }

    private static void extractElectricityBillInformation(String text) {
        String amountDuePattern = "Πληρωτέο μέχρι\\s*€([\\d.,]+)";
        String dueDatePattern = "Πληρωτέο μέχρι\\s*(\\d{2}/\\d{2}/\\d{4})";
        String accountNumberPattern = "Αριθμός Λογαριασμού\\s*(\\d{10})";
        String billingPeriodPattern = "Περίοδος Κατανάλωσης\\s*(\\d{2}/\\d{2}/\\d{4} - \\d{2}/\\d{2}/\\d{4})";

        Pattern amountPattern = Pattern.compile(amountDuePattern);
        Pattern datePattern = Pattern.compile(dueDatePattern);
        Pattern billingPeriodPatternCompiled = Pattern.compile(billingPeriodPattern);

        Matcher amountMatcher = amountPattern.matcher(text);
        Matcher dateMatcher = datePattern.matcher(text);
        Matcher billingPeriodMatcher = billingPeriodPatternCompiled.matcher(text);

        if (amountMatcher.find()) {
            System.out.println("Amount Due: €" + amountMatcher.group(1));
        } else {
            System.out.println("Amount Due not found.");
        }

        if (dateMatcher.find()) {
            System.out.println("Due Date: " + dateMatcher.group(1));
        } else {
            System.out.println("Due Date not found.");
        }

        if (billingPeriodMatcher.find()) {
            System.out.println("Billing Period: " + billingPeriodMatcher.group(1));
        } else {
            System.out.println("Billing Period not found.");
        }
    }
}