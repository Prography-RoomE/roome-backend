package com.sevenstars.roome.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserState {
    TERMS_AGREEMENT("termsAgreement"),
    NICKNAME("nickname"),
    REGISTRATION_COMPLETED("registrationCompleted");

    @JsonValue
    private final String value;
}
