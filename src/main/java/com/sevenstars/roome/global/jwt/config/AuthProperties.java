package com.sevenstars.roome.global.jwt.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class AuthProperties {
    private final String secret;
    private final Long tokenValidityInSeconds;
    private final Long refreshTokenValidityInSeconds;
    private final String issuerUri;
}
