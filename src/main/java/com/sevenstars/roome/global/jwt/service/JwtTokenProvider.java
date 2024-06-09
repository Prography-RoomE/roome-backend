package com.sevenstars.roome.global.jwt.service;

import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import com.sevenstars.roome.global.jwt.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import static com.sevenstars.roome.global.common.response.Result.EXPIRED_TOKEN;
import static com.sevenstars.roome.global.common.response.Result.INVALID_TOKEN;


@Component
public class JwtTokenProvider {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_TYPE_CLAIM_NAME = "token_type";
    private static final String TOKEN_TYPE_CLAIM_VALUE_ACCESS_TOKEN = "access_token";
    private static final String TOKEN_TYPE_CLAIM_VALUE_REFRESH_TOKEN = "refresh_token";
    private final JwtProperties properties;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final SecretKey key;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        tokenValidityInMilliseconds = properties.getTokenValidityInSeconds() * 1000;
        refreshTokenValidityInMilliseconds = properties.getRefreshTokenValidityInSeconds() * 1000;
        byte[] keyBytes = Base64.getUrlDecoder().decode(properties.getSecret());
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Long userId) {

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
                .issuer(properties.getIssuerUri())
                .issuedAt(now)
                .expiration(expirationDate)
                .subject(userId.toString())
                .claim(TOKEN_TYPE_CLAIM_NAME, TOKEN_TYPE_CLAIM_VALUE_ACCESS_TOKEN)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId) {

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .issuer(properties.getIssuerUri())
                .issuedAt(now)
                .expiration(expirationDate)
                .subject(userId.toString())
                .claim(TOKEN_TYPE_CLAIM_NAME, TOKEN_TYPE_CLAIM_VALUE_REFRESH_TOKEN)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, "", Collections.emptyList());
    }

    public Claims verifyAccessToken(String token) {
        token = resolveToken(token);
        Claims claims = verifyTokenSignature(token);
        verifyIss(claims);
        verifyAccessTokenType(claims);
        return claims;
    }

    public Claims verifyRefreshToken(String token) {
        token = resolveToken(token);
        Claims claims = verifyTokenSignature(token);
        verifyIss(claims);
        verifyRefreshTokenType(claims);
        return claims;
    }

    public String resolveToken(String token) {

        if (!StringUtils.hasText(token)) {
            throw new CustomClientErrorException(INVALID_TOKEN);
        }

        if (token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }

        return token;
    }

    private Claims verifyTokenSignature(String token) {

        try {
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (SignatureException | MalformedJwtException exception) {
            throw new CustomClientErrorException(INVALID_TOKEN);
        } catch (ExpiredJwtException exception) {
            throw new CustomClientErrorException(EXPIRED_TOKEN);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }


    private void verifyIss(Claims claims) {
        String iss = (String) claims.get("iss");
        if (!iss.contains(properties.getIssuerUri()) && !properties.getIssuerUri().contains(iss)) {
            throw new CustomClientErrorException(INVALID_TOKEN);
        }
    }

    private void verifyAccessTokenType(Claims claims) {
        String tokenType = (String) claims.get(TOKEN_TYPE_CLAIM_NAME);
        if (!TOKEN_TYPE_CLAIM_VALUE_ACCESS_TOKEN.equals(tokenType)) {
            throw new CustomClientErrorException(INVALID_TOKEN);
        }
    }

    private void verifyRefreshTokenType(Claims claims) {
        String tokenType = (String) claims.get(TOKEN_TYPE_CLAIM_NAME);
        if (!TOKEN_TYPE_CLAIM_VALUE_REFRESH_TOKEN.equals(tokenType)) {
            throw new CustomClientErrorException(INVALID_TOKEN);
        }
    }
}
