package com.sevenstars.roome.domain.profile.response;

import com.sevenstars.roome.domain.profile.entity.Element;
import com.sevenstars.roome.domain.profile.entity.Mbti;
import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.ProfileState;
import com.sevenstars.roome.domain.profile.entity.color.ColorDirection;
import com.sevenstars.roome.domain.profile.entity.color.ColorMode;
import com.sevenstars.roome.domain.profile.entity.color.ColorShape;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ProfileResponse {

    private final String nickname;
    private final ProfileState state;
    private final String count;
    private final List<Genre> preferredGenres;
    private final Mbti mbti;
    private final List<Strength> userStrengths;
    private final List<ImportantFactor> themeImportantFactors;
    private final HorrorThemePosition horrorThemePosition;
    private final HintUsagePreference hintUsagePreference;
    private final DeviceLockPreference deviceLockPreference;
    private final Activity activity;
    private final List<DislikedFactor> themeDislikedFactors;
    private final Color color;

    public static ProfileResponse of(Profile profile,
                                     List<Element> preferredGenres,
                                     List<Element> userStrengths,
                                     List<Element> themeImportantFactors,
                                     List<Element> horrorThemePositions,
                                     List<Element> hintUsagePreferences,
                                     List<Element> deviceLockPreferences,
                                     List<Element> activities,
                                     List<Element> themeDislikedFactors) {
        return new ProfileResponse(profile.getUser().getNickname(),
                profile.getState(),
                profile.getCount(),
                preferredGenres.stream().map(Genre::from).collect(Collectors.toList()),
                profile.getMbti(),
                userStrengths.stream().map(Strength::from).collect(Collectors.toList()),
                themeImportantFactors.stream().map(ImportantFactor::from).collect(Collectors.toList()),
                horrorThemePositions.isEmpty() ? null : HorrorThemePosition.from(horrorThemePositions.get(0)),
                hintUsagePreferences.isEmpty() ? null : HintUsagePreference.from(hintUsagePreferences.get(0)),
                deviceLockPreferences.isEmpty() ? null : DeviceLockPreference.from(deviceLockPreferences.get(0)),
                activities.isEmpty() ? null : Activity.from(activities.get(0)),
                themeDislikedFactors.stream().map(DislikedFactor::from).collect(Collectors.toList()),
                profile.getColor() == null ? null : Color.from(profile.getColor()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Genre {
        private final Long id;
        private final String title;
        private final String text;

        public static Genre from(Element genre) {
            return new Genre(genre.getId(),
                    StringUtils.hasText(genre.getEmoji()) ? genre.getEmoji() + " " + genre.getTitle() : genre.getTitle(),
                    genre.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Strength {
        private final Long id;
        private final String title;
        private final String text;

        public static Strength from(Element strength) {
            return new Strength(strength.getId(),
                    StringUtils.hasText(strength.getEmoji()) ? strength.getEmoji() + " " + strength.getTitle() : strength.getTitle(),
                    strength.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class ImportantFactor {
        private final Long id;
        private final String title;
        private final String text;

        public static ImportantFactor from(Element importantFactor) {
            return new ImportantFactor(importantFactor.getId(),
                    StringUtils.hasText(importantFactor.getEmoji()) ? importantFactor.getEmoji() + " " + importantFactor.getSubTitle() : importantFactor.getSubTitle(),
                    importantFactor.getSubTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class HorrorThemePosition {
        private final Long id;
        private final String title;
        private final String text;

        public static HorrorThemePosition from(Element horrorThemePosition) {
            return new HorrorThemePosition(horrorThemePosition.getId(),
                    StringUtils.hasText(horrorThemePosition.getEmoji()) ? horrorThemePosition.getEmoji() + " " + horrorThemePosition.getTitle() : horrorThemePosition.getTitle(),
                    horrorThemePosition.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class HintUsagePreference {
        private final Long id;
        private final String title;
        private final String text;

        public static HintUsagePreference from(Element hintUsagePreference) {
            return new HintUsagePreference(hintUsagePreference.getId(),
                    StringUtils.hasText(hintUsagePreference.getEmoji()) ? hintUsagePreference.getEmoji() + " " + hintUsagePreference.getTitle() : hintUsagePreference.getTitle(),
                    hintUsagePreference.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class DeviceLockPreference {
        private final Long id;
        private final String title;
        private final String text;

        public static DeviceLockPreference from(Element deviceLockPreference) {
            return new DeviceLockPreference(deviceLockPreference.getId(),
                    StringUtils.hasText(deviceLockPreference.getEmoji()) ? deviceLockPreference.getEmoji() + " " + deviceLockPreference.getTitle() : deviceLockPreference.getTitle(),
                    deviceLockPreference.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Activity {
        private final Long id;
        private final String title;
        private final String text;

        public static Activity from(Element activity) {
            return new Activity(activity.getId(),
                    StringUtils.hasText(activity.getEmoji()) ? activity.getEmoji() + " " + activity.getTitle() : activity.getTitle(),
                    activity.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class DislikedFactor {
        private final Long id;
        private final String title;
        private final String text;

        public static DislikedFactor from(Element dislikedFactor) {
            return new DislikedFactor(dislikedFactor.getId(),
                    StringUtils.hasText(dislikedFactor.getEmoji()) ? dislikedFactor.getEmoji() + " " + dislikedFactor.getTitle() : dislikedFactor.getTitle(),
                    dislikedFactor.getTitle());
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
