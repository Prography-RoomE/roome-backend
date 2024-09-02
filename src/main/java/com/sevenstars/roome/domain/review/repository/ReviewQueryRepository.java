package com.sevenstars.roome.domain.review.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sevenstars.roome.domain.review.entity.Review;
import com.sevenstars.roome.domain.review.entity.ReviewState;
import com.sevenstars.roome.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.sevenstars.roome.domain.review.entity.QReview.review;

@RequiredArgsConstructor
@Repository
public class ReviewQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Review> findByUser(User user,
                                   String state,
                                   Integer page,
                                   Integer size,
                                   String sort) {

        long offset = (long) page * size;
        long limit = size;

        List<Review> content = queryFactory.select(review)
                .from(review)
                .where(review.user.eq(user),
                        equalsState(state))
                .orderBy(sort(sort), defaultSort())
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = queryFactory.select(review.count())
                .from(review)
                .fetchOne();

        if (totalCount == null) {
            totalCount = 0L;
        }

        return new PageImpl<>(content,
                PageRequest.of(page, size),
                totalCount);
    }

    private BooleanExpression equalsState(String state) {
        if (!StringUtils.hasText(state)) {
            return null;
        }

        return Arrays.stream(ReviewState.values())
                .filter(reviewState -> reviewState.getValue().equals(state))
                .findAny().map(review.state::eq).orElse(null);
    }

    private OrderSpecifier<?> sort(String sort) {

        if (!StringUtils.hasText(sort)) {
            return defaultSort();
        }

        return switch (sort) {
            case "score,asc" -> review.score.asc();
            case "score,desc" -> review.score.desc();
            case "storeName,asc" -> review.storeName.asc();
            case "storeName,desc" -> review.storeName.desc();
            case "themeName,asc" -> review.themeName.asc();
            case "themeName,desc" -> review.themeName.desc();
            case "id,asc" -> review.id.asc();
            case "id,desc" -> review.id.desc();
            default -> defaultSort();
        };
    }

    private OrderSpecifier<Long> defaultSort() {
        return review.id.asc();
    }
}
