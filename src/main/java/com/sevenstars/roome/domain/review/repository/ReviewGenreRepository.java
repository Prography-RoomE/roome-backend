package com.sevenstars.roome.domain.review.repository;

import com.sevenstars.roome.domain.review.entity.Review;
import com.sevenstars.roome.domain.review.entity.ReviewGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewGenreRepository extends JpaRepository<ReviewGenre, Long> {

    @Query("SELECT rg FROM ReviewGenre rg " +
            "JOIN FETCH rg.element e " +
            "WHERE rg.review = :review " +
            "AND e.isDeleted = false " +
            "ORDER BY e.priority ASC")
    List<ReviewGenre> findByReview(Review review);

    @Query("SELECT rg FROM ReviewGenre rg " +
            "JOIN FETCH rg.element e " +
            "WHERE rg.review.id IN :ids " +
            "AND e.isDeleted = false")
    List<ReviewGenre> findByReviewIds(List<Long> ids);
}
