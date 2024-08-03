package com.sevenstars.roome.domain.review.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ReviewOptionalRequest {
    private Boolean success;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate playDate;
    private Integer totalTime;
    private Integer remainingTime;
    private Integer hintCount;
    private Integer participants;
    private String difficultyLevel;
    private String fearLevel;
    private String activityLevel;
    private Double interiorRating;
    private Double directionRating;
    private Double storyRating;
    private String content;
    private List<String> imageUrls;
    private Boolean spoiler;
    private Boolean isPublic;
}
