package com.example;

import jakarta.mail.*;
import jakarta.mail.search.FlagTerm;

import java.util.Properties;

public class EmailReader {

    public void readEmailSubjects(String username, String password) {
        try {
            // Outlook IMAP settings
            String host = "outlook.office365.com";
            String mailStoreType = "imap";

            // Set properties for the mail session
            Properties properties = new Properties();
            properties.put("mail.store.protocol", mailStoreType);
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");

            // Establish a mail session
            Session emailSession = Session.getDefaultInstance(properties);

            // Create the IMAP store object and connect to the email account
            Store store = emailSession.getStore("imap");
            store.connect(host, username, password);

            // Open the inbox folder
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // Get all unread messages
            Message[] messages = emailFolder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

            // Print subjects of all emails
            for (Message message : messages) {
                System.out.println("Email Subject: " + message.getSubject());
            }

            // Close the folder and store
            emailFolder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
