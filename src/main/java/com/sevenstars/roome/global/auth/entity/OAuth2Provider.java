package com.sevenstars.roome.global.auth.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {

    APPLE("apple"),
    GOOGLE("google"),
    KAKAO("kakao");

    @JsonValue
    private final String name;
}
