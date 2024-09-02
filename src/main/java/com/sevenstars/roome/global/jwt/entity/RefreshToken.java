package com.sevenstars.roome.global.jwt.entity;

import com.sevenstars.roome.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String token;

    public RefreshToken(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public void update(String token) {
        this.token = token;
    }
}
