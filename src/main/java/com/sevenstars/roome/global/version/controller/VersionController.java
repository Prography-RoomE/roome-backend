package com.sevenstars.roome.global.version.controller;

import com.sevenstars.roome.global.common.response.ApiResponse;
import com.sevenstars.roome.global.version.request.VersionRequest;
import com.sevenstars.roome.global.version.request.VersionsRequest;
import com.sevenstars.roome.global.version.response.VersionResponse;
import com.sevenstars.roome.global.version.response.VersionsResponse;
import com.sevenstars.roome.global.version.service.VersionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공통")
@Slf4j
@RequiredArgsConstructor
@RestController
public class VersionController {

    private final VersionService versionService;

    @GetMapping("/versions")
    public ApiResponse<VersionsResponse> getVersions() {
        return ApiResponse.success(versionService.getVersions());
    }

    @GetMapping("/versions/server")
    public ApiResponse<VersionResponse> getServerVersion() {
        return ApiResponse.success(versionService.getServerVersion());
    }

    @GetMapping("/versions/aos")
    public ApiResponse<VersionResponse> getAosVersion() {
        return ApiResponse.success(versionService.getAosVersion());
    }

    @GetMapping("/versions/ios")
    public ApiResponse<VersionResponse> getIosVersion() {
        return ApiResponse.success(versionService.getIosVersion());
    }

    @PutMapping("/versions")
    public ApiResponse<Void> updateVersions(@RequestBody @Valid VersionsRequest request) {
        versionService.updateVersions(request);
        return ApiResponse.success();
    }

    @PutMapping("/versions/server")
    public ApiResponse<Void> updateServerVersion(@RequestBody @Valid VersionRequest request) {
        versionService.updateServerVersion(request);
        return ApiResponse.success();
    }

    @PutMapping("/versions/aos")
    public ApiResponse<Void> updateAosVersion(@RequestBody @Valid VersionRequest request) {
        versionService.updateAosVersion(request);
        return ApiResponse.success();
    }

    @PutMapping("/versions/ios")
    public ApiResponse<Void> updateIosVersion(@RequestBody @Valid VersionRequest request) {
        versionService.updateIosVersion(request);
        return ApiResponse.success();
    }
}
