package com.sevenstars.roome.domain.profile.service;

import com.sevenstars.roome.domain.profile.entity.Profile;
import com.sevenstars.roome.domain.profile.entity.activity.Activity;
import com.sevenstars.roome.domain.profile.entity.color.Color;
import com.sevenstars.roome.domain.profile.entity.device.DeviceLockPreference;
import com.sevenstars.roome.domain.profile.entity.dislike.DislikedFactor;
import com.sevenstars.roome.domain.profile.entity.dislike.ThemeDislikedFactor;
import com.sevenstars.roome.domain.profile.entity.genre.Genre;
import com.sevenstars.roome.domain.profile.entity.genre.PreferredGenre;
import com.sevenstars.roome.domain.profile.entity.hint.HintUsagePreference;
import com.sevenstars.roome.domain.profile.entity.important.ImportantFactor;
import com.sevenstars.roome.domain.profile.entity.important.ThemeImportantFactor;
import com.sevenstars.roome.domain.profile.entity.position.HorrorThemePosition;
import com.sevenstars.roome.domain.profile.entity.strength.Strength;
import com.sevenstars.roome.domain.profile.entity.strength.UserStrength;
import com.sevenstars.roome.domain.profile.repository.ProfileRepository;
import com.sevenstars.roome.domain.profile.repository.activity.ActivityRepository;
import com.sevenstars.roome.domain.profile.repository.color.ColorRepository;
import com.sevenstars.roome.domain.profile.repository.device.DeviceLockPreferenceRepository;
import com.sevenstars.roome.domain.profile.repository.dislike.DislikedFactorRepository;
import com.sevenstars.roome.domain.profile.repository.dislike.ThemeDislikedFactorRepository;
import com.sevenstars.roome.domain.profile.repository.genre.GenreRepository;
import com.sevenstars.roome.domain.profile.repository.genre.PreferredGenreRepository;
import com.sevenstars.roome.domain.profile.repository.hint.HintUsagePreferenceRepository;
import com.sevenstars.roome.domain.profile.repository.important.ImportantFactorRepository;
import com.sevenstars.roome.domain.profile.repository.important.ThemeImportantFactorRepository;
import com.sevenstars.roome.domain.profile.repository.position.HorrorThemePositionRepository;
import com.sevenstars.roome.domain.profile.repository.strength.StrengthRepository;
import com.sevenstars.roome.domain.profile.repository.strength.UserStrengthRepository;
import com.sevenstars.roome.domain.profile.response.ProfileDefaultResponse;
import com.sevenstars.roome.domain.profile.response.ProfileResponse;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sevenstars.roome.global.common.response.Result.PROFILE_NOT_FOUND;
import static com.sevenstars.roome.global.common.response.Result.USER_NOT_FOUND;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final GenreRepository genreRepository;
    private final StrengthRepository strengthRepository;
    private final ImportantFactorRepository importantFactorRepository;
    private final HorrorThemePositionRepository horrorThemePositionRepository;
    private final HintUsagePreferenceRepository hintUsagePreferenceRepository;
    private final DeviceLockPreferenceRepository deviceLockPreferenceRepository;
    private final ActivityRepository activityRepository;
    private final DislikedFactorRepository dislikedFactorRepository;
    private final ColorRepository colorRepository;
    private final PreferredGenreRepository preferredGenreRepository;
    private final UserStrengthRepository userStrengthRepository;
    private final ThemeImportantFactorRepository themeImportantFactorRepository;
    private final ThemeDislikedFactorRepository themeDislikedFactorRepository;

    @Transactional(readOnly = true)
    public ProfileDefaultResponse getProfileDefaults() {

        List<Genre> genres = genreRepository.findByIsDeletedIsFalseOrderByPriorityAsc();
        List<Strength> strengths = strengthRepository.findByIsDeletedIsFalseOrderByPriorityAsc();
        List<ImportantFactor> importantFactors = importantFactorRepository.findByIsDeletedIsFalseOrderByPriorityAsc();
        List<HorrorThemePosition> horrorThemePositions = horrorThemePositionRepository.findByIsDeletedIsFalseOrderByPriorityAsc();
        List<HintUsagePreference> hintUsagePreferences = hintUsagePreferenceRepository.findByIsDeletedIsFalseOrderByPriorityAsc();
        List<DeviceLockPreference> deviceLockPreferences = deviceLockPreferenceRepository.findByIsDeletedIsFalseOrderByPriorityAsc();
        List<Activity> activities = activityRepository.findByIsDeletedIsFalseOrderByPriorityAsc();
        List<DislikedFactor> dislikedFactors = dislikedFactorRepository.findByIsDeletedIsFalseOrderByPriorityAsc();
        List<Color> colors = colorRepository.findByIsDeletedIsFalseOrderByPriorityAsc();

        return ProfileDefaultResponse.of(
                genres,
                strengths,
                importantFactors,
                horrorThemePositions,
                hintUsagePreferences,
                deviceLockPreferences,
                activities,
                dislikedFactors,
                colors);
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {

        User user = userRepository.findByIdAndWithdrawalIsFalse(userId)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        List<PreferredGenre> preferredGenres = preferredGenreRepository.findByProfile(profile);
        List<UserStrength> userStrengths = userStrengthRepository.findByProfile(profile);
        List<ThemeImportantFactor> themeImportantFactors = themeImportantFactorRepository.findByProfile(profile);
        List<ThemeDislikedFactor> themeDislikedFactors = themeDislikedFactorRepository.findByProfile(profile);

        return ProfileResponse.of(profile,
                preferredGenres,
                userStrengths,
                themeImportantFactors,
                themeDislikedFactors);
    }
}
