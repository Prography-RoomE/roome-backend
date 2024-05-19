package com.sevenstars.roome.domain.profile.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileState {

    ROOM_COUNT(0, "roomCount"),
    PREFERRED_GENRE(1, "preferredGenre"),
    MBTI(2, "mbti"),
    USER_STRENGTH(3, "userStrength"),
    IMPORTANT_FACTOR(4, "importantFactor"),
    HORROR_THEME_POSITION(5, "horrorThemePosition"),
    HINT_USAGE_PREFERENCE(6, "hintUsagePreference"),
    DEVICE_LOCK_PREFERENCE(7, "deviceLockPreference"),
    ACTIVITY(8, "activity"),
    DISLIKED_FACTOR(9, "dislikedFactor"),
    COLOR(10, "color"),
    COMPLETE(11, "complete");

    private final int order;

    @JsonValue
    private final String value;
}
