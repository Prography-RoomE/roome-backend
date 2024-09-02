package com.sevenstars.roome.global.auth.entity;

import lombok.Data;

@Data
public class OAuth2ProviderToken {
    private final String accessToken;
    private final String idToken;
}
