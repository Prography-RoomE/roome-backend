package com.sevenstars.roome.domain.profile.entity.device;

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
public class DeviceLockPreference {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String emoji;

    private String imageUrl;

    private Boolean isDeleted;

    private Integer priority;

    public DeviceLockPreference(String title, String description, String emoji, String imageUrl, Integer priority) {
        this.title = title;
        this.description = description;
        this.emoji = emoji;
        this.imageUrl = imageUrl;
        this.isDeleted = false;
        this.priority = priority;
    }
}
