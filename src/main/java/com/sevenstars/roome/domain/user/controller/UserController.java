package com.sevenstars.roome.domain.user.controller;

import com.sevenstars.roome.domain.profile.response.ProfileResponse;
import com.sevenstars.roome.domain.user.request.NicknameRequest;
import com.sevenstars.roome.domain.user.request.TermsAgreementRequest;
import com.sevenstars.roome.domain.user.response.UserImageResponse;
import com.sevenstars.roome.domain.user.response.UserResponse;
import com.sevenstars.roome.domain.user.service.UserService;
import com.sevenstars.roome.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ApiResponse<UserResponse> getUser(@AuthenticationPrincipal Long id) {
        return ApiResponse.success(userService.getUser(id));
    }

    @PutMapping("/users/terms-agreement")
    public ApiResponse<Void> updateTermsAgreement(@AuthenticationPrincipal Long id, @RequestBody @Valid TermsAgreementRequest request) {
        userService.updateTermsAgreement(id, request);
        return ApiResponse.success();
    }

    @PostMapping("/users/nickname/validation")
    public ApiResponse<Void> validateNickname(@AuthenticationPrincipal Long id, @RequestBody @Valid NicknameRequest request) {
        userService.validateNickname(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/users/nickname")
    public ApiResponse<Void> updateNickname(@AuthenticationPrincipal Long id, @RequestBody @Valid NicknameRequest request) {
        userService.updateNickname(id, request);
        return ApiResponse.success();
    }

    @PostMapping("/users/image")
    public ApiResponse<UserImageResponse> updateImage(@AuthenticationPrincipal Long id, @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(userService.updateImage(id, file));
    }

    @DeleteMapping("/users/image")
    public ApiResponse<Void> deleteImage(@AuthenticationPrincipal Long id) {
        userService.deleteImage(id);
        return ApiResponse.success();
    }

    @GetMapping("/users/profile")
    public ApiResponse<ProfileResponse> getUserProfile(@RequestParam String nickname) {
        return ApiResponse.success(userService.getUserProfile(nickname));
    }
}
