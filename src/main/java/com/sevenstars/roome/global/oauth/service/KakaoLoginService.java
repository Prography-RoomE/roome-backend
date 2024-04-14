package com.sevenstars.roome.global.oauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevenstars.roome.global.common.exception.CustomServerErrorException;
import com.sevenstars.roome.global.common.response.ExceptionMessage;
import com.sevenstars.roome.global.oauth.entity.KakaoProperties;
import com.sevenstars.roome.global.oauth.entity.OAuth2Provider;
import com.sevenstars.roome.global.oauth.entity.TokenHeader;
import com.sevenstars.roome.global.oauth.request.SignInRequest;
import com.sevenstars.roome.global.oauth.request.WithdrawalRequest;
import com.sevenstars.roome.global.oauth.response.PublicKeyResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoLoginService implements OAuth2LoginService {

    private final KakaoProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private PrivateKey privateKey;

    @Override
    public boolean supports(OAuth2Provider provider) {
        return OAuth2Provider.KAKAO.equals(provider);
    }

    @Override
    public void signIn(SignInRequest request) {

        String code = request.getCode();
        String idToken = request.getIdToken();

        Claims claims = getClaims(idToken);
    }

    @Override
    public void withdrawal(WithdrawalRequest request) {

    }

    public Claims getClaims(String identityToken) {
        Claims claims = verifyIdentityTokenSignature(identityToken);
        verifyClaims(claims);
        return claims;
    }

    private Claims verifyIdentityTokenSignature(String identityToken) {

        try {

            PublicKeyResponse response = fetchPublicKey();

            String headerString = identityToken.substring(0, identityToken.indexOf("."));
            TokenHeader header = objectMapper.readValue(Base64.getUrlDecoder().decode(headerString), TokenHeader.class);

            PublicKeyResponse.Key key = response.getKey(header.getKid(), header.getAlg())
                    .orElseThrow(() -> new CustomServerErrorException(ExceptionMessage.PUBLIC_KEY_NOT_FOUND.getMessage()));

            byte[] nBytes = Base64.getUrlDecoder().decode(key.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(key.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(key.getKty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            return Jwts.parser().verifyWith(publicKey).build()
                    .parseSignedClaims(identityToken)
                    .getPayload();

        } catch (SignatureException | MalformedJwtException exception) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_TOKEN.getMessage());
        } catch (ExpiredJwtException exception) {
            throw new IllegalArgumentException(ExceptionMessage.EXPIRED_TOKEN.getMessage());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void verifyClaims(Claims claims) {
        verifyIss(claims);
        verifyAud(claims);
    }

    private void verifyIss(Claims claims) {
        String iss = (String) claims.get("iss");
        if (!iss.contains(properties.getIssuerUri()) && !properties.getIssuerUri().contains(iss)) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_TOKEN.getMessage());
        }
    }

    private void verifyAud(Claims claims) {
        Set<String> auds = (Set) claims.get("aud");

        for (String aud : auds) {
            if (aud.equals(properties.getClientId())) {
                return;
            }
        }

        throw new IllegalArgumentException(ExceptionMessage.INVALID_TOKEN.getMessage());
    }

    private PublicKeyResponse fetchPublicKey() {
        return restTemplate.getForObject(properties.getPublicKeyUri(), PublicKeyResponse.class);
    }
}
