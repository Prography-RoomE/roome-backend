package com.sevenstars.roome.domain.profile.entity.position;

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
public class HorrorThemePosition {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String emoji;

    private String imageUrl;

    private Boolean isDeleted;

    private Integer priority;

    public HorrorThemePosition(String title, String description, String emoji, String imageUrl, Integer priority) {
        this.title = title;
        this.description = description;
        this.emoji = emoji;
        this.imageUrl = imageUrl;
        this.isDeleted = false;
        this.priority = priority;
    }
}
