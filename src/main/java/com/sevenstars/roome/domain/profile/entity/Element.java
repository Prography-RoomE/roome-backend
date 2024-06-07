package com.sevenstars.roome.domain.profile.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Element {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private ElementType type;

    private String title;

    private String subTitle;

    private String description;

    private String emoji;

    private String imageUrl;

    private Boolean isDeleted;

    private Integer priority;

    public Element(ElementType type,
                   String title,
                   String subTitle,
                   String description,
                   String emoji,
                   String imageUrl,
                   Integer priority) {
        this.type = type;
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.emoji = emoji;
        this.imageUrl = imageUrl;
        this.isDeleted = false;
        this.priority = priority;
    }
}
