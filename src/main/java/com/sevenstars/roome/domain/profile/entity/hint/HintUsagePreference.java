package com.sevenstars.roome.domain.profile.entity.hint;

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
public class HintUsagePreference {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private String description;

    private Boolean isDeleted;

    private Integer priority;

    public HintUsagePreference(String title, String description, Integer priority) {
        this.title = title;
        this.description = description;
        this.isDeleted = false;
        this.priority = priority;
    }
}
