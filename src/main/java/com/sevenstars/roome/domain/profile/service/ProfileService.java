package com.sevenstars.roome.domain.profile.service;

import com.sevenstars.roome.domain.profile.entity.Mbti;
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
import com.sevenstars.roome.domain.profile.request.*;
import com.sevenstars.roome.domain.profile.response.ProfileDefaultResponse;
import com.sevenstars.roome.domain.profile.response.ProfileResponse;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.sevenstars.roome.global.common.response.Result.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileService {

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

        Profile profile = profileRepository.findByUserId(userId)
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

    @Transactional
    public void clearProfile(Long userId) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        profile.clear();

        preferredGenreRepository.deleteByProfile(profile);
        userStrengthRepository.deleteByProfile(profile);
        themeImportantFactorRepository.deleteByProfile(profile);
        themeDislikedFactorRepository.deleteByProfile(profile);
    }

    @Transactional
    public void updateRoomCount(Long userId, RoomCountRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Integer count = request.getCount();
        Boolean isPlusEnabled = request.getIsPlusEnabled();

        profile.updateRoomCount(count, isPlusEnabled);
    }

    @Transactional
    public void updatePreferredGenres(Long userId, PreferredGenresRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        List<Long> ids = request.getIds();

        List<Genre> genres = genreRepository.findAllById(ids);

        if (ids.size() != genres.size()) {
            throw new CustomClientErrorException(PROFILE_PREFERRED_GENRES_NOT_FOUND);
        }

        preferredGenreRepository.deleteByProfile(profile);

        genres.forEach(genre -> preferredGenreRepository.save(new PreferredGenre(profile, genre)));

        profile.updatePreferredGenres();
    }

    @Transactional
    public void updateMbti(Long userId, MbtiRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Mbti mbti = Arrays.stream(Mbti.values())
                .filter(value -> value.name().equals(request.getMbti()))
                .findAny()
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_MBTI_NOT_FOUND));

        profile.updateMbti(mbti);
    }

    @Transactional
    public void updateUserStrengths(Long userId, UserStrengthsRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        List<Long> ids = request.getIds();

        List<Strength> strengths = strengthRepository.findAllById(ids);

        if (ids.size() != strengths.size()) {
            throw new CustomClientErrorException(PROFILE_USER_STRENGTHS_NOT_FOUND);
        }

        userStrengthRepository.deleteByProfile(profile);

        strengths.forEach(strength -> userStrengthRepository.save(new UserStrength(profile, strength)));

        profile.updateUserStrengths();
    }

    @Transactional
    public void updateThemeImportantFactors(Long userId, ThemeImportantFactorsRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        List<Long> ids = request.getIds();

        List<ImportantFactor> importantFactors = importantFactorRepository.findAllById(ids);

        if (ids.size() != importantFactors.size()) {
            throw new CustomClientErrorException(PROFILE_THEME_IMPORTANT_FACTORS_NOT_FOUND);
        }

        themeImportantFactorRepository.deleteByProfile(profile);

        importantFactors.forEach(importantFactor -> themeImportantFactorRepository.save(new ThemeImportantFactor(profile, importantFactor)));

        profile.updateThemeImportantFactors();
    }

    @Transactional
    public void updateHorrorThemePosition(Long userId, HorrorThemePositionRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Long id = request.getId();

        HorrorThemePosition horrorThemePosition = horrorThemePositionRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_HORROR_THEME_POSITION_NOT_FOUND));

        profile.updateHorrorThemePosition(horrorThemePosition);
    }

    @Transactional
    public void updateHintUsagePreference(Long userId, HintUsagePreferenceRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Long id = request.getId();

        HintUsagePreference hintUsagePreference = hintUsagePreferenceRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_HINT_USAGE_PREFERENCE_NOT_FOUND));

        profile.updateHintUsagePreference(hintUsagePreference);
    }

    @Transactional
    public void updateDeviceLockPreference(Long userId, DeviceLockPreferenceRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Long id = request.getId();

        DeviceLockPreference deviceLockPreference = deviceLockPreferenceRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_DEVICE_LOCK_PREFERENCE_NOT_FOUND));

        profile.updateDeviceLockPreference(deviceLockPreference);
    }

    @Transactional
    public void updateActivity(Long userId, ActivityRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Long id = request.getId();

        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_ACTIVITY_NOT_FOUND));

        profile.updateActivity(activity);
    }

    @Transactional
    public void updateThemeDislikedFactors(Long userId, ThemeDislikedFactorsRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        List<Long> ids = request.getIds();

        List<DislikedFactor> dislikedFactors = dislikedFactorRepository.findAllById(ids);

        if (ids.size() != dislikedFactors.size()) {
            throw new CustomClientErrorException(PROFILE_THEME_DISLIKED_FACTORS_NOT_FOUND);
        }

        themeDislikedFactorRepository.deleteByProfile(profile);

        dislikedFactors.forEach(dislikedFactor -> themeDislikedFactorRepository.save(new ThemeDislikedFactor(profile, dislikedFactor)));

        profile.updateThemeDislikedFactors();
    }

    @Transactional
    public void updateColor(Long userId, ColorRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_NOT_FOUND));

        Long id = request.getId();

        Color color = colorRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(PROFILE_COLOR_NOT_FOUND));

        profile.updateColor(color);
    }
}
