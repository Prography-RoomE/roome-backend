package com.sevenstars.roome.global.auth.config;

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
