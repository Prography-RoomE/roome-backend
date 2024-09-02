package com.sevenstars.roome.domain.profile.entity.color;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ColorMode {
    SOLID("solid"),
    GRADIENT("gradient");

    @JsonValue
    private final String value;
}
