package com.sevenstars.roome.domain.review.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ReviewMandatoryRequest {
    @NotNull
    public Double score;
    @NotNull
    private String storeName;
    @NotNull
    private String themeName;
    private List<Long> genreIds;
}
