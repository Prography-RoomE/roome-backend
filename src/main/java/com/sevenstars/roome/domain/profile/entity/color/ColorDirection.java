package com.sevenstars.roome.domain.profile.entity.color;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ColorDirection {
    TOP_LEFT_TO_BOTTOM_RIGHT("topLeftToBottomRight"),
    TOP_RIGHT_TO_BOTTOM_LEFT("topRightToBottomLeft"),
    BOTTOM_LEFT_TO_TOP_RIGHT("bottomLeftToTopRight"),
    BOTTOM_RIGHT_TO_TOP_LEFT("bottomRightToTopLeft"),
    TOP_TO_BOTTOM("topToBottom"),
    BOTTOM_TO_TOP("bottomToTop"),
    NONE("none");

    @JsonValue
    private final String value;
}
