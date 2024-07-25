package com.sevenstars.roome.global.version.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Version {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(255)")
    @Enumerated(STRING)
    private VersionType type;

    private String version;

    public Version(VersionType type, String version) {
        this.type = type;
        this.version = version;
    }

    public void update(String version) {
        this.version = version;
    }
}
