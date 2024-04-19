package com.sevenstars.roome.global.oauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevenstars.roome.global.common.exception.CustomServerErrorException;
import com.sevenstars.roome.global.common.response.ExceptionMessage;
import com.sevenstars.roome.global.oauth.config.OAuth2ProviderProperties;
import com.sevenstars.roome.global.oauth.response.Key;
import com.sevenstars.roome.global.oauth.entity.OAuth2Provider;
import com.sevenstars.roome.global.oauth.entity.OAuth2ProviderToken;
import com.sevenstars.roome.global.oauth.entity.TokenHeader;
import com.sevenstars.roome.global.oauth.request.SignInRequest;
import com.sevenstars.roome.global.oauth.request.WithdrawalRequest;
import com.sevenstars.roome.global.oauth.response.PublicKeyResponse;
import com.sevenstars.roome.global.oauth.response.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

import static com.sevenstars.roome.global.common.response.ExceptionMessage.PROVIDER_INVALID_RESPONSE;
import static com.sevenstars.roome.global.common.response.ExceptionMessage.PUBLIC_KEY_UPDATE_FAIL;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractLoginService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private List<Key> keys = Collections.emptyList();

    public void signIn(SignInRequest request) {

        String code = request.getCode();
        String idToken = request.getIdToken();

        if (!StringUtils.hasText(idToken)) {
            if (!StringUtils.hasText(code)) {
                throw new IllegalArgumentException();
            }
            idToken = getToken(code).getIdToken();
        }

        Claims claims = getClaims(idToken);

        // TODO: User Service
    }

    public void withdrawal(WithdrawalRequest request) {

        String code = request.getCode();

        if (StringUtils.hasText(code)) {
            revokeToken(code);
        }

        // TODO: User Service
    }

    protected OAuth2ProviderToken getToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", getProperties().getGrantType());
        params.add("code", code);
        params.add("redirect_uri", getProperties().getRedirectUri());
        params.add("client_id", getProperties().getClientId());
        params.add("client_secret", getProperties().getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        TokenResponse response = restTemplate.postForObject(getProperties().getTokenUri(), request, TokenResponse.class);

        if (response == null) {
            throw new CustomServerErrorException(PROVIDER_INVALID_RESPONSE.getMessage());
        }

        return new OAuth2ProviderToken(response.getAccessToken(), response.getIdToken());
    }

    protected Claims getClaims(String identityToken) {
        Claims claims = verifyIdentityTokenSignature(identityToken);
        verifyClaims(claims);
        return claims;
    }

    protected Claims verifyIdentityTokenSignature(String identityToken) {

        try {
            String headerString = identityToken.substring(0, identityToken.indexOf("."));
            TokenHeader header = objectMapper.readValue(Base64.getUrlDecoder().decode(headerString), TokenHeader.class);

            Key key = getKey(header.getKid(), header.getAlg())
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

    protected void verifyClaims(Claims claims) {
        verifyIss(claims);
        verifyAud(claims);
    }

    protected void verifyIss(Claims claims) {
        String iss = (String) claims.get("iss");
        if (!iss.contains(getProperties().getIssuerUri()) && !getProperties().getIssuerUri().contains(iss)) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_TOKEN.getMessage());
        }
    }

    protected void verifyAud(Claims claims) {
        Set<String> auds = (Set) claims.get("aud");

        for (String aud : auds) {
            if (aud.equals(getProperties().getClientId())) {
                return;
            }
        }

        throw new IllegalArgumentException(ExceptionMessage.INVALID_TOKEN.getMessage());
    }

    protected void updateKey() {
        PublicKeyResponse response = fetchPublicKey();
        if (response == null || response.getKeys().isEmpty()) {
            throw new IllegalStateException(PUBLIC_KEY_UPDATE_FAIL.getMessage());
        }
        keys = response.getKeys();
        log.info("{}={}", getProvider().getName(), keys);
    }

    protected Optional<Key> getKey(String kid, String alg) {
        return this.keys.stream()
                .filter(key -> key.getKid().equals(kid) && key.getAlg().equals(alg))
                .findFirst();
    }

    protected PublicKeyResponse fetchPublicKey() {
        return restTemplate.getForObject(getProperties().getPublicKeyUri(), PublicKeyResponse.class);
    }

    protected abstract OAuth2Provider getProvider();

    protected abstract OAuth2ProviderProperties getProperties();

    protected abstract void revokeToken(String code);
}
