// package com.example;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Properties;
// import javax.mail.*;

// public class EmailHandler {
//     public List<String> readEmailSubjects() throws MessagingException {
//         List<String> subjects = new ArrayList<>();
        
//         Properties properties = new Properties();
//         properties.put("mail.store.protocol", "imaps");
        
//         Session session = Session.getDefaultInstance(properties, null);
//         Store store = session.getStore("imaps");
        
//         // You need to replace these with your actual Outlook credentials
//         store.connect("outlook.office365.com", "your.email@outlook.com", "your_password");
        
//         Folder inbox = store.getFolder("INBOX");
//         inbox.open(Folder.READ_ONLY);
        
//         Message[] messages = inbox.getMessages();
//         for (Message message : messages) {
//             subjects.add(message.getSubject());
//         }
        
//         inbox.close(false);
//         store.close();
        
//         return subjects;
//     }
// }