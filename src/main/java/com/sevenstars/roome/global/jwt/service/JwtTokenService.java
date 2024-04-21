package com.sevenstars.roome.global.jwt.service;

import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.repository.UserRepository;
import com.sevenstars.roome.global.auth.response.TokenResponse;
import com.sevenstars.roome.global.jwt.entity.RefreshToken;
import com.sevenstars.roome.global.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static com.sevenstars.roome.global.common.response.ExceptionMessage.INVALID_ID;
import static com.sevenstars.roome.global.common.response.ExceptionMessage.INVALID_TOKEN;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class JwtTokenService {

    private static final String BEARER_PREFIX = "Bearer ";
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public TokenResponse issue(Long userId) {

        User user = userRepository.findByIdAndWithdrawalFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_ID.getMessage()));

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
    public TokenResponse reissue(Long userId, String authorizationHeader) {

        User user = userRepository.findByIdAndWithdrawalFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_ID.getMessage()));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException(INVALID_TOKEN.getMessage()));

        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalStateException(INVALID_TOKEN.getMessage());
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length());

        if (!token.equals(refreshToken.getToken())) {
            throw new IllegalStateException(INVALID_TOKEN.getMessage());
        }

        String newToken = tokenProvider.createToken(userId);
        String newRefreshToken = tokenProvider.createRefreshToken(userId);

        refreshToken.update(newRefreshToken);

        return new TokenResponse(newToken, newRefreshToken);
    }

    @Transactional
    public void delete(Long userId) {

        User user = userRepository.findByIdAndWithdrawalFalse(userId)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_ID.getMessage()));

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);
    }
}
