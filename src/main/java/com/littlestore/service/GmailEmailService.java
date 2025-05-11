package com.littlestore.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.client.auth.oauth2.TokenResponseException;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailSendException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

@Service
@Lazy
public class GmailEmailService {

    private final Gmail gmail;

    public GmailEmailService(@Lazy Gmail gmail) {
        this.gmail = gmail;
    }

    public void send(String to, String from, String subject, String bodyText)
            throws MessagingException, IOException {
        MimeMessage email = createEmail(to, from, subject, bodyText);
        Message message = createMessageWithEmail(email);
        try {
        	gmail.users().messages().send(from, message).execute();
        }
        catch (TokenResponseException e) {
    	  throw new MailSendException("E-mail service is down (OAuth token invalid). Please re-authorize your GMAIL_REFRESH_TOKEN.");
        }
        catch (IOException e) {
            // other I/O errors
            throw new MailSendException("E-mail service is temporarily unavailable.", e);
        }
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
