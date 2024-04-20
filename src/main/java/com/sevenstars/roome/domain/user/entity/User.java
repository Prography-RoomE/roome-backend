package com.sevenstars.roome.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String serviceId;

    private String serviceUserId;

    private String email;

    private String nickname;

    private Boolean withdrawal;

    public User(String serviceId, String serviceUserId, String email) {
        this.serviceId = serviceId;
        this.serviceUserId = serviceUserId;
        this.email = email;
        this.nickname = "";
        this.withdrawal = false;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void update(String email) {
        this.email = email;
    }

    public void withdraw() {
        this.withdrawal = true;
    }
}
