package com.sevenstars.roome.domain.review.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewState {
    DRAFT("draft"),
    PUBLISHED("published");

    @JsonValue
    private final String value;
}
