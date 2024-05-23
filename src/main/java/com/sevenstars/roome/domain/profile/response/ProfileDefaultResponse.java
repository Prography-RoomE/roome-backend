package com.sevenstars.roome.domain.profile.response;

import com.sevenstars.roome.domain.profile.entity.color.ColorDirection;
import com.sevenstars.roome.domain.profile.entity.color.ColorMode;
import com.sevenstars.roome.domain.profile.entity.color.ColorShape;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ProfileDefaultResponse {

    private final List<Genre> genres;
    private final List<Strength> strengths;
    private final List<ImportantFactor> importantFactors;
    private final List<HorrorThemePosition> horrorThemePositions;
    private final List<HintUsagePreference> hintUsagePreferences;
    private final List<DeviceLockPreference> deviceLockPreferences;
    private final List<Activity> activities;
    private final List<DislikedFactor> dislikedFactors;
    private final List<Color> colors;

    public static ProfileDefaultResponse of(
            List<com.sevenstars.roome.domain.profile.entity.genre.Genre> genres,
            List<com.sevenstars.roome.domain.profile.entity.strength.Strength> strengths,
            List<com.sevenstars.roome.domain.profile.entity.important.ImportantFactor> importantFactors,
            List<com.sevenstars.roome.domain.profile.entity.position.HorrorThemePosition> horrorThemePositions,
            List<com.sevenstars.roome.domain.profile.entity.hint.HintUsagePreference> hintUsagePreferences,
            List<com.sevenstars.roome.domain.profile.entity.device.DeviceLockPreference> deviceLockPreferences,
            List<com.sevenstars.roome.domain.profile.entity.activity.Activity> activities,
            List<com.sevenstars.roome.domain.profile.entity.dislike.DislikedFactor> dislikedFactors,
            List<com.sevenstars.roome.domain.profile.entity.color.Color> colors) {

        return new ProfileDefaultResponse(
                genres.stream().map(ProfileDefaultResponse.Genre::from).toList(),
                strengths.stream().map(ProfileDefaultResponse.Strength::from).toList(),
                importantFactors.stream().map(ProfileDefaultResponse.ImportantFactor::from).toList(),
                horrorThemePositions.stream().map(ProfileDefaultResponse.HorrorThemePosition::from).toList(),
                hintUsagePreferences.stream().map(ProfileDefaultResponse.HintUsagePreference::from).toList(),
                deviceLockPreferences.stream().map(ProfileDefaultResponse.DeviceLockPreference::from).toList(),
                activities.stream().map(ProfileDefaultResponse.Activity::from).toList(),
                dislikedFactors.stream().map(ProfileDefaultResponse.DislikedFactor::from).toList(),
                colors.stream().map(ProfileDefaultResponse.Color::from).toList());
    }

    @Getter
    @RequiredArgsConstructor
    public static class Genre {
        private final Long id;
        private final String title;

        public static Genre from(com.sevenstars.roome.domain.profile.entity.genre.Genre genre) {
            return new Genre(genre.getId(), genre.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Strength {
        private final Long id;
        private final String title;

        public static Strength from(com.sevenstars.roome.domain.profile.entity.strength.Strength strength) {
            return new Strength(strength.getId(), strength.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class ImportantFactor {
        private final Long id;
        private final String title;

        public static ImportantFactor from(com.sevenstars.roome.domain.profile.entity.important.ImportantFactor factor) {
            return new ImportantFactor(factor.getId(), factor.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class HorrorThemePosition {
        private final Long id;
        private final String title;
        private final String description;

        public static HorrorThemePosition from(com.sevenstars.roome.domain.profile.entity.position.HorrorThemePosition position) {
            return new HorrorThemePosition(position.getId(), position.getTitle(), position.getDescription());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class HintUsagePreference {
        private final Long id;
        private final String title;
        private final String description;

        public static HintUsagePreference from(com.sevenstars.roome.domain.profile.entity.hint.HintUsagePreference preference) {
            return new HintUsagePreference(preference.getId(), preference.getTitle(), preference.getDescription());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class DeviceLockPreference {
        private final Long id;
        private final String title;

        public static DeviceLockPreference from(com.sevenstars.roome.domain.profile.entity.device.DeviceLockPreference preference) {
            return new DeviceLockPreference(preference.getId(), preference.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Activity {
        private final Long id;
        private final String title;
        private final String description;

        public static Activity from(com.sevenstars.roome.domain.profile.entity.activity.Activity activity) {
            return new Activity(activity.getId(), activity.getTitle(), activity.getDescription());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class DislikedFactor {
        private final Long id;
        private final String title;

        public static DislikedFactor from(com.sevenstars.roome.domain.profile.entity.dislike.DislikedFactor factor) {
            return new DislikedFactor(factor.getId(), factor.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Color {
        private final Long id;
        private final String title;
        private final ColorMode mode;
        private final ColorShape shape;
        private final ColorDirection direction;
        private final String startColor;
        private final String endColor;

        public static Color from(com.sevenstars.roome.domain.profile.entity.color.Color color) {
            return new Color(color.getId(),
                    color.getTitle(),
                    color.getMode(),
                    color.getShape(),
                    color.getDirection(),
                    color.getStartColor(),
                    color.getEndColor());
        }
    }
}
