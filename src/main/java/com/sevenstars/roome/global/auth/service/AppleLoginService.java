package com.sevenstars.roome.global.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevenstars.roome.domain.user.service.UserService;
import com.sevenstars.roome.global.auth.config.AppleProperties;
import com.sevenstars.roome.global.auth.config.OAuth2ProviderProperties;
import com.sevenstars.roome.global.auth.entity.OAuth2Provider;
import com.sevenstars.roome.global.auth.entity.OAuth2ProviderToken;
import com.sevenstars.roome.global.auth.response.OAuth2TokenResponse;
import com.sevenstars.roome.global.common.exception.CustomServerErrorException;
import com.sevenstars.roome.global.jwt.service.JwtTokenService;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import static com.sevenstars.roome.global.auth.entity.OAuth2Provider.APPLE;
import static com.sevenstars.roome.global.common.response.Result.PROVIDER_INVALID_RESPONSE;

@Slf4j
@Service
public class AppleLoginService extends AbstractLoginService implements OAuth2LoginService {

    private final AppleProperties properties;
    private final RestTemplate restTemplate;
    private final PrivateKey privateKey;
    private final long tokenValidityInMilliseconds;

    public AppleLoginService(RestTemplate restTemplate,
                             ObjectMapper objectMapper,
                             AppleProperties properties,
                             UserService userService,
                             JwtTokenService tokenService) {
        super(restTemplate, objectMapper, userService, tokenService);
        this.restTemplate = restTemplate;
        this.properties = properties;
        this.privateKey = getPrivateKey();
        this.tokenValidityInMilliseconds = properties.getTokenValidityInSeconds() * 1000;
    }

    @Override
    public boolean supports(OAuth2Provider provider) {
        return getProvider().equals(provider);
    }

    @Override
    protected OAuth2Provider getProvider() {
        return APPLE;
    }

    @Override
    protected OAuth2ProviderProperties getProperties() {
        return properties;
    }

    @Override
    protected void revokeToken(String code) {

        String tokenRevokeUri = properties.getTokenRevokeUri();
        String clientId = properties.getClientId();
        String clientSecret = getClientSecret();
        String accessToken = getToken(code).getAccessToken();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("token", accessToken);
        params.add("token_type_hint", "access_token");
        restTemplate.postForObject(tokenRevokeUri, params, String.class);
    }

    @Override
    protected OAuth2ProviderToken getToken(String code) {

        String tokenUri = properties.getTokenUri();
        String clientId = properties.getClientId();
        String clientSecret = getClientSecret();
        String grantType = getProperties().getGrantType();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("grant_type", grantType);

        OAuth2TokenResponse response = restTemplate.postForObject(tokenUri, params, OAuth2TokenResponse.class);

        if (response == null) {
            throw new CustomServerErrorException(PROVIDER_INVALID_RESPONSE);
        }

        return new OAuth2ProviderToken(response.getAccessToken(), response.getIdToken());
    }

    public String getClientSecret() {

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + tokenValidityInMilliseconds);

        String clientId = properties.getClientId();
        String keyId = properties.getKeyId();
        String teamId = properties.getTeamId();

        return Jwts.builder()
                .header()
                .keyId(keyId)
                .and()
                .issuer(teamId)
                .issuedAt(now)
                .expiration(expirationDate)
                .audience().add(properties.getIssuerUri())
                .and()
                .subject(clientId)
                .signWith(privateKey, Jwts.SIG.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {

        try {
            String keyFileName = "AuthKey_" + properties.getKeyId() + ".p8";
            ClassPathResource resource = new ClassPathResource(keyFileName);
            Path path = Paths.get(resource.getURI());
            String content = Files.readString(path);

            content = content.replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decodedKey = Base64.getDecoder().decode(content);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory kf = KeyFactory.getInstance("EC");

            return kf.generatePrivate(privateKeySpec);

        } catch (Exception exception) {
            log.error("Get apple private key failed", exception);
            throw new RuntimeException(exception);
        }
    }
}
