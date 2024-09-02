package com.sevenstars.roome.global.jwt.repository;

import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.global.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser(User user);
}
