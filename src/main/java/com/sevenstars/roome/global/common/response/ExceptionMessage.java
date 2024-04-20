package com.sevenstars.roome.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    INVALID_TOKEN("Invalid token."),
    EXPIRED_TOKEN("Expired token."),
    PUBLIC_KEY_NOT_FOUND("Public key not found."),
    PROVIDER_NOT_FOUND("Provider not found."),
    PROVIDER_INVALID_RESPONSE("Provider response is invalid."),
    PUBLIC_KEY_UPDATE_FAIL("Public key update failed."),
    INVALID_ID("Invalid ID.");
    private final String message;
}
