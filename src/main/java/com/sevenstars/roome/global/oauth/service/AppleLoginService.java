package com.sevenstars.roome.global.oauth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevenstars.roome.global.common.exception.CustomServerErrorException;
import com.sevenstars.roome.global.common.response.ExceptionMessage;
import com.sevenstars.roome.global.oauth.entity.AppleProperties;
import com.sevenstars.roome.global.oauth.entity.OAuth2Provider;
import com.sevenstars.roome.global.oauth.entity.TokenHeader;
import com.sevenstars.roome.global.oauth.request.SignInRequest;
import com.sevenstars.roome.global.oauth.request.WithdrawalRequest;
import com.sevenstars.roome.global.oauth.response.AppleTokenResponse;
import com.sevenstars.roome.global.oauth.response.PublicKeyResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppleLoginService implements OAuth2LoginService {

    private final AppleProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        //this.privateKey = getPrivateKey();
    }

    @Override
    public boolean supports(OAuth2Provider provider) {
        return OAuth2Provider.APPLE.equals(provider);
    }

    @Override
    public void signIn(SignInRequest request) {

        String code = request.getCode();
        String idToken = request.getIdToken();

        Claims claims = getClaims(idToken);
        AppleTokenResponse response = generateToken(code);
        log.info("{}", response);


    }

    @Override
    public void withdrawal(WithdrawalRequest request) {

    }

    public Claims getClaims(String identityToken) {
        Claims claims = verifyIdentityTokenSignature(identityToken);
        verifyClaims(claims);
        return claims;
    }

    public AppleTokenResponse generateToken(String authorizationCode) {

        String tokenUri = properties.getTokenUri();
        String clientId = properties.getClientId();
        // TODO
        // kid: Key ID
        // iss: Team ID
        // sub: Client ID
        String clientSecret = this.getClientSecret("", "", clientId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", authorizationCode);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return restTemplate.postForObject(tokenUri, request, AppleTokenResponse.class);
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
        String aud = (String) claims.get("aud");
        if (!aud.equals(properties.getClientId())) {
            throw new IllegalArgumentException(ExceptionMessage.INVALID_TOKEN.getMessage());
        }
    }

    private String getClientSecret(String kid, String iss, String sub) {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .header().add("kid", kid)
                .add("alg", "ES256")
                .and()
                .issuer(iss)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expirationDate)
                .audience()
                .add(properties.getIssuerUri())
                .and()
                .subject(sub)
                .signWith(getPrivateKey(), Jwts.SIG.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {

        try {
            ClassPathResource resource = new ClassPathResource("Apple_Developer_페이지에서_다운.p8");
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

    private PublicKeyResponse fetchPublicKey() {
        return restTemplate.getForObject(properties.getPublicKeyUri(), PublicKeyResponse.class);
    }
}
