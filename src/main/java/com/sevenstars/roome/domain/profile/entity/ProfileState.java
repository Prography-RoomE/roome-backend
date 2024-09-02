package com.sevenstars.roome.domain.profile.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public enum ProfileState {

    ROOM_COUNT(0, "roomCount", Profile::updateRoomCountState),
    PREFERRED_GENRES(1, "preferredGenres", Profile::updatePreferredGenresState),
    MBTI(2, "mbti", Profile::updateMbtiState),
    USER_STRENGTHS(3, "userStrengths", Profile::updateUserStrengthsState),
    THEME_IMPORTANT_FACTORS(4, "themeImportantFactors", Profile::updateThemeImportantFactorsState),
    HORROR_THEME_POSITION(5, "horrorThemePosition", Profile::updateHorrorThemePositionState),
    HINT_USAGE_PREFERENCE(6, "hintUsagePreference", Profile::updateHintUsagePreferenceState),
    DEVICE_LOCK_PREFERENCE(7, "deviceLockPreference", Profile::updateDeviceLockPreferenceState),
    ACTIVITY(8, "activity", Profile::updateActivityState),
    THEME_DISLIKED_FACTORS(9, "themeDislikedFactors", Profile::updateThemeDislikedFactorsState),
    COLOR(10, "color", Profile::updateColorState),
    COMPLETE(11, "complete", Profile::updateStateToComplete);

    private final int order;

    @JsonValue
    private final String value;

    private final Consumer<Profile> profileStateUpdateConsumer;
}
