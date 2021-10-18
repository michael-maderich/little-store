package com.littlestore.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendSimpleEmail { 
	final String senderEmailId = "scruffqpons.com";
	final String senderPassword = "***REMOVED***812";
	final String emailSMTPserver = "smtp.gmail.com";

	public SendSimpleEmail(String receiverEmail, String subject, String messageText) {	

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", emailSMTPserver);
	 
		try { 			
			Authenticator auth = new SMTPAuthenticator();
	                Session session = Session.getInstance(props, auth);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(senderEmailId));
			message.setRecipients(Message.RecipientType.TO, 
					InternetAddress.parse(receiverEmail));
			message.setSubject(subject);
			message.setText(messageText);
	 
			Transport.send(message); 
			System.out.println("Order confirmation sent successfully."); 
	    } catch (Exception e) {
		e.printStackTrace();
	    System.err.println("Error in sending order confirmation.");
	   }
	}
	 
	private class SMTPAuthenticator extends 
	  javax.mail.Authenticator {
	    public PasswordAuthentication 
	       getPasswordAuthentication() {
	        return new PasswordAuthentication(senderEmailId, 
	        		senderPassword);
	    }
	}
}
