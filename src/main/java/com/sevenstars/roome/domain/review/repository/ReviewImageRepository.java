package com.sevenstars.roome.domain.review.repository;

import com.sevenstars.roome.domain.review.entity.Review;
import com.sevenstars.roome.domain.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReview(Review review);
}
