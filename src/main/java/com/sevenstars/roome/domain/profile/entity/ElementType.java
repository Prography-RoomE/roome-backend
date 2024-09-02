package com.sevenstars.roome.domain.profile.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ElementType {

    PREFERRED_GENRE(2, ProfileState.PREFERRED_GENRES),
    USER_STRENGTH(2, ProfileState.USER_STRENGTHS),
    THEME_IMPORTANT_FACTOR(2, ProfileState.THEME_IMPORTANT_FACTORS),
    HORROR_THEME_POSITION(1, ProfileState.HORROR_THEME_POSITION),
    HINT_USAGE_PREFERENCE(1, ProfileState.HINT_USAGE_PREFERENCE),
    DEVICE_LOCK_PREFERENCE(1, ProfileState.DEVICE_LOCK_PREFERENCE),
    ACTIVITY(1, ProfileState.ACTIVITY),
    THEME_DISLIKED_FACTOR(2, ProfileState.THEME_DISLIKED_FACTORS);

    private final int maxSize;
    private final ProfileState profileState;
}
