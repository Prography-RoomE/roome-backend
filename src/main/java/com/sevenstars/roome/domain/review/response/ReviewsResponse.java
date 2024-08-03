package com.sevenstars.roome.domain.review.response;

import com.sevenstars.roome.domain.profile.entity.Element;
import com.sevenstars.roome.domain.review.entity.ReviewState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class ReviewsResponse {

    private final Integer totalPages;
    private final Integer pageNumber;
    private final Integer pageSize;
    private final Long totalElements;
    private final List<Review> reviews;

    public static ReviewsResponse of(Integer totalPages,
                                     Integer pageNumber,
                                     Integer pageSize,
                                     Long totalElements,
                                     List<com.sevenstars.roome.domain.review.entity.Review> reviews,
                                     Map<Long, List<Element>> genres) {


        return new ReviewsResponse(totalPages,
                pageNumber,
                pageSize,
                totalElements,
                reviews.stream().map(review -> Review.of(review, genres.get(review.getId())))
                        .collect(Collectors.toList()));
    }

    @Getter
    @RequiredArgsConstructor
    public static class Review {
        private final Long id;
        private final ReviewState state;
        private final Double score;
        private final String storeName;
        private final String themeName;
        private final List<Genre> genres;

        public static Review of(com.sevenstars.roome.domain.review.entity.Review review,
                                List<Element> genres) {
            return new Review(review.getId(),
                    review.getState(),
                    review.getScore(),
                    review.getStoreName(),
                    review.getThemeName(),
                    genres.stream().map(Genre::from)
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
}
