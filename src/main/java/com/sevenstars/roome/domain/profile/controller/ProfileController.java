package com.sevenstars.roome.domain.profile.controller;

import com.sevenstars.roome.domain.profile.request.*;
import com.sevenstars.roome.domain.profile.response.ProfileDefaultResponse;
import com.sevenstars.roome.domain.profile.response.ProfileResponse;
import com.sevenstars.roome.domain.profile.service.ProfileService;
import com.sevenstars.roome.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "프로필")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/profiles/defaults")
    public ApiResponse<ProfileDefaultResponse> getProfileDefaults(@AuthenticationPrincipal Long id) {
        return ApiResponse.success(profileService.getProfileDefaults());
    }

    @GetMapping("/profiles")
    public ApiResponse<ProfileResponse> getProfile(@AuthenticationPrincipal Long id) {
        return ApiResponse.success(profileService.getProfile(id));
    }

    @DeleteMapping("/profiles")
    public ApiResponse<Void> clearProfile(@AuthenticationPrincipal Long id) {
        profileService.clearProfile(id);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/room-count")
    public ApiResponse<Void> updateRoomCount(@AuthenticationPrincipal Long id, @RequestBody @Valid RoomCountRequest request) {
        profileService.updateRoomCount(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/preferred-genres")
    public ApiResponse<Void> updatePreferredGenres(@AuthenticationPrincipal Long id, @RequestBody @Valid PreferredGenresRequest request) {
        profileService.updatePreferredGenres(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/mbti")
    public ApiResponse<Void> updateMbti(@AuthenticationPrincipal Long id, @RequestBody @Valid MbtiRequest request) {
        profileService.updateMbti(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/user-strengths")
    public ApiResponse<Void> updateUserStrengths(@AuthenticationPrincipal Long id, @RequestBody @Valid UserStrengthsRequest request) {
        profileService.updateUserStrengths(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/theme-important-factors")
    public ApiResponse<Void> updateThemeImportantFactors(@AuthenticationPrincipal Long id, @RequestBody @Valid ThemeImportantFactorsRequest request) {
        profileService.updateThemeImportantFactors(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/horror-theme-position")
    public ApiResponse<Void> updateHorrorThemePosition(@AuthenticationPrincipal Long id, @RequestBody @Valid HorrorThemePositionRequest request) {
        profileService.updateHorrorThemePosition(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/hint-usage-preference")
    public ApiResponse<Void> updateHintUsagePreference(@AuthenticationPrincipal Long id, @RequestBody @Valid HintUsagePreferenceRequest request) {
        profileService.updateHintUsagePreference(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/device-lock-preference")
    public ApiResponse<Void> updateDeviceLockPreference(@AuthenticationPrincipal Long id, @RequestBody @Valid DeviceLockPreferenceRequest request) {
        profileService.updateDeviceLockPreference(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/activity")
    public ApiResponse<Void> updateActivity(@AuthenticationPrincipal Long id, @RequestBody @Valid ActivityRequest request) {
        profileService.updateActivity(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/theme-disliked-factors")
    public ApiResponse<Void> updateThemeDislikedFactors(@AuthenticationPrincipal Long id, @RequestBody @Valid ThemeDislikedFactorsRequest request) {
        profileService.updateThemeDislikedFactors(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/color")
    public ApiResponse<Void> updateColor(@AuthenticationPrincipal Long id, @RequestBody @Valid ColorRequest request) {
        profileService.updateColor(id, request);
        return ApiResponse.success();
    }
}
