package com.sevenstars.roome.domain.user.controller;

import com.sevenstars.roome.domain.user.response.UserResponse;
import com.sevenstars.roome.domain.user.service.UserService;
import com.sevenstars.roome.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public ApiResponse<UserResponse> getUser(@AuthenticationPrincipal Long id) {

        return ApiResponse.success(userService.getUser(id));
    }
}
