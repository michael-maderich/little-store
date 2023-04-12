//package com.littlestore.utils;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Properties;
//
//import javax.mail.Message;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import com.littlestore.service.GeneralDataService;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Properties;
//
//import javax.activation.DataHandler;
//import javax.activation.DataSource;
//import javax.mail.BodyPart;
//import javax.mail.MessagingException;
//import javax.mail.Multipart;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
//import javax.mail.internet.MimeUtility;
//
//import com.google.api.client.auth.oauth2.BearerToken;
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.auth.oauth2.StoredCredential;
//import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
//import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
//import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.gmail.Gmail;
//import com.google.api.services.gmail.GmailScopes;
//import com.google.api.services.gmail.model.Message;
//
//public class GmailSender {
//  private static final String APPLICATION_NAME = "My Gmail Application";
//  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//  private static HttpTransport httpTransport;
//  private static final String USER_ID = "me";
//
//  static {
//    try {
//      httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
//
//  private static Credential authorize() throws Exception {
//    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
//        GmailSender.class.getResourceAsStream("/client_secret.json"));
//    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport,
//        JSON_FACTORY, clientSecrets, GmailScopes.all()).build();
//    VerificationCodeReceiver receiver = new LocalServerReceiver.Builder().setHost(
//        "localhost").setPort(8888).build();
//    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
//  }
//
//  public static void sendMessage(String recipientEmail, String subject, String body) throws Exception {
//    Credential credential = authorize();
//    Gmail service = new Gmail.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
//        APPLICATION_NAME).build();
//
//    Message message = createMessageWithEmail(createEmail(recipientEmail, USER_ID, subject, body));
//    sendMessage(service, USER_ID, message);
//  }
//
//  private static MimeMessage createEmail(String to, String from, String subject,
//      String bodyText) throws MessagingException {
//    Properties props = new Properties();
//    Session session = Session.getDefaultInstance(props, null);
//
//    MimeMessage email = new MimeMessage(session);
//    email.setFrom(new InternetAddress(from));
//    email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
//    email.setSubject(subject);
//
//    MimeBodyPart mimeBodyPart = new MimeBodyPart();
//  }
//}
