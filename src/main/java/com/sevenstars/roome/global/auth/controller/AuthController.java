package com.sevenstars.roome.global.auth.controller;

import com.sevenstars.roome.global.auth.request.SignInRequest;
import com.sevenstars.roome.global.auth.request.TokenRequest;
import com.sevenstars.roome.global.auth.request.WithdrawalRequest;
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

    @PostMapping("/signin")
    public ApiResponse<TokenResponse> signIn(@RequestBody @Valid SignInRequest request) {
        return ApiResponse.success(loginService.signIn(request));
    }

    @PostMapping("/token")
    public ApiResponse<TokenResponse> getToken(@RequestBody @Valid TokenRequest request) {
        return ApiResponse.success(tokenService.getToken(request));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/signout")
    public ApiResponse<Void> signOut(@AuthenticationPrincipal Long userId) {
        tokenService.delete(userId);
        return ApiResponse.success();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/withdrawal")
    public ApiResponse<Void> withdraw(@AuthenticationPrincipal Long userId, @RequestBody @Valid WithdrawalRequest request) {
        loginService.withdraw(userId, request);
        return ApiResponse.success();
    }

    @Operation(hidden = true)
    @GetMapping("/login/{provider}")
    public void login(@PathVariable String provider, @RequestParam String code) {
        log.info("{}= {}", provider, code);
    }
}
