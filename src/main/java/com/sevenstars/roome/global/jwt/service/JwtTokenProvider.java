package com.sevenstars.roome.global.jwt.service;

import com.sevenstars.roome.global.jwt.config.AuthProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;

import static com.sevenstars.roome.global.common.response.ExceptionMessage.EXPIRED_TOKEN;
import static com.sevenstars.roome.global.common.response.ExceptionMessage.INVALID_TOKEN;

@Component
public class JwtTokenProvider {

    private final AuthProperties properties;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final SecretKey key;

    public JwtTokenProvider(AuthProperties properties) {
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
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, "", Collections.emptyList());
    }

    public Claims getClaims(String token) {
        Claims claims = verifyTokenSignature(token);
        verifyClaims(claims);
        return claims;
    }

    private Claims verifyTokenSignature(String token) {

        try {
            return Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (SignatureException | MalformedJwtException exception) {
            throw new IllegalStateException(INVALID_TOKEN.getMessage());
        } catch (ExpiredJwtException exception) {
            throw new IllegalStateException(EXPIRED_TOKEN.getMessage());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void verifyClaims(Claims claims) {
        verifyIss(claims);
    }

    private void verifyIss(Claims claims) {
        String iss = (String) claims.get("iss");
        if (!iss.contains(properties.getIssuerUri()) && !properties.getIssuerUri().contains(iss)) {
            throw new IllegalStateException(INVALID_TOKEN.getMessage());
        }
    }
}
