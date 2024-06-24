package com.sevenstars.roome.global.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevenstars.roome.domain.user.entity.User;
import com.sevenstars.roome.domain.user.request.UserRequest;
import com.sevenstars.roome.domain.user.service.UserService;
import com.sevenstars.roome.global.auth.config.OAuth2ProviderProperties;
import com.sevenstars.roome.global.auth.entity.OAuth2Provider;
import com.sevenstars.roome.global.auth.entity.OAuth2ProviderToken;
import com.sevenstars.roome.global.auth.entity.TokenHeader;
import com.sevenstars.roome.global.auth.request.SignInRequest;
import com.sevenstars.roome.global.auth.request.WithdrawalRequest;
import com.sevenstars.roome.global.auth.response.Key;
import com.sevenstars.roome.global.auth.response.OAuth2TokenResponse;
import com.sevenstars.roome.global.auth.response.PublicKeyResponse;
import com.sevenstars.roome.global.auth.response.TokenResponse;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import com.sevenstars.roome.global.common.exception.CustomServerErrorException;
import com.sevenstars.roome.global.jwt.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;

import static com.sevenstars.roome.global.common.response.Result.*;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractLoginService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final JwtTokenService tokenService;
    private List<Key> keys = Collections.emptyList();

    public TokenResponse signIn(SignInRequest request) {

        String code = request.getCode();
        String idToken = request.getIdToken();

        if (!StringUtils.hasText(idToken)) {
            if (!StringUtils.hasText(code)) {
                throw new CustomClientErrorException(AUTHORIZATION_CODE_AND_ID_TOKEN_EMPTY);
            }
            idToken = getToken(code).getIdToken();
        }

        Claims claims = getClaims(idToken);

        String subject = claims.getSubject();
        String email = (String) claims.get("email");
        User user = userService.saveOrUpdate(new UserRequest(getProvider().getName(), subject, email));
        Long userId = user.getId();

        return tokenService.issue(userId);
    }

    public void withdraw(Long id, WithdrawalRequest request) {

        String code = request.getCode();

        if (StringUtils.hasText(code)) {
            revokeToken(code);
        }

        userService.withdraw(id);
    }

    protected OAuth2ProviderToken getToken(String code) {

        String tokenUri = getProperties().getTokenUri();
        String clientId = getProperties().getClientId();
        String clientSecret = getProperties().getClientSecret();
        String grantType = getProperties().getGrantType();
        String redirectUri = getProperties().getRedirectUri();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("grant_type", grantType);
        params.add("redirect_uri", redirectUri);

        OAuth2TokenResponse response = restTemplate.postForObject(tokenUri, params, OAuth2TokenResponse.class);

        if (response == null) {
            throw new CustomServerErrorException(PROVIDER_INVALID_RESPONSE);
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
                    .orElseThrow(() -> new CustomServerErrorException(PUBLIC_KEY_NOT_FOUND));

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
            throw new CustomClientErrorException(INVALID_TOKEN);
        } catch (ExpiredJwtException exception) {
            throw new CustomClientErrorException(EXPIRED_TOKEN);
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
            throw new CustomClientErrorException(INVALID_TOKEN);
        }
    }

    protected void verifyAud(Claims claims) {
        Set<String> auds = (Set) claims.get("aud");

        for (String aud : auds) {
            if (aud.equals(getProperties().getClientId())) {
                return;
            }
        }

        throw new CustomClientErrorException(INVALID_TOKEN);
    }

    protected void updateKey() {
        PublicKeyResponse response = fetchPublicKey();
        if (response == null || response.getKeys().isEmpty()) {
            throw new CustomClientErrorException(PUBLIC_KEY_UPDATE_FAIL);
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
