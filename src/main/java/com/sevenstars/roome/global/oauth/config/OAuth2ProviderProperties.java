package com.sevenstars.roome.global.oauth.config;

public interface OAuth2ProviderProperties {

    String getGrantType();

    String getClientId();

    String getClientSecret();

    String getTokenUri();

    String getRedirectUri();

    String getTokenRevokeUri();

    String getPublicKeyUri();

    String getIssuerUri();
}
