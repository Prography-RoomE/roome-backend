package com.sevenstars.roome.domain.review.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ThemesResponse {
    private final List<String> names;
}
