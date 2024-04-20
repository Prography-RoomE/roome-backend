package com.sevenstars.roome.global.auth.controller;

import com.sevenstars.roome.global.auth.request.SignInRequest;
import com.sevenstars.roome.global.auth.request.WithdrawalRequest;
import com.sevenstars.roome.global.auth.response.TokenResponse;
import com.sevenstars.roome.global.auth.service.LoginService;
import com.sevenstars.roome.global.common.response.ApiResponse;
import com.sevenstars.roome.global.jwt.service.JwtTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissue(@AuthenticationPrincipal Long id,
                                              @RequestHeader("Authorization") String authorizationHeader) {
        return ApiResponse.success(tokenService.reissue(id, authorizationHeader));
    }

    @PostMapping("/withdrawal")
    public ApiResponse<Void> signIn(@AuthenticationPrincipal Long id, @RequestBody @Valid WithdrawalRequest request) {
        loginService.withdrawal(id, request);
        return ApiResponse.success();
    }

    @GetMapping("/login/{provider}")
    public void login(@PathVariable String provider, @RequestParam String code) {
        log.info("{}= {}", provider, code);
    }
}
