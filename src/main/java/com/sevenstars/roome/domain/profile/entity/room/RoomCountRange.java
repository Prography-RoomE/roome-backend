package com.sevenstars.roome.domain.profile.entity.room;

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
public class RoomCountRange {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private Integer minCount;

    private Integer maxCount;

    public RoomCountRange(String title, Integer minCount, Integer maxCount) {
        this.title = title;
        this.minCount = minCount;
        this.maxCount = maxCount;
    }
}
