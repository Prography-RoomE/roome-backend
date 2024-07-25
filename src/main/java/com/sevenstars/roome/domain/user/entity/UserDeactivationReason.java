package com.sevenstars.roome.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class UserDeactivationReason {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String reason;

    private String content;

    public UserDeactivationReason(String reason, String content) {
        this.reason = StringUtils.hasText(reason) ? reason : "";
        this.content = StringUtils.hasText(content) ? content : "";
    }
}
