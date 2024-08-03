package com.sevenstars.roome.domain.review.repository;

import com.sevenstars.roome.domain.review.entity.Review;
import com.sevenstars.roome.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser(User user);
}
