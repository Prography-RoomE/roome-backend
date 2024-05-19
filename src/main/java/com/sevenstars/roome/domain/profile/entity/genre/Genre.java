package com.sevenstars.roome.domain.profile.entity.genre;

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
public class Genre {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private Boolean isDeleted;

    private Integer priority;

    public Genre(String title, Integer priority) {
        this.title = title;
        this.isDeleted = false;
        this.priority = priority;
    }
}
