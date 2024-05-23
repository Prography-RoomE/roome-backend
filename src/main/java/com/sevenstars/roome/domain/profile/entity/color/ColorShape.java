package com.sevenstars.roome.domain.profile.entity.color;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ColorShape {
    LINEAR("linear"),
    RADIAL("radial"),
    ANGULAR("angular"),
    DIAMOND("diamond"),
    NONE("none");

    @JsonValue
    private final String value;
}
