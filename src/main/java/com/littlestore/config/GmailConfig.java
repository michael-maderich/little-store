package com.littlestore.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class GmailConfig {

    private static final String APPLICATION_NAME = "little-store";
    private final GmailProperties props;

    public GmailConfig(GmailProperties props) {
        this.props = props;
    }

    @Bean
    @Lazy
    Gmail gmailService() throws GeneralSecurityException, IOException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        // Read environment variables
        String clientId     = props.getClientId();
        String clientSecret = props.getClientSecret();
        String refreshToken = props.getRefreshToken();

        if (clientId == null || clientSecret == null || refreshToken == null) {
            throw new IllegalStateException("Gmail credentials are not set in environment variables.");
        }

        // Build credentials
        UserCredentials credentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();

        // Optionally, force a token refresh immediately
        credentials.refresh();

        return new Gmail.Builder(
                httpTransport,
                jsonFactory,
                new HttpCredentialsAdapter(credentials)
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }
}
