package com.littlestore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gmail")
public class GmailProperties {
    private String clientId     = System.getenv("GMAIL_CLIENT_ID");
    private String clientSecret = System.getenv("GMAIL_CLIENT_SECRET");
    private String redirectUri  = System.getenv("GMAIL_REDIRECT_URI");
    private String refreshToken = System.getenv("GMAIL_REFRESH_TOKEN");

    // getters + setters
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRedirectUri() {
		return redirectUri;
	}
	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
