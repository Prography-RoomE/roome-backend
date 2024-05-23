package com.sevenstars.roome.domain.profile.entity.color;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Color {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private ColorMode mode;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private ColorShape shape;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private ColorDirection direction;

    private String startColor;

    private String endColor;

    private Boolean isDeleted;

    private Integer priority;

    public Color(String title,
                 ColorMode mode,
                 ColorShape shape,
                 ColorDirection direction,
                 String startColor,
                 String endColor,
                 Integer priority) {
        this.title = title;
        this.mode = mode;
        this.shape = shape;
        this.direction = direction;
        this.startColor = startColor;
        this.endColor = endColor;
        this.isDeleted = false;
        this.priority = priority;
    }
}
