package com.sevenstars.roome.domain.profile.controller;

import com.sevenstars.roome.domain.profile.response.ProfileDefaultResponse;
import com.sevenstars.roome.domain.profile.service.ProfileService;
import com.sevenstars.roome.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
