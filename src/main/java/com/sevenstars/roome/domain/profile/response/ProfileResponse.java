package com.sevenstars.roome.domain.profile.response;

import com.sevenstars.roome.domain.profile.entity.Mbti;
import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.ProfileState;
import com.sevenstars.roome.domain.profile.entity.color.ColorDirection;
import com.sevenstars.roome.domain.profile.entity.color.ColorMode;
import com.sevenstars.roome.domain.profile.entity.color.ColorShape;
import com.sevenstars.roome.domain.profile.entity.dislike.ThemeDislikedFactor;
import com.sevenstars.roome.domain.profile.entity.genre.PreferredGenre;
import com.sevenstars.roome.domain.profile.entity.important.ThemeImportantFactor;
import com.sevenstars.roome.domain.profile.entity.strength.UserStrength;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ProfileResponse {

    private final Long id;
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
                                     List<PreferredGenre> preferredGenres,
                                     List<UserStrength> userStrengths,
                                     List<ThemeImportantFactor> themeImportantFactors,
                                     List<ThemeDislikedFactor> themeDislikedFactors) {
        return new ProfileResponse(profile.getId(),
                profile.getState(),
                profile.getCount(),
                preferredGenres.stream().map(preferredGenre -> Genre.from(preferredGenre.getGenre())).collect(Collectors.toList()),
                profile.getMbti(),
                userStrengths.stream().map(userStrength -> Strength.from(userStrength.getStrength())).collect(Collectors.toList()),
                themeImportantFactors.stream().map(themeImportantFactor -> ImportantFactor.from(themeImportantFactor.getImportantFactor())).collect(Collectors.toList()),
                profile.getHorrorThemePosition() == null ? null : HorrorThemePosition.from(profile.getHorrorThemePosition()),
                profile.getHintUsagePreference() == null ? null : HintUsagePreference.from(profile.getHintUsagePreference()),
                profile.getDeviceLockPreference() == null ? null : DeviceLockPreference.from(profile.getDeviceLockPreference()),
                profile.getActivity() == null ? null : Activity.from(profile.getActivity()),
                themeDislikedFactors.stream().map(themeDislikedFactor -> DislikedFactor.from(themeDislikedFactor.getDislikedFactor())).collect(Collectors.toList()),
                profile.getColor() == null ? null : Color.from(profile.getColor()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Genre {
        private final Long id;
        private final String title;

        public static Genre from(com.sevenstars.roome.domain.profile.entity.genre.Genre genre) {
            return new Genre(genre.getId(),
                    StringUtils.hasText(genre.getEmoji()) ? genre.getEmoji() + " " + genre.getTitle() : genre.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Strength {
        private final Long id;
        private final String title;

        public static Strength from(com.sevenstars.roome.domain.profile.entity.strength.Strength strength) {
            return new Strength(strength.getId(),
                    StringUtils.hasText(strength.getEmoji()) ? strength.getEmoji() + " " + strength.getTitle() : strength.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class ImportantFactor {
        private final Long id;
        private final String title;

        public static ImportantFactor from(com.sevenstars.roome.domain.profile.entity.important.ImportantFactor importantFactor) {
            return new ImportantFactor(importantFactor.getId(),
                    StringUtils.hasText(importantFactor.getEmoji()) ? importantFactor.getEmoji() + " " + importantFactor.getSubTitle() : importantFactor.getSubTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class HorrorThemePosition {
        private final Long id;
        private final String title;

        public static HorrorThemePosition from(com.sevenstars.roome.domain.profile.entity.position.HorrorThemePosition horrorThemePosition) {
            return new HorrorThemePosition(horrorThemePosition.getId(),
                    StringUtils.hasText(horrorThemePosition.getEmoji()) ? horrorThemePosition.getEmoji() + " " + horrorThemePosition.getTitle() : horrorThemePosition.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class HintUsagePreference {
        private final Long id;
        private final String title;

        public static HintUsagePreference from(com.sevenstars.roome.domain.profile.entity.hint.HintUsagePreference hintUsagePreference) {
            return new HintUsagePreference(hintUsagePreference.getId(),
                    StringUtils.hasText(hintUsagePreference.getEmoji()) ? hintUsagePreference.getEmoji() + " " + hintUsagePreference.getTitle() : hintUsagePreference.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class DeviceLockPreference {
        private final Long id;
        private final String title;

        public static DeviceLockPreference from(com.sevenstars.roome.domain.profile.entity.device.DeviceLockPreference deviceLockPreference) {
            return new DeviceLockPreference(deviceLockPreference.getId(),
                    StringUtils.hasText(deviceLockPreference.getEmoji()) ? deviceLockPreference.getEmoji() + " " + deviceLockPreference.getTitle() : deviceLockPreference.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Activity {
        private final Long id;
        private final String title;

        public static Activity from(com.sevenstars.roome.domain.profile.entity.activity.Activity activity) {
            return new Activity(activity.getId(),
                    StringUtils.hasText(activity.getEmoji()) ? activity.getEmoji() + " " + activity.getTitle() : activity.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class DislikedFactor {
        private final Long id;
        private final String title;

        public static DislikedFactor from(com.sevenstars.roome.domain.profile.entity.dislike.DislikedFactor dislikedFactor) {
            return new DislikedFactor(dislikedFactor.getId(),
                    StringUtils.hasText(dislikedFactor.getEmoji()) ? dislikedFactor.getEmoji() + " " + dislikedFactor.getTitle() : dislikedFactor.getTitle());
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
