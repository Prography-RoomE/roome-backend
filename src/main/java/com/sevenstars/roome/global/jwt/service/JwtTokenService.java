package com.sevenstars.roome.global.jwt.service;

import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.global.auth.request.TokenRequest;
import com.sevenstars.roome.global.auth.response.TokenResponse;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import com.sevenstars.roome.global.jwt.entity.RefreshToken;
import com.sevenstars.roome.global.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.sevenstars.roome.global.common.response.Result.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class JwtTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public TokenResponse issue(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        String token = tokenProvider.createToken(userId);
        String refreshToken = tokenProvider.createRefreshToken(userId);

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUser(user);
        if (optionalRefreshToken.isEmpty()) {
            refreshTokenRepository.save(new RefreshToken(user, refreshToken));
        } else {
            optionalRefreshToken.get().update(refreshToken);
        }

        return new TokenResponse(token, refreshToken);
    }

    @Transactional
    public TokenResponse getToken(TokenRequest request) {

        String token = tokenProvider.resolveToken(request.getRefreshToken());
        Claims claims = tokenProvider.verifyRefreshToken(token);
        Long userId = Long.valueOf(claims.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new CustomClientErrorException(REFRESH_TOKEN_NOT_FOUND));

        if (!token.equals(refreshToken.getToken())) {
            throw new CustomClientErrorException(INVALID_TOKEN);
        }

        String newToken = tokenProvider.createToken(userId);
        String newRefreshToken = tokenProvider.createRefreshToken(userId);

        refreshToken.update(newRefreshToken);

        return new TokenResponse(newToken, newRefreshToken);
    }

    @Transactional
    public void delete(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomClientErrorException(USER_NOT_FOUND));

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);
    }
}
