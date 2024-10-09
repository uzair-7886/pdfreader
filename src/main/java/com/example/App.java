package com.example;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // System.out.println( "Hello World!" );
        PDFHandler pdfHandler = new PDFHandler();
        
        String pdfFilePath = "/home/uzair/Desktop/pdf-reader/pdfreader/src/main/resources/static/nwb/eki/WaterBoard_9_6769_2024_4.PDF";

        String pdfText = pdfHandler.readPDF(pdfFilePath);
        
        System.out.println(pdfText);

    }
}
