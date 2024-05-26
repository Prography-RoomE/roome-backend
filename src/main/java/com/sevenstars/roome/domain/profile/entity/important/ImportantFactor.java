package com.sevenstars.roome.domain.profile.entity.important;

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
public class ImportantFactor {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private String emoji;

    private String imageUrl;

    private Boolean isDeleted;

    private Integer priority;

    public ImportantFactor(String title, String emoji, String imageUrl, Integer priority) {
        this.title = title;
        this.emoji = emoji;
        this.imageUrl = imageUrl;
        this.isDeleted = false;
        this.priority = priority;
    }
}
