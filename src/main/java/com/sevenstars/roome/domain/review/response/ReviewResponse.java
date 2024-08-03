package com.sevenstars.roome.domain.review.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sevenstars.roome.domain.profile.entity.Element;
import com.sevenstars.roome.domain.review.entity.Review;
import com.sevenstars.roome.domain.review.entity.ReviewGenre;
import com.sevenstars.roome.domain.review.entity.ReviewImage;
import com.sevenstars.roome.domain.review.entity.ReviewState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ReviewResponse {

    private final Long id;
    private final ReviewState state;
    private final Double score;
    private final String storeName;
    private final String themeName;
    private final Boolean spoiler;
    private final Boolean isPublic;
    private final Boolean success;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate playDate;
    private final Integer totalTime;
    private final Integer remainingTime;
    private final Integer hintCount;
    private final Integer participants;
    private final String difficultyLevel;
    private final String fearLevel;
    private final String activityLevel;
    private final Double interiorRating;
    private final Double directionRating;
    private final Double storyRating;
    private final String content;
    private final List<Genre> genres;
    private final List<String> imageUrls;

    public static ReviewResponse of(Review review, List<ReviewGenre> reviewGenres, List<ReviewImage> reviewImages) {
        return new ReviewResponse(review.getId(),
                review.getState(),
                review.getScore(),
                review.getStoreName(),
                review.getThemeName(),
                review.getSpoiler(),
                review.getIsPublic(),
                review.getSuccess(),
                review.getPlayDate(),
                review.getTotalTime(),
                review.getRemainingTime(),
                review.getHintCount(),
                review.getParticipants(),
                review.getDifficultyLevel(),
                review.getFearLevel(),
                review.getActivityLevel(),
                review.getInteriorRating(),
                review.getDirectionRating(),
                review.getStoryRating(),
                review.getContent(),
                reviewGenres.stream()
                        .map(ReviewGenre::getElement)
                        .map(Genre::from)
                        .collect(Collectors.toList()),
                reviewImages.stream()
                        .map(ReviewImage::getUrl)
                        .collect(Collectors.toList()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Genre {
        private final Long id;
        private final String title;

        public static Genre from(Element element) {
            return new Genre(element.getId(), element.getTitle());
        }
    }
}
