package com.sevenstars.roome.domain.profile.response;

import com.sevenstars.roome.domain.profile.entity.Mbti;
import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.ProfileState;
import com.sevenstars.roome.domain.profile.entity.dislike.ThemeDislikedFactor;
import com.sevenstars.roome.domain.profile.entity.genre.PreferredGenre;
import com.sevenstars.roome.domain.profile.entity.important.ThemeImportantFactor;
import com.sevenstars.roome.domain.profile.entity.strength.UserStrength;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ProfileResponse {

    private final Long id;
    private final ProfileState state;
    private final Integer count;
    private final Boolean isPlusEnabled;
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
                profile.getIsPlusEnabled(),
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

        public static ImportantFactor from(com.sevenstars.roome.domain.profile.entity.important.ImportantFactor importantFactor) {
            return new ImportantFactor(importantFactor.getId(), importantFactor.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class HorrorThemePosition {
        private final Long id;
        private final String title;

        public static HorrorThemePosition from(com.sevenstars.roome.domain.profile.entity.position.HorrorThemePosition horrorThemePosition) {
            return new HorrorThemePosition(horrorThemePosition.getId(), horrorThemePosition.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class HintUsagePreference {
        private final Long id;
        private final String title;

        public static HintUsagePreference from(com.sevenstars.roome.domain.profile.entity.hint.HintUsagePreference hintUsagePreference) {
            return new HintUsagePreference(hintUsagePreference.getId(), hintUsagePreference.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class DeviceLockPreference {
        private final Long id;
        private final String title;

        public static DeviceLockPreference from(com.sevenstars.roome.domain.profile.entity.device.DeviceLockPreference deviceLockPreference) {
            return new DeviceLockPreference(deviceLockPreference.getId(), deviceLockPreference.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Activity {
        private final Long id;
        private final String title;

        public static Activity from(com.sevenstars.roome.domain.profile.entity.activity.Activity activity) {
            return new Activity(activity.getId(), activity.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class DislikedFactor {
        private final Long id;
        private final String title;

        public static DislikedFactor from(com.sevenstars.roome.domain.profile.entity.dislike.DislikedFactor dislikedFactor) {
            return new DislikedFactor(dislikedFactor.getId(), dislikedFactor.getTitle());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Color {
        private final Long id;
        private final String title;

        public static Color from(com.sevenstars.roome.domain.profile.entity.color.Color color) {
            return new Color(color.getId(), color.getTitle());
        }
    }
}
