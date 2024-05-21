package com.sevenstars.roome.domain.profile.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileState {

    ROOM_COUNT(0, "roomCount"),
    PREFERRED_GENRES(1, "preferredGenres"),
    MBTI(2, "mbti"),
    USER_STRENGTHS(3, "userStrengths"),
    THEME_IMPORTANT_FACTORS(4, "themeImportantFactors"),
    HORROR_THEME_POSITION(5, "horrorThemePosition"),
    HINT_USAGE_PREFERENCE(6, "hintUsagePreference"),
    DEVICE_LOCK_PREFERENCE(7, "deviceLockPreference"),
    ACTIVITY(8, "activity"),
    THEME_DISLIKED_FACTORS(9, "themeDislikedFactors"),
    COLOR(10, "color"),
    COMPLETE(11, "complete");

    private final int order;

    @JsonValue
    private final String value;
}
