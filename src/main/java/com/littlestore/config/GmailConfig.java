package com.littlestore.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
public class GmailConfig {
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);

    @Value("${gmail.application-name}")
    private String appName;

    @Value("${gmail.credentials-file}")
    private String credentialsFilePath;

    @Value("${gmail.tokens-dir}")
    private String tokensDir;

    @Bean
    Gmail gmailService() throws GeneralSecurityException, IOException {
        // 1. Load client secrets.
        InputStream in = getClass().getResourceAsStream(
                credentialsFilePath.replace("classpath:", "/"));
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + credentialsFilePath);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // 2. Build flow and trigger user consent.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                clientSecrets,
                SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new File(tokensDir)))
            .setAccessType("offline")
            .build();

        // 3. Authorize (will open browser on first run).
        /*First‑run note: the authorize(...) call will spin up a small local HTTP server
         * and open your browser so you can consent. A refresh token is stored in ${user.home}/.gmail-tokens.*/
        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
            .setPort(8080)
            .setCallbackPath("/oauth2/callback")
            .build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver)
            .authorize("user");

        // 4. Build the Gmail client.
        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential)
            .setApplicationName(appName)
            .build();
    }
}
