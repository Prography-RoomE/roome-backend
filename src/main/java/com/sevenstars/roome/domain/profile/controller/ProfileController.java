package com.sevenstars.roome.domain.profile.controller;

import com.sevenstars.roome.domain.profile.entity.ElementType;
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

import static com.sevenstars.roome.domain.profile.entity.ElementType.*;

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

    @PutMapping("/profiles/room-count-range")
    public ApiResponse<Void> updateRoomCountRange(@AuthenticationPrincipal Long id, @RequestBody @Valid RoomCountRangeRequest request) {
        profileService.updateRoomCountRange(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/preferred-genres")
    public ApiResponse<Void> updatePreferredGenres(@AuthenticationPrincipal Long id, @RequestBody @Valid ProfileElementsRequest request) {
        profileService.updateProfileElements(id, request, ElementType.PREFERRED_GENRE);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/mbti")
    public ApiResponse<Void> updateMbti(@AuthenticationPrincipal Long id, @RequestBody @Valid MbtiRequest request) {
        profileService.updateMbti(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/user-strengths")
    public ApiResponse<Void> updateUserStrengths(@AuthenticationPrincipal Long id, @RequestBody @Valid ProfileElementsRequest request) {
        profileService.updateProfileElements(id, request, USER_STRENGTH);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/theme-important-factors")
    public ApiResponse<Void> updateThemeImportantFactors(@AuthenticationPrincipal Long id, @RequestBody @Valid ProfileElementsRequest request) {
        profileService.updateProfileElements(id, request, THEME_IMPORTANT_FACTOR);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/horror-theme-position")
    public ApiResponse<Void> updateHorrorThemePosition(@AuthenticationPrincipal Long id, @RequestBody @Valid ProfileElementRequest request) {
        profileService.updateProfileElement(id, request, HORROR_THEME_POSITION);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/hint-usage-preference")
    public ApiResponse<Void> updateHintUsagePreference(@AuthenticationPrincipal Long id, @RequestBody @Valid ProfileElementRequest request) {
        profileService.updateProfileElement(id, request, HINT_USAGE_PREFERENCE);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/device-lock-preference")
    public ApiResponse<Void> updateDeviceLockPreference(@AuthenticationPrincipal Long id, @RequestBody @Valid ProfileElementRequest request) {
        profileService.updateProfileElement(id, request, DEVICE_LOCK_PREFERENCE);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/activity")
    public ApiResponse<Void> updateActivity(@AuthenticationPrincipal Long id, @RequestBody @Valid ProfileElementRequest request) {
        profileService.updateProfileElement(id, request, ACTIVITY);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/theme-disliked-factors")
    public ApiResponse<Void> updateThemeDislikedFactors(@AuthenticationPrincipal Long id, @RequestBody @Valid ProfileElementsRequest request) {
        profileService.updateProfileElements(id, request, THEME_DISLIKED_FACTOR);
        return ApiResponse.success();
    }

    @PutMapping("/profiles/color")
    public ApiResponse<Void> updateColor(@AuthenticationPrincipal Long id, @RequestBody @Valid ColorRequest request) {
        profileService.updateColor(id, request);
        return ApiResponse.success();
    }
}
