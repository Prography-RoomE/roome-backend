package com.sevenstars.roome.domain.review.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Theme {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String area;

    private String storeName;

    private String name;

    private Double score;

    private String difficulty;

    private Integer reviewCount;

    private Boolean isDeleted;

    public Theme(String area,
                 String storeName,
                 String name,
                 Double score,
                 String difficulty,
                 Integer reviewCount) {
        this.area = area;
        this.storeName = storeName;
        this.name = name;
        this.score = score;
        this.difficulty = difficulty;
        this.reviewCount = reviewCount;
        this.isDeleted = false;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getName() {
        return name;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

    public void update(Theme theme) {
        this.area = theme.getArea();
        this.storeName = theme.getStoreName();
        this.name = theme.getName();
        this.score = theme.getScore();
        this.difficulty = theme.getDifficulty();
        this.reviewCount = theme.getReviewCount();
        this.isDeleted = false;
    }
}
