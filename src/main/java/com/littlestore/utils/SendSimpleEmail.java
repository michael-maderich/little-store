package com.littlestore.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SendSimpleEmail {
	final String senderEmailId = "thelittlestoregoods@gmail.com";
	final String emailSMTPserver = "smtp.gmail.com";
	String TOKEN_URL = "https://www.googleapis.com/oauth2/v4/token";
	String oauthClientId = "***REMOVED***";
	String oauthSecret = "***REMOVED***";
	String refreshToken = "***REMOVED***";
	String accessToken = "***REMOVED***";
	long tokenExpires = 0;

	public SendSimpleEmail(String recipient, String subject, String message) {

		if (System.currentTimeMillis() > tokenExpires) {
		    try {
		        String request = "client_id=" + URLEncoder.encode(oauthClientId, "UTF-8")
		                + "&client_secret=" + URLEncoder.encode(oauthSecret, "UTF-8")
		                + "&refresh_token=" + URLEncoder.encode(refreshToken, "UTF-8")
		                + "&grant_type=refresh_token";
		        HttpURLConnection conn = (HttpURLConnection) new URL(TOKEN_URL).openConnection();
		        conn.setDoOutput(true);
		        conn.setRequestMethod("POST");
		        PrintWriter out = new PrintWriter(conn.getOutputStream());
		        out.print(request); // note: println causes error
		        out.flush();
		        out.close();
		        conn.connect();
		        try {
		            HashMap<String, Object> result;
		            result = new ObjectMapper().readValue(conn.getInputStream(), new TypeReference<HashMap<String, Object>>() {
		            });
		            accessToken = (String) result.get("access_token");
		            tokenExpires = System.currentTimeMillis() + (((Number) result.get("expires_in")).intValue() * 1000);
		        }
		        catch (IOException e) {
		            String line;
		            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		            while ((line = in.readLine()) != null) {
		                System.out.println(line);
		            }
		            System.out.flush();
		        }
				Properties props = new Properties();
				props.put("mail.smtp.ssl.enable", "true"); // required for Gmail
				props.put("mail.smtp.sasl.enable", "true");
				props.put("mail.smtp.sasl.mechanisms", "XOAUTH2");
				props.put("mail.smtp.auth.login.disable", "true");
				props.put("mail.smtp.auth.plain.disable", "true");
				
				Session session = Session.getInstance(props);
				Message msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(senderEmailId));
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

				msg.setSubject(subject);
				msg.setSentDate(new Date());
				msg.setText(message);
				msg.saveChanges();

				Transport transport = session.getTransport("smtp");
				transport.connect("smtp.gmail.com", senderEmailId, accessToken);
				transport.send(msg, msg.getAllRecipients());
		    }
		    catch (Exception e) {
		        e.printStackTrace();
		    }
		}
	}
}
