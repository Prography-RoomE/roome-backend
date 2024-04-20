package com.sevenstars.roome.global.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevenstars.roome.domain.user.service.UserService;
import com.sevenstars.roome.global.auth.config.KakaoProperties;
import com.sevenstars.roome.global.auth.config.OAuth2ProviderProperties;
import com.sevenstars.roome.global.auth.entity.OAuth2Provider;
import com.sevenstars.roome.global.jwt.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.sevenstars.roome.global.auth.entity.OAuth2Provider.KAKAO;

@Slf4j
@Service
public class KakaoLoginService extends AbstractLoginService implements OAuth2LoginService {

    private final KakaoProperties properties;
    private final RestTemplate restTemplate;

    public KakaoLoginService(RestTemplate restTemplate,
                             ObjectMapper objectMapper,
                             KakaoProperties properties,
                             UserService userService,
                             JwtTokenService tokenService) {
        super(restTemplate, objectMapper, userService, tokenService);
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public boolean supports(OAuth2Provider provider) {
        return getProvider().equals(provider);
    }

    @Override
    protected OAuth2Provider getProvider() {
        return KAKAO;
    }

    @Override
    protected OAuth2ProviderProperties getProperties() {
        return properties;
    }

    public void revokeToken(String code) {
        String accessToken = getToken(code).getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Object> entity = new HttpEntity<>("", headers);
        restTemplate.exchange(properties.getTokenRevokeUri(), HttpMethod.POST, entity, String.class);
    }
}
