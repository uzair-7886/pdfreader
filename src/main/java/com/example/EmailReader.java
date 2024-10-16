package com.example;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import java.util.Properties;

public class EmailReader {

    public void readEmailSubjects(String username, String accessToken) {
        Properties properties = new Properties();
        
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "outlook.office365.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");
        properties.put("mail.imaps.auth.mechanisms", "XOAUTH2");
        properties.put("mail.imaps.auth.login.disable", "true");
        properties.put("mail.imaps.auth.plain.disable", "true");
        
        properties.put("mail.debug", "true");
        properties.put("mail.debug.auth", "true");

        try {
            Session session = Session.getInstance(properties);
            
            Store store = session.getStore("imaps");

            // Set up SASL XOAUTH2 authentication
            final String oauthToken = "user=" + username + "\001auth=Bearer " + accessToken + "\001\001";
            store.connect("outlook.office365.com", username, oauthToken);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();

            for (Message message : messages) {
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + InternetAddress.toString(message.getFrom()));
                System.out.println("Sent Date: " + message.getSentDate());
                System.out.println("--------------------");
            }

            inbox.close(false);
            store.close();

        } catch (MessagingException e) {
            System.err.println("An error occurred while trying to read emails:");
            e.printStackTrace();
        }
    }
}