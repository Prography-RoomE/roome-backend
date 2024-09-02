package com.sevenstars.roome.domain.review.repository;

import com.sevenstars.roome.domain.profile.entity.Element;
import com.sevenstars.roome.domain.profile.repository.ElementRepository;
import com.sevenstars.roome.domain.review.entity.Review;
import com.sevenstars.roome.domain.review.entity.ReviewGenre;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static com.sevenstars.roome.domain.profile.entity.ElementType.PREFERRED_GENRE;
import static com.sevenstars.roome.domain.profile.entity.ElementType.USER_STRENGTH;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReviewGenreRepositoryTest {

    @Autowired
    ElementRepository elementRepository;
    @Autowired
    ReviewGenreRepository reviewGenreRepository;
    @Autowired
    ReviewRepository reviewRepository;

    @DisplayName("후기 장르를 후기 ID로 조회시 삭제 되지 않은 장르만 출력한다.")
    @Test
    void findByReviewTest() {

        // Given
        List<Element> elements = List.of(
                new Element(PREFERRED_GENRE, "액션", "", "", "", "", 0),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 4),
                new Element(PREFERRED_GENRE, "스릴러", "", "", "", "", 5),
                new Element(USER_STRENGTH, "관찰력", "", "", "", "", 0),
                new Element(USER_STRENGTH, "분석력", "", "", "", "", 1),
                new Element(USER_STRENGTH, "추리력", "", "", "", "", 2));

        elements.get(0).markAsDeleted();
        elementRepository.saveAll(elements);

        Review review = new Review(null, 5.0, "제로월드 홍대점", "층간 소음");

        reviewRepository.save(review);

        List<ReviewGenre> reviewGenres = List.of(
                new ReviewGenre(elements.get(0), review),
                new ReviewGenre(elements.get(2), review),
                new ReviewGenre(elements.get(1), review));

        reviewGenreRepository.saveAll(reviewGenres);

        // When
        List<ReviewGenre> foundReviewGenres = reviewGenreRepository.findByReview(review);

        // Then
        assertThat(foundReviewGenres.size()).isEqualTo(2);
        assertThat(foundReviewGenres.get(0).getElement().getTitle()).isEqualTo(elements.get(1).getTitle());
        assertThat(foundReviewGenres.get(1).getElement().getTitle()).isEqualTo(elements.get(2).getTitle());
    }

    @DisplayName("후기 장르를 후기 ID로 조회시 삭제 되지 않은 장르만 출력한다.")
    @Test
    void findByReviewIdTest() {

        // Given
        List<Element> elements = List.of(
                new Element(PREFERRED_GENRE, "액션", "", "", "", "", 0),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 4),
                new Element(PREFERRED_GENRE, "스릴러", "", "", "", "", 5),
                new Element(USER_STRENGTH, "관찰력", "", "", "", "", 0),
                new Element(USER_STRENGTH, "분석력", "", "", "", "", 1),
                new Element(USER_STRENGTH, "추리력", "", "", "", "", 2));

        elements.get(0).markAsDeleted();
        elementRepository.saveAll(elements);

        List<Review> reviews = List.of(new Review(null, 5.0, "제로월드 홍대점", "층간 소음"),
                new Review(null, 4.5, "티켓 투 이스케이프", "갤럭시 익스프레스"),
                new Review(null, 4.5, "오아시스", "배드 타임 (BÆD TIME)"),
                new Review(null, 4.0, "제로월드 홍대점", "ALIVE"),
                new Review(null, 5.0, "지구별 방탈출 홍대라스트시티점", "섀도우"));

        reviewRepository.saveAll(reviews);

        List<ReviewGenre> reviewGenres = List.of(
                new ReviewGenre(elements.get(0), reviews.get(0)),
                new ReviewGenre(elements.get(1), reviews.get(0)),
                new ReviewGenre(elements.get(2), reviews.get(1)),
                new ReviewGenre(elements.get(3), reviews.get(1)),
                new ReviewGenre(elements.get(4), reviews.get(2)),
                new ReviewGenre(elements.get(5), reviews.get(2)),
                new ReviewGenre(elements.get(0), reviews.get(3)),
                new ReviewGenre(elements.get(1), reviews.get(3)),
                new ReviewGenre(elements.get(2), reviews.get(4)),
                new ReviewGenre(elements.get(3), reviews.get(4)));

        reviewGenreRepository.saveAll(reviewGenres);

        // When
        List<Long> ids = List.of(reviews.get(0).getId(), reviews.get(1).getId());
        List<ReviewGenre> foundReviewGenres = reviewGenreRepository.findByReviewIds(ids);

        // Then
        assertThat(foundReviewGenres.get(0).getElement().getTitle()).isEqualTo(elements.get(1).getTitle());
        assertThat(foundReviewGenres.get(1).getElement().getTitle()).isEqualTo(elements.get(2).getTitle());
        assertThat(foundReviewGenres.get(2).getElement().getTitle()).isEqualTo(elements.get(3).getTitle());
    }
}
