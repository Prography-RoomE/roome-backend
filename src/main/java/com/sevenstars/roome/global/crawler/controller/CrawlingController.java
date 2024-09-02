package com.sevenstars.roome.global.crawler.controller;

import com.sevenstars.roome.global.common.response.ApiResponse;
import com.sevenstars.roome.global.crawler.request.CrawlingRequest;
import com.sevenstars.roome.global.crawler.service.CrawlingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "크롤링")
@RequiredArgsConstructor
@RestController
public class CrawlingController {

    private final CrawlingService crawlingService;

    @PostMapping("/crawling/themes")
    public ApiResponse<Void> updateThemes(@RequestBody CrawlingRequest request) {
        crawlingService.updateThemes(request);
        return ApiResponse.success();
    }
}
