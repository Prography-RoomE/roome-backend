package com.sevenstars.roome.domain.review.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ReviewImagesResponse {
    private final List<String> imageUrls;
}
