package com.sevenstars.roome.global.oauth.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties implements OAuth2ProviderProperties {
    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUri;
    private final String redirectUri;
    private final String tokenRevokeUri;
    private final String publicKeyUri;
    private final String issuerUri;
}
