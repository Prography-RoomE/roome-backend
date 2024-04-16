package com.sevenstars.roome.global.oauth.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "google")
public class GoogleProperties {
    private String grantType;
    private String clientId;
    private String clientSecret;
    private String tokenUri;
    private String redirectUri;
    private String tokenRevokeUri;
    private String publicKeyUri;
    private String issuerUri;
}
