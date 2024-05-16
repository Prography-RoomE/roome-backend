package com.sevenstars.roome.domain.common.entity;

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
public class ForbiddenWord {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String word;

    public ForbiddenWord(String word) {
        this.word = word;
    }
}
