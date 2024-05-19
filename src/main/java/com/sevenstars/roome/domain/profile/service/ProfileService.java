package com.sevenstars.roome.domain.profile.service;

import com.sevenstars.roome.domain.profile.entity.activity.Activity;
import com.sevenstars.roome.domain.profile.entity.color.Color;
import com.sevenstars.roome.domain.profile.entity.device.DeviceLockPreference;
import com.sevenstars.roome.domain.profile.entity.dislike.DislikedFactor;
import com.sevenstars.roome.domain.profile.entity.genre.Genre;
import com.sevenstars.roome.domain.profile.entity.hint.HintUsagePreference;
import com.sevenstars.roome.domain.profile.entity.important.ImportantFactor;
import com.sevenstars.roome.domain.profile.entity.position.HorrorThemePosition;
import com.sevenstars.roome.domain.profile.entity.strength.Strength;
import com.sevenstars.roome.domain.profile.repository.activity.ActivityRepository;
import com.sevenstars.roome.domain.profile.repository.color.ColorRepository;
import com.sevenstars.roome.domain.profile.repository.device.DeviceLockPreferenceRepository;
import com.sevenstars.roome.domain.profile.repository.dislike.DislikedFactorRepository;
import com.sevenstars.roome.domain.profile.repository.genre.GenreRepository;
import com.sevenstars.roome.domain.profile.repository.hint.HintUsagePreferenceRepository;
import com.sevenstars.roome.domain.profile.repository.important.ImportantFactorRepository;
import com.sevenstars.roome.domain.profile.repository.position.HorrorThemePositionRepository;
import com.sevenstars.roome.domain.profile.repository.strength.StrengthRepository;
import com.sevenstars.roome.domain.profile.response.ProfileDefaultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileService {

    private final GenreRepository genreRepository;
    private final StrengthRepository strengthRepository;
    private final ImportantFactorRepository importantFactorRepository;
    private final HorrorThemePositionRepository horrorThemePositionRepository;
    private final HintUsagePreferenceRepository hintUsagePreferenceRepository;
    private final DeviceLockPreferenceRepository deviceLockPreferenceRepository;
    private final ActivityRepository activityRepository;
    private final DislikedFactorRepository dislikedFactorRepository;
    private final ColorRepository colorRepository;

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
}
