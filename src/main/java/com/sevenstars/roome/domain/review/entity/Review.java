package com.sevenstars.roome.domain.review.entity;

import com.sevenstars.roome.domain.common.entity.BaseTimeEntity;
import com.sevenstars.roome.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

import static com.sevenstars.roome.domain.review.entity.ReviewState.DRAFT;
import static com.sevenstars.roome.domain.review.entity.ReviewState.PUBLISHED;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private ReviewState state;

    private Double score;

    private String storeName;

    private String themeName;

    private Boolean spoiler;

    private Boolean isPublic;

    private Boolean success;

    private LocalDate playDate;

    private Integer totalTime;

    private Integer remainingTime;

    private Integer hintCount;

    private Integer participants;

    private String difficultyLevel;

    private String fearLevel;

    private String activityLevel;

    private Double interiorRating;

    private Double directionRating;

    private Double storyRating;

    private String content;

    public Review(User user, Double score, String storeName, String themeName) {
        this.spoiler = true;
        this.isPublic = false;

        this.user = user;
        this.state = DRAFT;

        this.score = Math.round(score * 10.0) / 10.0;
        this.storeName = storeName;
        this.themeName = themeName;

        this.difficultyLevel = "";
        this.fearLevel = "";
        this.activityLevel = "";
        this.content = "";
    }

    public void update(Double score, String storeName, String themeName) {
        this.score = score;
        this.storeName = storeName;
        this.themeName = themeName;
    }

    public void update(Boolean success,
                       LocalDate playDate,
                       Integer totalTime,
                       Integer remainingTime,
                       Integer hintCount,
                       Integer participants,
                       String difficultyLevel,
                       String fearLevel,
                       String activityLevel,
                       Double interiorRating,
                       Double directionRating,
                       Double storyRating,
                       String content,
                       Boolean spoiler,
                       Boolean isPublic) {
        this.success = success;
        this.playDate = playDate;
        this.totalTime = totalTime;
        this.remainingTime = remainingTime;
        this.hintCount = hintCount;
        this.participants = participants;

        if (StringUtils.hasText(difficultyLevel)) {
            this.difficultyLevel = difficultyLevel;
        }
        if (StringUtils.hasText(fearLevel)) {
            this.fearLevel = fearLevel;
        }
        if (StringUtils.hasText(activityLevel)) {
            this.activityLevel = activityLevel;
        }
        if (interiorRating != null) {
            this.interiorRating = Math.round(interiorRating * 10.0) / 10.0;
        }
        if (directionRating != null) {
            this.directionRating = Math.round(directionRating * 10.0) / 10.0;
        }
        if (storyRating != null) {
            this.storyRating = Math.round(storyRating * 10.0) / 10.0;
        }
        if (StringUtils.hasText(content)) {
            this.content = content;
        }
        if (spoiler != null) {
            this.spoiler = spoiler;
        }
        if (isPublic != null) {
            this.isPublic = isPublic;
        }
    }

    public void publish() {
        this.state = PUBLISHED;
    }

    public void deleteUser() {
        this.user = null;
    }
}
