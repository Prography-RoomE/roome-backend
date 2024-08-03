package com.sevenstars.roome.domain.review.controller;

import com.sevenstars.roome.domain.review.request.ReviewMandatoryRequest;
import com.sevenstars.roome.domain.review.request.ReviewOptionalRequest;
import com.sevenstars.roome.domain.review.response.*;
import com.sevenstars.roome.domain.review.service.ReviewService;
import com.sevenstars.roome.domain.review.service.ThemeService;
import com.sevenstars.roome.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "후기")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;
    private final ThemeService themeService;

    @GetMapping("/reviews/stores")
    public ApiResponse<StoresResponse> getStores(@RequestParam(required = false) String storeName) {
        return ApiResponse.success(themeService.getStores(storeName));
    }

    @GetMapping("/reviews/themes")
    public ApiResponse<ThemesResponse> getThemes(@RequestParam(required = false) String name) {
        return ApiResponse.success(themeService.getThemes(name));
    }

    @GetMapping("/reviews/genres")
    public ApiResponse<GenresResponse> getGenres() {
        return ApiResponse.success(reviewService.getGenres());
    }

    @GetMapping("/reviews")
    public ApiResponse<ReviewsResponse> getReviews(@AuthenticationPrincipal Long userId,
                                                   @RequestParam(required = false) String state,
                                                   @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer size,
                                                   @RequestParam(required = false) String sort) {
        return ApiResponse.success(reviewService.getReviews(userId, state, page, size, sort));
    }

    @GetMapping("/reviews/{id}")
    public ApiResponse<ReviewResponse> getReview(@PathVariable Long id) {
        return ApiResponse.success(reviewService.getReview(id));
    }

    @PostMapping("/reviews")
    public ApiResponse<ReviewIdResponse> createReview(@AuthenticationPrincipal Long userId,
                                                      @RequestBody @Valid ReviewMandatoryRequest request) {
        return ApiResponse.success(reviewService.createReview(userId, request));
    }

    @PutMapping("/reviews/{id}/mandatory")
    public ApiResponse<Void> updateMandatoryContents(@PathVariable Long id,
                                                     @RequestBody @Valid ReviewMandatoryRequest request) {
        reviewService.updateMandatoryContents(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/reviews/{id}/optional")
    public ApiResponse<ReviewImagesResponse> updateOptionalContents(@PathVariable Long id,
                                                                    @RequestPart(value = "file", required = false) List<MultipartFile> files,
                                                                    @RequestPart(value = "json") @Valid ReviewOptionalRequest request) {
        return ApiResponse.success(reviewService.updateOptionalContents(id, files, request));
    }

    @PostMapping("/reviews/{id}/publish")
    public ApiResponse<Void> publishReview(@PathVariable Long id) {
        reviewService.publishReview(id);
        return ApiResponse.success();
    }

    @DeleteMapping("/reviews/{id}")
    public ApiResponse<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ApiResponse.success();
    }
}
