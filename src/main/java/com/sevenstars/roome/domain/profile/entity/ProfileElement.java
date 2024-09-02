package com.sevenstars.roome.domain.profile.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class ProfileElement {


    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "element_id")
    private Element element;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private ElementType type;

    public ProfileElement(Profile profile, Element element, ElementType type) {
        this.profile = profile;
        this.element = element;
        this.type = type;
    }

    public void clear() {
        this.element = null;
    }

    public void update(Element element) {
        this.element = element;
    }
}
