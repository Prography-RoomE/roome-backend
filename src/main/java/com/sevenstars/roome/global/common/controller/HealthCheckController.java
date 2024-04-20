package com.sevenstars.roome.global.common.controller;

import com.sevenstars.roome.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공통")
@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ApiResponse<Void> getHealth() {
        return ApiResponse.success();
    }
}
