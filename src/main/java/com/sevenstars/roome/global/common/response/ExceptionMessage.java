package com.sevenstars.roome.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    INVALID_TOKEN("Invalid token."),
    EXPIRED_TOKEN("Expired token."),
    PUBLIC_KEY_NOT_FOUND("Public key not found."),
    PROVIDER_NOT_FOUND("Provider not found.");

    private final String message;
}
