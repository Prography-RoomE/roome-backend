package com.sevenstars.roome.global.auth.controller;

import com.sevenstars.roome.global.auth.request.DeactivateRequest;
import com.sevenstars.roome.global.auth.request.SignInRequest;
import com.sevenstars.roome.global.auth.request.TokenRequest;
import com.sevenstars.roome.global.auth.response.TokenResponse;
import com.sevenstars.roome.global.auth.service.LoginService;
import com.sevenstars.roome.global.common.response.ApiResponse;
import com.sevenstars.roome.global.jwt.service.JwtTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증")
@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final LoginService loginService;
    private final JwtTokenService tokenService;

    @PostMapping("/auth/signin")
    public ApiResponse<TokenResponse> signIn(@RequestBody @Valid SignInRequest request) {
        return ApiResponse.success(loginService.signIn(request));
    }

    @PostMapping("/auth/token")
    public ApiResponse<TokenResponse> getToken(@RequestBody @Valid TokenRequest request) {
        return ApiResponse.success(tokenService.getToken(request));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/auth/signout")
    public ApiResponse<Void> signOut(@AuthenticationPrincipal Long userId) {
        tokenService.delete(userId);
        return ApiResponse.success();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/auth/deactivate")
    public ApiResponse<Void> deactivate(@AuthenticationPrincipal Long userId, @RequestBody @Valid DeactivateRequest request) {
        loginService.deactivate(userId, request);
        return ApiResponse.success();
    }

    @Operation(hidden = true)
    @GetMapping("/login/{provider}")
    public void login(@PathVariable String provider, @RequestParam String code) {
        log.info("{}={}", provider, code);
    }
}
