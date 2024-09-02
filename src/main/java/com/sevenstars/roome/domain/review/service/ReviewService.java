package com.sevenstars.roome.domain.review.service;

import com.sevenstars.roome.domain.common.service.StorageService;
import com.sevenstars.roome.domain.profile.entity.Element;
import com.sevenstars.roome.domain.profile.repository.ElementRepository;
import com.sevenstars.roome.domain.review.entity.Review;
import com.sevenstars.roome.domain.review.entity.ReviewGenre;
import com.sevenstars.roome.domain.review.entity.ReviewImage;
import com.sevenstars.roome.domain.review.repository.ReviewGenreRepository;
import com.sevenstars.roome.domain.review.repository.ReviewImageRepository;
import com.sevenstars.roome.domain.review.repository.ReviewQueryRepository;
import com.sevenstars.roome.domain.review.repository.ReviewRepository;
import com.sevenstars.roome.domain.review.request.ReviewMandatoryRequest;
import com.sevenstars.roome.domain.review.request.ReviewOptionalRequest;
import com.sevenstars.roome.domain.review.response.*;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static com.sevenstars.roome.domain.profile.entity.ElementType.PREFERRED_GENRE;
import static com.sevenstars.roome.global.common.response.Result.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {
    private static final Integer REVIEW_MAX_SIZE = 100;
    private static final Integer REVIEW_DEFAULT_SIZE = 10;
    private static final Integer REVIEW_MIN_SIZE = 1;
    private static final Integer REVIEW_IMAGE_MAX_SIZE = 5;
    private static final String REVIEWS_IMAGES_PATH = "/reviews/images";

    private final StorageService storageService;
    private final UserRepository userRepository;
    private final ElementRepository elementRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewQueryRepository reviewQueryRepository;
    private final ReviewGenreRepository reviewGenreRepository;
    private final ReviewImageRepository reviewImageRepository;

    public GenresResponse getGenres() {
        return GenresResponse.from(elementRepository.findByTypeAndIsDeletedFalseOrderByPriorityAsc(PREFERRED_GENRE));
    }

    public ReviewsResponse getReviews(Long userId, String state, Integer page, Integer size, String sort) {

        User user = findUserById(userId);

        if (size == null) {
            size = REVIEW_DEFAULT_SIZE;
        } else if (size < REVIEW_MIN_SIZE) {
            size = REVIEW_MIN_SIZE;
        } else if (size > REVIEW_MAX_SIZE) {
            size = REVIEW_MAX_SIZE;
        }

        if (page == null) {
            page = 0;
        }

        Page<Review> reviewPage = reviewQueryRepository.findByUser(user, state, page, size, sort);
        int totalPages = reviewPage.getTotalPages();

        long totalElements = reviewPage.getTotalElements();
        int pageSize = reviewPage.getPageable().getPageSize();
        int pageNumber = reviewPage.getPageable().getPageNumber();

        List<Review> reviews = reviewPage.getContent();
        List<Long> ids = reviews.stream().map(Review::getId).toList();

        List<ReviewGenre> reviewGenres = reviewGenreRepository.findByReviewIds(ids);

        Map<Long, List<Element>> genres = reviewGenres.stream()
                .collect(Collectors.groupingBy(reviewGenre -> reviewGenre.getReview().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .map(ReviewGenre::getElement)
                                        .sorted(Comparator.comparingInt(Element::getPriority))
                                        .collect(Collectors.toList()))));

        // 장르가 없는 후기는 빈 리스트로 초기화
        ids.forEach(id -> genres.putIfAbsent(id, new ArrayList<>()));

        return ReviewsResponse.of(totalPages, pageNumber, pageSize, totalElements, reviews, genres);
    }

    public ReviewResponse getReview(Long id) {

        Review review = findReviewById(id);

        List<ReviewGenre> reviewGenres = reviewGenreRepository.findByReview(review);
        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);

        return ReviewResponse.of(review, reviewGenres, reviewImages);
    }

    @Transactional
    public ReviewIdResponse createReview(Long userId, ReviewMandatoryRequest request) {

        User user = findUserById(userId);

        validateMandatoryRequest(request);

        List<Element> genres = findGenres(request.getGenreIds());

        Review review = reviewRepository.save(new Review(user, request.getScore(), request.getStoreName(), request.getThemeName()));

        saveReviewGenres(review, genres);

        return new ReviewIdResponse(review.getId());
    }

    @Transactional
    public void updateMandatoryContents(Long id, ReviewMandatoryRequest request) {

        Review review = findReviewById(id);

        validateMandatoryRequest(request);

        List<Element> genres = findGenres(request.getGenreIds());

        review.update(request.getScore(), request.getStoreName(), request.getThemeName());

        updateReviewGenres(review, genres);
    }

    @Transactional
    public ReviewImagesResponse updateOptionalContents(Long id, List<MultipartFile> files, ReviewOptionalRequest request) {

        Review review = findReviewById(id);
        List<String> imageUrls = getImageUrls(request);
        List<MultipartFile> validatedFiles = validateFiles(files);
        validateImageCount(imageUrls, validatedFiles);

        updateReview(review, request);

        List<String> updatedImageUrls = processReviewImages(review, imageUrls, validatedFiles);

        return new ReviewImagesResponse(updatedImageUrls);
    }

    @Transactional
    public void publishReview(Long id) {

        Review review = findReviewById(id);
        review.publish();
    }

    @Transactional
    public void deleteReview(Long id) {

        Review review = findReviewById(id);

        List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
        List<ReviewGenre> reviewGenres = reviewGenreRepository.findByReview(review);

        for (ReviewImage reviewImage : reviewImages) {
            String url = reviewImage.getUrl();
            storageService.deleteImage(REVIEWS_IMAGES_PATH, url);
        }

        reviewImageRepository.deleteAll(reviewImages);
        reviewGenreRepository.deleteAll(reviewGenres);
        reviewRepository.delete(review);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));
    }

    private Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new CustomClientErrorException(REVIEW_NOT_FOUND));
    }

    private List<String> getImageUrls(ReviewOptionalRequest request) {
        return request.getImageUrls() == null ? Collections.emptyList() : request.getImageUrls();
    }

    private List<MultipartFile> validateFiles(List<MultipartFile> files) {
        return files == null ? Collections.emptyList() : files;
    }

    private void validateImageCount(List<String> imageUrls, List<MultipartFile> files) {
        if (imageUrls.size() + files.size() > REVIEW_IMAGE_MAX_SIZE) {
            throw new CustomClientErrorException(REVIEW_IMAGES_EXCEEDED);
        }
    }

    private void updateReview(Review review, ReviewOptionalRequest request) {
        review.update(
                request.getSuccess(),
                request.getPlayDate(),
                request.getTotalTime(),
                request.getRemainingTime(),
                request.getHintCount(),
                request.getParticipants(),
                request.getDifficultyLevel(),
                request.getFearLevel(),
                request.getActivityLevel(),
                request.getInteriorRating(),
                request.getDirectionRating(),
                request.getStoryRating(),
                request.getContent(),
                request.getSpoiler(),
                request.getIsPublic()
        );
    }

    private List<String> processReviewImages(Review review, List<String> imageUrls, List<MultipartFile> files) {
        List<ReviewImage> existingReviewImages = reviewImageRepository.findByReview(review);

        // 기존 이미지 중 삭제 필요한 이미지 삭제 처리
        List<ReviewImage> deleteReviewImages = getImagesToDelete(existingReviewImages, imageUrls);
        deleteReviewImages(deleteReviewImages);

        List<String> updatedImageUrls = getRemainingImageUrls(existingReviewImages, deleteReviewImages);

        // 새 이미지 처리
        List<ReviewImage> newReviewImages = saveNewImages(files, review, updatedImageUrls);
        reviewImageRepository.saveAll(newReviewImages);

        return updatedImageUrls;
    }

    private List<ReviewImage> getImagesToDelete(List<ReviewImage> existingImages, List<String> imageUrls) {
        return existingImages.stream()
                .filter(image -> !imageUrls.contains(image.getUrl()))
                .collect(Collectors.toList());
    }

    private List<String> getRemainingImageUrls(List<ReviewImage> existingImages, List<ReviewImage> deleteReviewImages) {
        return existingImages.stream()
                .filter(image -> !deleteReviewImages.contains(image))
                .map(ReviewImage::getUrl)
                .collect(Collectors.toList());
    }

    private void deleteReviewImages(List<ReviewImage> deleteReviewImages) {
        deleteReviewImages.forEach(image -> storageService.deleteImage(REVIEWS_IMAGES_PATH, image.getUrl()));
        reviewImageRepository.deleteAll(deleteReviewImages);
    }

    private List<ReviewImage> saveNewImages(List<MultipartFile> files, Review review, List<String> updatedImageUrls) {
        List<ReviewImage> newReviewImages = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = storageService.saveImage(REVIEWS_IMAGES_PATH, file);
            updatedImageUrls.add(url);
            newReviewImages.add(new ReviewImage(review, url));
        }
        return newReviewImages;
    }

    private void validateMandatoryRequest(ReviewMandatoryRequest request) {
        if (!StringUtils.hasText(request.getStoreName())) {
            throw new CustomClientErrorException(REVIEW_STORE_NAME_BLANK);
        }

        if (!StringUtils.hasText(request.getThemeName())) {
            throw new CustomClientErrorException(REVIEW_THEME_NAME_BLANK);
        }
    }

    private List<Element> findGenres(List<Long> genreIds) {
        if (genreIds == null || genreIds.isEmpty()) {
            return Collections.emptyList();
        }

        if (genreIds.size() > PREFERRED_GENRE.getMaxSize()) {
            throw new CustomClientErrorException(REVIEW_GENRES_EXCEEDED);
        }

        List<Element> genres = elementRepository.findByIdInAndTypeAndIsDeletedFalseOrderByPriorityAsc(genreIds, PREFERRED_GENRE);

        if (genreIds.size() != genres.size()) {
            throw new CustomClientErrorException(REVIEW_GENRES_NOT_FOUND);
        }

        return genres;
    }

    private void saveReviewGenres(Review review, List<Element> genres) {
        for (Element genre : genres) {
            reviewGenreRepository.save(new ReviewGenre(genre, review));
        }
    }

    private void updateReviewGenres(Review review, List<Element> genres) {
        List<Long> reviewGenreIds = reviewGenreRepository.findByReview(review).stream()
                .map(ReviewGenre::getId).collect(Collectors.toList());

        reviewGenreRepository.deleteAllById(reviewGenreIds);

        saveReviewGenres(review, genres);
    }
}
