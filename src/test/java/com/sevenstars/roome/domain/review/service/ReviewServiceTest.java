package com.sevenstars.roome.domain.review.service;

import com.sevenstars.roome.domain.common.service.StorageService;
import com.sevenstars.roome.domain.profile.entity.Element;
import com.sevenstars.roome.domain.profile.repository.ElementRepository;
import com.sevenstars.roome.domain.review.entity.Review;
import com.sevenstars.roome.domain.review.entity.ReviewGenre;
import com.sevenstars.roome.domain.review.entity.ReviewImage;
import com.sevenstars.roome.domain.review.entity.ReviewState;
import com.sevenstars.roome.domain.review.repository.ReviewGenreRepository;
import com.sevenstars.roome.domain.review.repository.ReviewImageRepository;
import com.sevenstars.roome.domain.review.repository.ReviewRepository;
import com.sevenstars.roome.domain.review.request.ReviewMandatoryRequest;
import com.sevenstars.roome.domain.review.request.ReviewOptionalRequest;
import com.sevenstars.roome.domain.review.response.ReviewIdResponse;
import com.sevenstars.roome.domain.review.response.ReviewImagesResponse;
import com.sevenstars.roome.domain.review.response.ReviewResponse;
import com.sevenstars.roome.domain.review.response.ReviewsResponse;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

