package com.littlestore.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

@Service
public class GmailEmailService {

    private final Gmail gmail;

    public GmailEmailService(Gmail gmail) {
        this.gmail = gmail;
    }

    public void send(String to, String from, String subject, String bodyText)
            throws MessagingException, IOException {
        MimeMessage email = createEmail(to, from, subject, bodyText);
        Message message = createMessageWithEmail(email);
        gmail.users().messages().send(from, message).execute();
    }

    private MimeMessage createEmail(
            String to, String from, String subject, String bodyText)
            throws MessagingException {
        Session session = Session.getDefaultInstance(new Properties(), null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                           new InternetAddress(to));
        email.setSubject(subject);
        email.setContent(bodyText, "text/html; charset=utf-8");
//        email.setText(bodyText);
        return email;
    }

    private Message createMessageWithEmail(MimeMessage email)
            throws MessagingException, IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            email.writeTo(buffer);
            String encodedEmail =
                Base64.getUrlEncoder().encodeToString(buffer.toByteArray());
            Message message = new Message();
            message.setRaw(encodedEmail);
            return message;
        }
    }
}
