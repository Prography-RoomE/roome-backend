package com.sevenstars.roome.global.oauth.controller;

import com.sevenstars.roome.global.common.response.ApiResponse;
import com.sevenstars.roome.global.oauth.request.SignInRequest;
import com.sevenstars.roome.global.oauth.request.WithdrawalRequest;
import com.sevenstars.roome.global.oauth.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final LoginService loginService;

    @PostMapping("/signin")
    public ApiResponse<Void> signIn(@RequestBody @Valid SignInRequest request) {
        loginService.signIn(request);
        return ApiResponse.success();
    }

    @PostMapping("/withdrawal")
    public ApiResponse<Void> signIn(@RequestBody @Valid WithdrawalRequest request) {
        loginService.withdrawal(request);
        return ApiResponse.success();
    }

    @GetMapping("/login/{provider}")
    public void login(@PathVariable String provider, @RequestParam String code) {
        log.info("{}= {}", provider, code);
    }
}
