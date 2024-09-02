package com.sevenstars.roome.domain.review.repository;

import com.sevenstars.roome.domain.TestQuerydslConfig;
import com.sevenstars.roome.domain.review.entity.Review;
import com.sevenstars.roome.domain.review.entity.ReviewState;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestQuerydslConfig.class)
@DataJpaTest
class ReviewQueryRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    ReviewQueryRepository reviewQueryRepository;


    @DisplayName("사용자 후기 조회시 페이징 후 정렬 되어 출력된다.")
    @Test
    void findByUserTest1() {

        // Given
        User user = new User("", "", "roome.dev@gmail.com");

        List<Review> reviews = List.of(new Review(user, 5.0, "제로월드 홍대점", "층간 소음"),
                new Review(user, 4.5, "티켓 투 이스케이프", "갤럭시 익스프레스"),
                new Review(user, 4.5, "오아시스", "배드 타임 (BÆD TIME)"),
                new Review(user, 4.0, "제로월드 홍대점", "ALIVE"),
                new Review(user, 5.0, "지구별 방탈출 홍대라스트시티점", "섀도우"));

        userRepository.save(user);
        reviewRepository.saveAll(reviews);

        // When & Then
        Page<Review> page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 0, 2, "id,asc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getContent().size()).isEqualTo(2);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(0));
        assertThat(page.getContent().get(1)).isEqualTo(reviews.get(1));

        page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 1, 2, "id,asc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getContent().size()).isEqualTo(2);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(2));
        assertThat(page.getContent().get(1)).isEqualTo(reviews.get(3));

        page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 2, 2, "id,asc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(2);
        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(4));
    }

    @DisplayName("사용자 후기 조회시 페이징 후 정렬 되어 출력된다.")
    @Test
    void findByUserTest2() {

        // Given
        User user = new User("", "", "roome.dev@gmail.com");

        List<Review> reviews = List.of(new Review(user, 5.0, "제로월드 홍대점", "층간 소음"),
                new Review(user, 4.5, "티켓 투 이스케이프", "갤럭시 익스프레스"),
                new Review(user, 4.5, "오아시스", "배드 타임 (BÆD TIME)"),
                new Review(user, 4.0, "제로월드 홍대점", "ALIVE"),
                new Review(user, 5.0, "지구별 방탈출 홍대라스트시티점", "섀도우"));

        userRepository.save(user);
        reviewRepository.saveAll(reviews);

        // When & Then
        Page<Review> page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 0, 2, "id,desc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getContent().size()).isEqualTo(2);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(4));
        assertThat(page.getContent().get(1)).isEqualTo(reviews.get(3));

        page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 1, 2, "id,desc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getContent().size()).isEqualTo(2);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(2));
        assertThat(page.getContent().get(1)).isEqualTo(reviews.get(1));

        page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 2, 2, "id,desc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(2);
        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(0));
    }

    @DisplayName("사용자 후기 조회시 페이징 후 정렬 되어 출력된다.")
    @Test
    void findByUserTest3() {

        // Given
        User user = new User("", "", "roome.dev@gmail.com");

        List<Review> reviews = List.of(new Review(user, 5.0, "제로월드 홍대점", "층간 소음"),
                new Review(user, 4.5, "티켓 투 이스케이프", "갤럭시 익스프레스"),
                new Review(user, 4.5, "오아시스", "배드 타임 (BÆD TIME)"),
                new Review(user, 4.0, "제로월드 홍대점", "ALIVE"),
                new Review(user, 5.0, "지구별 방탈출 홍대라스트시티점", "섀도우"));

        userRepository.save(user);
        reviewRepository.saveAll(reviews);

        // When & Then
        Page<Review> page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 0, 2, "score,asc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getContent().size()).isEqualTo(2);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(3));
        assertThat(page.getContent().get(1)).isEqualTo(reviews.get(1));

        page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 1, 2, "score,asc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getContent().size()).isEqualTo(2);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(2));
        assertThat(page.getContent().get(1)).isEqualTo(reviews.get(0));

        page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 2, 2, "score,asc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(2);
        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(4));
    }

    @DisplayName("사용자 후기 조회시 페이징 후 정렬 되어 출력된다.")
    @Test
    void findByUserTest4() {

        // Given
        User user = new User("", "", "roome.dev@gmail.com");

        List<Review> reviews = List.of(new Review(user, 5.0, "제로월드 홍대점", "층간 소음"),
                new Review(user, 4.5, "티켓 투 이스케이프", "갤럭시 익스프레스"),
                new Review(user, 4.5, "오아시스", "배드 타임 (BÆD TIME)"),
                new Review(user, 4.0, "제로월드 홍대점", "ALIVE"),
                new Review(user, 5.0, "지구별 방탈출 홍대라스트시티점", "섀도우"));

        userRepository.save(user);
        reviewRepository.saveAll(reviews);

        // When & Then
        Page<Review> page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 0, 2, "score,desc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getContent().size()).isEqualTo(2);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(0));
        assertThat(page.getContent().get(1)).isEqualTo(reviews.get(4));

        page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 1, 2, "score,desc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(1);
        assertThat(page.getContent().size()).isEqualTo(2);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(1));
        assertThat(page.getContent().get(1)).isEqualTo(reviews.get(2));

        page = reviewQueryRepository.findByUser(user, ReviewState.DRAFT.getValue(), 2, 2, "score,desc");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(2);
        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(reviews.get(3));
    }
}