import static com.sevenstars.roome.domain.profile.entity.ElementType.PREFERRED_GENRE;
import static com.sevenstars.roome.domain.profile.entity.ElementType.USER_STRENGTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@Transactional
@SpringBootTest
class ReviewServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ElementRepository elementRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ReviewGenreRepository reviewGenreRepository;
    @Autowired
    ReviewImageRepository reviewImageRepository;
    @Autowired
    ReviewService reviewService;
    @MockBean
    StorageService storageService;

    String createReviewImageUrl() {
        return "https://storage.server.com/reviews/images/" + UUID.randomUUID() + ".png";
    }

    User createUser(String email) {
        return new User("", "", email);
    }

    List<Element> createElements() {
        return List.of(
                new Element(PREFERRED_GENRE, "액션", "", "", "", "", 0),
                new Element(PREFERRED_GENRE, "범죄", "", "", "", "", 1),
                new Element(PREFERRED_GENRE, "SF", "", "", "", "", 2),
                new Element(PREFERRED_GENRE, "코미디", "", "", "", "", 3),
                new Element(PREFERRED_GENRE, "로맨스", "", "", "", "", 4),
                new Element(PREFERRED_GENRE, "스릴러", "", "", "", "", 5),
                new Element(USER_STRENGTH, "관찰력", "", "", "", "", 0),
                new Element(USER_STRENGTH, "분석력", "", "", "", "", 1),
                new Element(USER_STRENGTH, "추리력", "", "", "", "", 2));
    }

    List<Review> createReviews(User user) {
        return List.of(new Review(user, 5.0, "제로월드 홍대점", "층간 소음"),
                new Review(user, 4.5, "티켓 투 이스케이프", "갤럭시 익스프레스"),
                new Review(user, 4.5, "오아시스", "배드 타임 (BÆD TIME)"),
                new Review(user, 4.0, "제로월드 홍대점", "ALIVE"),
                new Review(user, 5.0, "지구별 방탈출 홍대라스트시티점", "섀도우"));
    }

    List<ReviewGenre> createReviewGenres(List<Review> reviews, List<Element> elements) {
        return List.of(
                new ReviewGenre(elements.get(1), reviews.get(0)),
                new ReviewGenre(elements.get(0), reviews.get(0)),
                new ReviewGenre(elements.get(3), reviews.get(1)),
                new ReviewGenre(elements.get(2), reviews.get(1)),
                new ReviewGenre(elements.get(5), reviews.get(2)),
                new ReviewGenre(elements.get(4), reviews.get(2)),
                new ReviewGenre(elements.get(0), reviews.get(3)),
                new ReviewGenre(elements.get(1), reviews.get(3)));
    }

    List<MultipartFile> createMultipartFiles(int count) {
        List<MultipartFile> files = new ArrayList<>();
        byte[] content = "Hello, World!".getBytes();

        for (int i = 0; i < count; i++) {
            files.add(new MockMultipartFile("file", i + ".png", "image/png", content));
        }
        return files;
    }

    @DisplayName("사용자 후기 조회시 페이징 후 정렬 되어 출력된다. 장르도 우선 순위로 정렬 되어 출력된다.")
    @Test
    void getReviewsTest1() {

        // Given
        List<Element> elements = createElements();
        elements.get(0).markAsDeleted();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        List<Review> reviews = createReviews(user);
        reviewRepository.saveAll(reviews);

        List<ReviewGenre> reviewGenres = createReviewGenres(reviews, elements);
        reviewGenreRepository.saveAll(reviewGenres);

        // When & Then
        ReviewsResponse response = reviewService.getReviews(user.getId(), "draft", 0, 2, "id,asc");

        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getPageSize()).isEqualTo(2);
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getReviews().size()).isEqualTo(2);
        assertThat(response.getReviews().get(0).getThemeName()).isEqualTo(reviews.get(0).getThemeName());
        assertThat(response.getReviews().get(0).getGenres().get(0).getTitle()).isEqualTo(elements.get(1).getTitle());
        assertThat(response.getReviews().get(1).getThemeName()).isEqualTo(reviews.get(1).getThemeName());
        assertThat(response.getReviews().get(1).getGenres().get(0).getTitle()).isEqualTo(elements.get(2).getTitle());
        assertThat(response.getReviews().get(1).getGenres().get(1).getTitle()).isEqualTo(elements.get(3).getTitle());

        response = reviewService.getReviews(user.getId(), "draft", 1, 2, "id,asc");

        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getPageSize()).isEqualTo(2);
        assertThat(response.getPageNumber()).isEqualTo(1);
        assertThat(response.getReviews().size()).isEqualTo(2);
        assertThat(response.getReviews().get(0).getThemeName()).isEqualTo(reviews.get(2).getThemeName());
        assertThat(response.getReviews().get(0).getGenres().get(0).getTitle()).isEqualTo(elements.get(4).getTitle());
        assertThat(response.getReviews().get(0).getGenres().get(1).getTitle()).isEqualTo(elements.get(5).getTitle());
        assertThat(response.getReviews().get(1).getThemeName()).isEqualTo(reviews.get(3).getThemeName());
        assertThat(response.getReviews().get(1).getGenres().get(0).getTitle()).isEqualTo(elements.get(1).getTitle());

        response = reviewService.getReviews(user.getId(), "draft", 2, 2, "id,asc");

        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getPageSize()).isEqualTo(2);
        assertThat(response.getPageNumber()).isEqualTo(2);
        assertThat(response.getReviews().size()).isEqualTo(1);
        assertThat(response.getReviews().get(0).getThemeName()).isEqualTo(reviews.get(4).getThemeName());
        assertThat(response.getReviews().get(0).getGenres().size()).isEqualTo(0);
    }

    @DisplayName("사용자 후기 조회시 페이징 후 정렬 되어 출력된다. 장르도 우선 순위로 정렬 되어 출력된다.")
    @Test
    void getReviewsTest2() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        List<Review> reviews = createReviews(user);
        reviewRepository.saveAll(reviews);

        List<ReviewGenre> reviewGenres = createReviewGenres(reviews, elements);
        reviewGenreRepository.saveAll(reviewGenres);

        // When & Then
        ReviewsResponse response = reviewService.getReviews(user.getId(), "draft", 0, 2, "score,asc");

        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getPageSize()).isEqualTo(2);
        assertThat(response.getPageNumber()).isEqualTo(0);
        assertThat(response.getReviews().size()).isEqualTo(2);
        assertThat(response.getReviews().get(0).getThemeName()).isEqualTo(reviews.get(3).getThemeName());
        assertThat(response.getReviews().get(0).getGenres().get(0).getTitle()).isEqualTo(elements.get(0).getTitle());
        assertThat(response.getReviews().get(0).getGenres().get(1).getTitle()).isEqualTo(elements.get(1).getTitle());
        assertThat(response.getReviews().get(1).getThemeName()).isEqualTo(reviews.get(1).getThemeName());
        assertThat(response.getReviews().get(1).getGenres().get(0).getTitle()).isEqualTo(elements.get(2).getTitle());
        assertThat(response.getReviews().get(1).getGenres().get(1).getTitle()).isEqualTo(elements.get(3).getTitle());

        response = reviewService.getReviews(user.getId(), "draft", 1, 2, "score,asc");

        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getPageSize()).isEqualTo(2);
        assertThat(response.getPageNumber()).isEqualTo(1);
        assertThat(response.getReviews().size()).isEqualTo(2);
        assertThat(response.getReviews().get(0).getThemeName()).isEqualTo(reviews.get(2).getThemeName());
        assertThat(response.getReviews().get(0).getGenres().get(0).getTitle()).isEqualTo(elements.get(4).getTitle());
        assertThat(response.getReviews().get(0).getGenres().get(1).getTitle()).isEqualTo(elements.get(5).getTitle());
        assertThat(response.getReviews().get(1).getThemeName()).isEqualTo(reviews.get(0).getThemeName());
        assertThat(response.getReviews().get(1).getGenres().get(0).getTitle()).isEqualTo(elements.get(0).getTitle());
        assertThat(response.getReviews().get(1).getGenres().get(1).getTitle()).isEqualTo(elements.get(1).getTitle());

        response = reviewService.getReviews(user.getId(), "draft", 2, 2, "score,asc");

        assertThat(response.getTotalPages()).isEqualTo(3);
        assertThat(response.getTotalElements()).isEqualTo(5);
        assertThat(response.getPageSize()).isEqualTo(2);
        assertThat(response.getPageNumber()).isEqualTo(2);
        assertThat(response.getReviews().size()).isEqualTo(1);
        assertThat(response.getReviews().get(0).getThemeName()).isEqualTo(reviews.get(4).getThemeName());
        assertThat(response.getReviews().get(0).getGenres().size()).isEqualTo(0);
    }

    @DisplayName("사용자 후기 하나 조회시 후기 내용과 장르, 이미지가 출력된다.")
    @Test
    void getReviewTest1() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        List<Review> reviews = createReviews(user);
        Review review = reviews.get(0);

        Boolean success = true;
        LocalDate playDate = LocalDate.of(2024, 8, 1);
        int totalTime = 60;
        int remainingTime = 10;
        int hintCount = 5;
        int participants = 5;
        String difficultyLevel = "어려움";
        String fearLevel = "보통";
        String activityLevel = "보통";
        double interiorRating = 4.5;
        double directionRating = 4.0;
        double storyRating = 5.0;
        String content = "꿀잼";
        boolean spoiler = false;
        boolean isPublic = true;

        review.update(success,
                playDate,
                totalTime,
                remainingTime,
                hintCount,
                participants,
                difficultyLevel,
                fearLevel,
                activityLevel,
                interiorRating,
                directionRating,
                storyRating,
                content,
                spoiler,
                isPublic);

        reviewRepository.saveAll(reviews);

        List<ReviewGenre> reviewGenres = createReviewGenres(reviews, elements);
        reviewGenreRepository.saveAll(reviewGenres);

        List<ReviewImage> existReviewImages = List.of(new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()));

        reviewImageRepository.saveAll(existReviewImages);

        // When
        ReviewResponse response = reviewService.getReview(review.getId());

        // Then
        assertThat(response.getGenres().size()).isEqualTo(2);
        assertThat(response.getGenres().get(0).getTitle()).isEqualTo(elements.get(0).getTitle());
        assertThat(response.getGenres().get(1).getTitle()).isEqualTo(elements.get(1).getTitle());
        assertThat(response.getSuccess()).isEqualTo(success);
        assertThat(response.getPlayDate()).isEqualTo(playDate);
        assertThat(response.getTotalTime()).isEqualTo(totalTime);
        assertThat(response.getRemainingTime()).isEqualTo(remainingTime);
        assertThat(response.getHintCount()).isEqualTo(hintCount);
        assertThat(response.getParticipants()).isEqualTo(participants);
        assertThat(response.getDifficultyLevel()).isEqualTo(difficultyLevel);
        assertThat(response.getFearLevel()).isEqualTo(fearLevel);
        assertThat(response.getActivityLevel()).isEqualTo(activityLevel);
        assertThat(response.getInteriorRating()).isEqualTo(interiorRating);
        assertThat(response.getDirectionRating()).isEqualTo(directionRating);
        assertThat(response.getStoryRating()).isEqualTo(storyRating);
        assertThat(response.getContent()).isEqualTo(content);
        assertThat(response.getSpoiler()).isEqualTo(spoiler);
        assertThat(response.getIsPublic()).isEqualTo(isPublic);
    }

    @DisplayName("사용자 후기 하나 조회시 후기 내용과 장르, 이미지가 출력된다.")
    @Test
    void getReviewTest2() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        List<Review> reviews = createReviews(user);
        Review review = reviews.get(0);

        Boolean success = true;
        LocalDate playDate = LocalDate.of(2024, 8, 1);
        int totalTime = 60;
        int remainingTime = 10;
        int hintCount = 5;
        int participants = 5;
        String difficultyLevel = null;
        String fearLevel = null;
        String activityLevel = null;
        double interiorRating = 4.5;
        double directionRating = 4.0;
        double storyRating = 5.0;
        String content = null;
        Boolean spoiler = null;
        Boolean isPublic = null;

        review.update(success,
                playDate,
                totalTime,
                remainingTime,
                hintCount,
                participants,
                difficultyLevel,
                fearLevel,
                activityLevel,
                interiorRating,
                directionRating,
                storyRating,
                content,
                spoiler,
                isPublic);

        reviewRepository.saveAll(reviews);

        List<ReviewGenre> reviewGenres = createReviewGenres(reviews, elements);
        reviewGenreRepository.saveAll(reviewGenres);

        List<ReviewImage> existReviewImages = List.of(new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()));

        reviewImageRepository.saveAll(existReviewImages);

        // When
        ReviewResponse response = reviewService.getReview(review.getId());

        // Then
        assertThat(response.getGenres().size()).isEqualTo(2);
        assertThat(response.getGenres().get(0).getTitle()).isEqualTo(elements.get(0).getTitle());
        assertThat(response.getGenres().get(1).getTitle()).isEqualTo(elements.get(1).getTitle());
        assertThat(response.getSuccess()).isEqualTo(success);
        assertThat(response.getPlayDate()).isEqualTo(playDate);
        assertThat(response.getTotalTime()).isEqualTo(totalTime);
        assertThat(response.getRemainingTime()).isEqualTo(remainingTime);
        assertThat(response.getHintCount()).isEqualTo(hintCount);
        assertThat(response.getParticipants()).isEqualTo(participants);
        assertThat(response.getDifficultyLevel()).isEqualTo("");
        assertThat(response.getFearLevel()).isEqualTo("");
        assertThat(response.getActivityLevel()).isEqualTo("");
        assertThat(response.getInteriorRating()).isEqualTo(interiorRating);
        assertThat(response.getDirectionRating()).isEqualTo(directionRating);
        assertThat(response.getStoryRating()).isEqualTo(storyRating);
        assertThat(response.getContent()).isEqualTo("");
        assertThat(response.getSpoiler()).isTrue();
        assertThat(response.getIsPublic()).isFalse();
    }

    @DisplayName("사용자 후기 하나 생성시 평점은 소숫점 첫 번째 자리로 반올림된다. 장르는 우선순위로 정렬되어 저장된다.")
    @Test
    void createReviewTest1() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double score = 4.33;
        String storeName = "제로월드 홍대점";
        String themeName = "층간소음";
        List<Element> genres = List.of(elements.get(5), elements.get(1));
        List<Long> genreIds = genres.stream()
                .map(Element::getId)
                .toList();

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(score);
        request.setStoreName(storeName);
        request.setThemeName(themeName);
        request.setGenreIds(genreIds);

        // When
        ReviewIdResponse response = reviewService.createReview(user.getId(), request);

        // Then
        Long id = response.getId();
        assertThat(id).isNotNull();

        Optional<Review> optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview).isPresent();

        Review review = optionalReview.get();
        assertThat(review.getState()).isEqualTo(ReviewState.DRAFT);
        assertThat(review.getId()).isEqualTo(id);
        assertThat(review.getScore()).isEqualTo(4.3);
        assertThat(review.getStoreName()).isEqualTo(storeName);
        assertThat(review.getThemeName()).isEqualTo(themeName);

        List<ReviewGenre> reviewGenres = reviewGenreRepository.findByReview(review);
        assertThat(reviewGenres.size()).isEqualTo(2);
        assertThat(reviewGenres.get(0).getElement().getTitle()).isEqualTo(elements.get(1).getTitle());
        assertThat(reviewGenres.get(1).getElement().getTitle()).isEqualTo(elements.get(5).getTitle());
    }

    @DisplayName("사용자 후기 하나 생성시 장르가 없으면 빈 리스트로 저장된다.")
    @Test
    void createReviewTest2() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double score = 4.33;
        String storeName = "제로월드 홍대점";
        String themeName = "층간소음";

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(score);
        request.setStoreName(storeName);
        request.setThemeName(themeName);
        request.setGenreIds(null);

        // When
        ReviewIdResponse response = reviewService.createReview(user.getId(), request);

        // Then
        Long id = response.getId();
        assertThat(id).isNotNull();

        Optional<Review> optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview).isPresent();

        Review review = optionalReview.get();
        assertThat(review.getState()).isEqualTo(ReviewState.DRAFT);
        assertThat(review.getId()).isEqualTo(id);
        assertThat(review.getScore()).isEqualTo(4.3);
        assertThat(review.getStoreName()).isEqualTo(storeName);
        assertThat(review.getThemeName()).isEqualTo(themeName);

        List<ReviewGenre> reviewGenres = reviewGenreRepository.findByReview(review);
        assertThat(reviewGenres.size()).isEqualTo(0);
    }

    @DisplayName("사용자 후기 하나 생성시 장르가 없으면 빈 리스트로 저장된다.")
    @Test
    void createReviewTest3() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double score = 4.33;
        String storeName = "제로월드 홍대점";
        String themeName = "층간소음";

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(score);
        request.setStoreName(storeName);
        request.setThemeName(themeName);
        request.setGenreIds(Collections.emptyList());

        // When
        ReviewIdResponse response = reviewService.createReview(user.getId(), request);

        // Then
        Long id = response.getId();
        assertThat(id).isNotNull();

        Optional<Review> optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview).isPresent();

        Review review = optionalReview.get();
        assertThat(review.getState()).isEqualTo(ReviewState.DRAFT);
        assertThat(review.getId()).isEqualTo(id);
        assertThat(review.getScore()).isEqualTo(4.3);
        assertThat(review.getStoreName()).isEqualTo(storeName);
        assertThat(review.getThemeName()).isEqualTo(themeName);

        List<ReviewGenre> reviewGenres = reviewGenreRepository.findByReview(review);
        assertThat(reviewGenres.size()).isEqualTo(0);
    }

    @DisplayName("사용자 후기 하나 생성시 장르 개수가 최대 개수를 초과하면 예외가 발생한다.")
    @Test
    void createReviewTest4() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double score = 4.33;
        String storeName = "제로월드 홍대점";
        String themeName = "층간소음";
        List<Element> genres = List.of(elements.get(5), elements.get(1), elements.get(2));
        List<Long> genreIds = genres.stream()
                .map(Element::getId)
                .toList();

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(score);
        request.setStoreName(storeName);
        request.setThemeName(themeName);
        request.setGenreIds(genreIds);

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("사용자 후기 하나 생성시 장르 ID가 존재하지 않을 경우 예외가 발생한다.")
    @Test
    void createReviewTest5() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double score = 4.33;
        String storeName = "제로월드 홍대점";
        String themeName = "층간소음";
        List<Element> genres = List.of(elements.get(5), elements.get(elements.size() - 1));
        List<Long> genreIds = genres.stream()
                .map(Element::getId)
                .toList();

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(score);
        request.setStoreName(storeName);
        request.setThemeName(themeName);
        request.setGenreIds(genreIds);

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("사용자 후기 하나 생성시 매장 이름이 없으면 예외가 발생한다.")
    @Test
    void createReviewTest6() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double score = 4.33;
        String storeName = "제로월드 홍대점";
        String themeName = "층간소음";
        List<Element> genres = List.of(elements.get(5), elements.get(1));
        List<Long> genreIds = genres.stream()
                .map(Element::getId)
                .toList();

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(score);
        request.setStoreName("");
        request.setThemeName(themeName);
        request.setGenreIds(genreIds);

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("사용자 후기 하나 생성시 테마 이름이 없으면 예외가 발생한다.")
    @Test
    void createReviewTest7() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double score = 4.33;
        String storeName = "제로월드 홍대점";
        String themeName = "층간소음";
        List<Element> genres = List.of(elements.get(5), elements.get(1));
        List<Long> genreIds = genres.stream()
                .map(Element::getId)
                .toList();

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(score);
        request.setStoreName(storeName);
        request.setThemeName("");
        request.setGenreIds(genreIds);

        // When & Then
        assertThatThrownBy(() -> reviewService.createReview(user.getId(), request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("필수 항목 업데이트시 필수 항목 값이 변경된다.")
    @Test
    void updateMandatoryContentsTest1() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double prevScore = 4.33;
        String prevStoreName = "제로월드 홍대점";
        String prevThemeName = "층간소음";
        Review review = new Review(user, prevScore, prevStoreName, prevThemeName);

        List<ReviewGenre> reviewGenres = List.of(new ReviewGenre(elements.get(5), review),
                new ReviewGenre(elements.get(1), review));

        reviewRepository.save(review);
        reviewGenreRepository.saveAll(reviewGenres);

        Long id = review.getId();
        Double newScore = 4.1;
        String newStoreName = "티켓 투 이스케이프";
        String newThemeName = "갤럭시 익스프레스";

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(newScore);
        request.setStoreName(newStoreName);
        request.setThemeName(newThemeName);
        List<Element> genres = List.of(elements.get(2), elements.get(0));
        List<Long> genreIds = genres.stream()
                .map(Element::getId)
                .toList();

        request.setGenreIds(genreIds);

        // When
        reviewService.updateMandatoryContents(id, request);

        // Then
        Optional<Review> optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview).isPresent();

        Review foundReview = optionalReview.get();
        assertThat(foundReview.getState()).isEqualTo(ReviewState.DRAFT);
        assertThat(foundReview.getId()).isEqualTo(id);
        assertThat(foundReview.getScore()).isEqualTo(newScore);
        assertThat(foundReview.getStoreName()).isEqualTo(newStoreName);
        assertThat(foundReview.getThemeName()).isEqualTo(newThemeName);

        List<ReviewGenre> foundReviewGenres = reviewGenreRepository.findByReview(foundReview);
        assertThat(foundReviewGenres.size()).isEqualTo(2);
        assertThat(foundReviewGenres.get(0).getElement().getTitle()).isEqualTo(elements.get(0).getTitle());
        assertThat(foundReviewGenres.get(1).getElement().getTitle()).isEqualTo(elements.get(2).getTitle());
    }

    @DisplayName("필수 항목 업데이트시 장르가 없으면 빈 리스트로 저장된다.")
    @Test
    void updateMandatoryContentsTest2() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double prevScore = 4.33;
        String prevStoreName = "제로월드 홍대점";
        String prevThemeName = "층간소음";
        Review review = new Review(user, prevScore, prevStoreName, prevThemeName);

        List<ReviewGenre> reviewGenres = List.of(new ReviewGenre(elements.get(5), review),
                new ReviewGenre(elements.get(1), review));

        reviewRepository.save(review);
        reviewGenreRepository.saveAll(reviewGenres);

        Long id = review.getId();
        Double newScore = 4.1;
        String newStoreName = "티켓 투 이스케이프";
        String newThemeName = "갤럭시 익스프레스";

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(newScore);
        request.setStoreName(newStoreName);
        request.setThemeName(newThemeName);
        request.setGenreIds(null);

        // When
        reviewService.updateMandatoryContents(id, request);

        // Then
        Optional<Review> optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview).isPresent();

        Review foundReview = optionalReview.get();
        assertThat(foundReview.getState()).isEqualTo(ReviewState.DRAFT);
        assertThat(foundReview.getId()).isEqualTo(id);
        assertThat(foundReview.getScore()).isEqualTo(newScore);
        assertThat(foundReview.getStoreName()).isEqualTo(newStoreName);
        assertThat(foundReview.getThemeName()).isEqualTo(newThemeName);

        List<ReviewGenre> foundReviewGenres = reviewGenreRepository.findByReview(foundReview);
        assertThat(foundReviewGenres.size()).isEqualTo(0);
    }

    @DisplayName("필수 항목 업데이트시 장르가 없으면 빈 리스트로 저장된다.")
    @Test
    void updateMandatoryContentsTest3() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double prevScore = 4.33;
        String prevStoreName = "제로월드 홍대점";
        String prevThemeName = "층간소음";
        Review review = new Review(user, prevScore, prevStoreName, prevThemeName);

        List<ReviewGenre> reviewGenres = List.of(new ReviewGenre(elements.get(5), review),
                new ReviewGenre(elements.get(1), review));

        reviewRepository.save(review);
        reviewGenreRepository.saveAll(reviewGenres);

        Long id = review.getId();
        Double newScore = 4.1;
        String newStoreName = "티켓 투 이스케이프";
        String newThemeName = "갤럭시 익스프레스";

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(newScore);
        request.setStoreName(newStoreName);
        request.setThemeName(newThemeName);
        request.setGenreIds(Collections.emptyList());

        // When
        reviewService.updateMandatoryContents(id, request);

        // Then
        Optional<Review> optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview).isPresent();

        Review foundReview = optionalReview.get();
        assertThat(foundReview.getState()).isEqualTo(ReviewState.DRAFT);
        assertThat(foundReview.getId()).isEqualTo(id);
        assertThat(foundReview.getScore()).isEqualTo(newScore);
        assertThat(foundReview.getStoreName()).isEqualTo(newStoreName);
        assertThat(foundReview.getThemeName()).isEqualTo(newThemeName);

        List<ReviewGenre> foundReviewGenres = reviewGenreRepository.findByReview(foundReview);
        assertThat(foundReviewGenres.size()).isEqualTo(0);
    }

    @DisplayName("필수 항목 업데이트시 장르 개수가 최대 개수를 초과하면 예외가 발생한다.")
    @Test
    void updateMandatoryContentsTest4() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double prevScore = 4.33;
        String prevStoreName = "제로월드 홍대점";
        String prevThemeName = "층간소음";
        Review review = new Review(user, prevScore, prevStoreName, prevThemeName);

        List<ReviewGenre> reviewGenres = List.of(new ReviewGenre(elements.get(5), review),
                new ReviewGenre(elements.get(1), review));

        reviewRepository.save(review);
        reviewGenreRepository.saveAll(reviewGenres);

        Long id = review.getId();
        Double newScore = 4.1;
        String newStoreName = "티켓 투 이스케이프";
        String newThemeName = "갤럭시 익스프레스";

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(newScore);
        request.setStoreName(newStoreName);
        request.setThemeName(newThemeName);
        List<Element> genres = List.of(elements.get(2), elements.get(0), elements.get(5));
        List<Long> genreIds = genres.stream()
                .map(Element::getId)
                .toList();
        request.setGenreIds(genreIds);

        // When & Then
        assertThatThrownBy(() -> reviewService.updateMandatoryContents(id, request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("필수 항목 업데이트시 장르 ID가 존재하지 않을 경우 예외가 발생한다.")
    @Test
    void updateMandatoryContentsTest5() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double prevScore = 4.33;
        String prevStoreName = "제로월드 홍대점";
        String prevThemeName = "층간소음";
        Review review = new Review(user, prevScore, prevStoreName, prevThemeName);

        List<ReviewGenre> reviewGenres = List.of(new ReviewGenre(elements.get(5), review),
                new ReviewGenre(elements.get(1), review));

        reviewRepository.save(review);
        reviewGenreRepository.saveAll(reviewGenres);

        Long id = review.getId();
        Double newScore = 4.1;
        String newStoreName = "티켓 투 이스케이프";
        String newThemeName = "갤럭시 익스프레스";

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(newScore);
        request.setStoreName(newStoreName);
        request.setThemeName(newThemeName);
        List<Element> genres = List.of(elements.get(2), elements.get(elements.size() - 1));
        List<Long> genreIds = genres.stream()
                .map(Element::getId)
                .toList();
        request.setGenreIds(genreIds);

        // When & Then
        assertThatThrownBy(() -> reviewService.updateMandatoryContents(id, request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("필수 항목 업데이트시 매장 이름이 없으면 예외가 발생한다.")
    @Test
    void updateMandatoryContentsTest6() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double prevScore = 4.33;
        String prevStoreName = "제로월드 홍대점";
        String prevThemeName = "층간소음";
        Review review = new Review(user, prevScore, prevStoreName, prevThemeName);

        List<ReviewGenre> reviewGenres = List.of(new ReviewGenre(elements.get(5), review),
                new ReviewGenre(elements.get(1), review));

        reviewRepository.save(review);
        reviewGenreRepository.saveAll(reviewGenres);

        Long id = review.getId();
        Double newScore = 4.1;
        String newStoreName = "티켓 투 이스케이프";
        String newThemeName = "갤럭시 익스프레스";

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(newScore);
        request.setStoreName("");
        request.setThemeName(newThemeName);
        List<Element> genres = List.of(elements.get(2), elements.get(0));
        List<Long> genreIds = genres.stream()
                .map(Element::getId)
                .toList();

        request.setGenreIds(genreIds);

        // When & Then
        assertThatThrownBy(() -> reviewService.updateMandatoryContents(id, request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("필수 항목 업데이트시 테마 이름이 없으면 예외가 발생한다.")
    @Test
    void updateMandatoryContentsTest7() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double prevScore = 4.33;
        String prevStoreName = "제로월드 홍대점";
        String prevThemeName = "층간소음";
        Review review = new Review(user, prevScore, prevStoreName, prevThemeName);

        List<ReviewGenre> reviewGenres = List.of(new ReviewGenre(elements.get(5), review),
                new ReviewGenre(elements.get(1), review));

        reviewRepository.save(review);
        reviewGenreRepository.saveAll(reviewGenres);

        Long id = review.getId();
        Double newScore = 4.1;
        String newStoreName = "티켓 투 이스케이프";
        String newThemeName = "갤럭시 익스프레스";

        ReviewMandatoryRequest request = new ReviewMandatoryRequest();
        request.setScore(newScore);
        request.setStoreName(newStoreName);
        request.setThemeName("");
        List<Element> genres = List.of(elements.get(2), elements.get(0));
        List<Long> genreIds = genres.stream()
                .map(Element::getId)
                .toList();

        request.setGenreIds(genreIds);

        // When & Then
        assertThatThrownBy(() -> reviewService.updateMandatoryContents(id, request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("상세 후기 업데이트시 이미지 파일이 저장된다.")
    @Test
    void updateOptionalContentsTest1() {

        // Given
        int uploadFileCount = 5;
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        List<Review> reviews = createReviews(user);
        reviewRepository.saveAll(reviews);
        Review review = reviews.get(0);

        List<ReviewGenre> reviewGenres = createReviewGenres(reviews, elements);
        reviewGenreRepository.saveAll(reviewGenres);

        List<MultipartFile> multipartFiles = createMultipartFiles(uploadFileCount);
        willDoNothing().given(storageService).deleteImage(any(), any());
        given(storageService.saveImage(any(), any())).willAnswer((Answer<String>) invocation -> createReviewImageUrl());

        ReviewOptionalRequest request = new ReviewOptionalRequest();
        request.setImageUrls(Collections.emptyList());

        // When
        ReviewImagesResponse response = reviewService.updateOptionalContents(review.getId(), multipartFiles, request);

        // Then
        List<String> imageUrls = response.getImageUrls();
        assertThat(imageUrls.size()).isEqualTo(uploadFileCount);

        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
        assertThat(reviewImages.size()).isEqualTo(uploadFileCount);

        for (int i = 0; i < reviewImages.size(); i++) {
            assertThat(reviewImages.get(i).getUrl()).isEqualTo(imageUrls.get(i));
        }
    }

    @DisplayName("상세 후기 업데이트시 이미지 개수 초과시 예외가 발생한다.")
    @Test
    void updateOptionalContentsTest2() {

        // Given
        int uploadFileCount = 6;
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        List<Review> reviews = createReviews(user);
        reviewRepository.saveAll(reviews);
        Review review = reviews.get(0);

        List<ReviewGenre> reviewGenres = createReviewGenres(reviews, elements);
        reviewGenreRepository.saveAll(reviewGenres);

        List<MultipartFile> multipartFiles = createMultipartFiles(uploadFileCount);
        willDoNothing().given(storageService).deleteImage(any(), any());
        given(storageService.saveImage(any(), any())).willAnswer((Answer<String>) invocation -> createReviewImageUrl());

        ReviewOptionalRequest request = new ReviewOptionalRequest();
        request.setImageUrls(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> reviewService.updateOptionalContents(review.getId(), multipartFiles, request))
                .isInstanceOf(CustomClientErrorException.class);
    }

    @DisplayName("상세 후기 업데이트시 기존 이미지 일부를 삭제하고 새로운 이미지를 기존 이미지 뒤로 업로드 한다.")
    @Test
    void updateOptionalContentsTest3() {

        // Given
        int uploadFileCount = 2;
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        List<Review> reviews = createReviews(user);
        reviewRepository.saveAll(reviews);
        Review review = reviews.get(0);

        List<ReviewGenre> reviewGenres = createReviewGenres(reviews, elements);
        reviewGenreRepository.saveAll(reviewGenres);

        List<ReviewImage> existReviewImages = List.of(new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()));

        reviewImageRepository.saveAll(existReviewImages);

        List<MultipartFile> multipartFiles = createMultipartFiles(uploadFileCount);
        willDoNothing().given(storageService).deleteImage(any(), any());
        given(storageService.saveImage(any(), any())).willAnswer((Answer<String>) invocation -> createReviewImageUrl());

        ReviewOptionalRequest request = new ReviewOptionalRequest();

        List<String> deleteImageUrls = List.of(
                existReviewImages.get(1).getUrl(),
                existReviewImages.get(3).getUrl());

        List<String> existImageUrls = List.of(
                existReviewImages.get(0).getUrl(),
                existReviewImages.get(2).getUrl(),
                existReviewImages.get(4).getUrl());

        request.setImageUrls(existImageUrls);

        // When
        ReviewImagesResponse response = reviewService.updateOptionalContents(review.getId(), multipartFiles, request);

        // Then
        List<String> imageUrls = response.getImageUrls();
        assertThat(imageUrls.size()).isEqualTo(existImageUrls.size() + uploadFileCount);

        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
        assertThat(reviewImages.size()).isEqualTo(existImageUrls.size() + uploadFileCount);

        for (int i = 0; i < existImageUrls.size(); i++) {
            assertThat(reviewImages.get(i).getUrl()).isEqualTo(existImageUrls.get(i));
        }

        for (int i = 0; i < reviewImages.size(); i++) {
            assertThat(reviewImages.get(i).getUrl()).isEqualTo(imageUrls.get(i));
            assertThat(reviewImages.get(i).getUrl()).isNotIn(deleteImageUrls);
        }
    }

    @DisplayName("후기 등록시 후기 상태가 Publish로 변경된다.")
    @Test
    void publishReviewTest() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double prevScore = 4.33;
        String prevStoreName = "제로월드 홍대점";
        String prevThemeName = "층간소음";
        Review review = new Review(user, prevScore, prevStoreName, prevThemeName);

        List<ReviewGenre> reviewGenres = List.of(new ReviewGenre(elements.get(5), review),
                new ReviewGenre(elements.get(1), review));

        reviewRepository.save(review);
        reviewGenreRepository.saveAll(reviewGenres);

        Long id = review.getId();

        // When
        reviewService.publishReview(id);

        // Then
        Optional<Review> optionalReview = reviewRepository.findById(id);
        assertThat(optionalReview).isPresent();

        Review foundReview = optionalReview.get();
        assertThat(foundReview.getState()).isEqualTo(ReviewState.PUBLISHED);
    }

    @DisplayName("후기 삭제시 후기가 삭제되고 스토리지에서 이미지가 삭제된다.")
    @Test
    void deleteReviewTest() {

        // Given
        List<Element> elements = createElements();
        elementRepository.saveAll(elements);

        User user = createUser("roome.dev@gmail.com");
        userRepository.save(user);

        Double prevScore = 4.33;
        String prevStoreName = "제로월드 홍대점";
        String prevThemeName = "층간소음";
        Review review = new Review(user, prevScore, prevStoreName, prevThemeName);

        List<ReviewGenre> reviewGenres = List.of(new ReviewGenre(elements.get(5), review),
                new ReviewGenre(elements.get(1), review));

        reviewRepository.save(review);
        reviewGenreRepository.saveAll(reviewGenres);

        List<ReviewImage> existReviewImages = List.of(new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()),
                new ReviewImage(review, createReviewImageUrl()));

        reviewImageRepository.saveAll(existReviewImages);

        willDoNothing().given(storageService).deleteImage(any(), any());
        given(storageService.saveImage(any(), any())).willAnswer((Answer<String>) invocation -> createReviewImageUrl());

        Long id = review.getId();

        // When
        reviewService.deleteReview(id);

        // Then
        assertThat(reviewRepository.findById(id)).isEmpty();
        assertThat(reviewGenreRepository.findByReview(review)).isEmpty();
        assertThat(reviewImageRepository.findByReview(review)).isEmpty();
    }
}
