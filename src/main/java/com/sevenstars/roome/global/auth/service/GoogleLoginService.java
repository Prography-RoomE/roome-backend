package com.sevenstars.roome.global.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevenstars.roome.domain.user.service.UserService;
import com.sevenstars.roome.global.auth.config.GoogleProperties;
import com.sevenstars.roome.global.auth.config.OAuth2ProviderProperties;
import com.sevenstars.roome.global.auth.entity.OAuth2Provider;
import com.sevenstars.roome.global.jwt.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.sevenstars.roome.global.auth.entity.OAuth2Provider.GOOGLE;

@Slf4j
@Service
public class GoogleLoginService extends AbstractLoginService implements OAuth2LoginService {

    private final GoogleProperties properties;
    private final RestTemplate restTemplate;

    public GoogleLoginService(RestTemplate restTemplate,
                              ObjectMapper objectMapper,
                              GoogleProperties properties,
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
        return GOOGLE;
    }

    @Override
    protected OAuth2ProviderProperties getProperties() {
        return properties;
    }

    public void revokeToken(String code) {
        String accessToken = getToken(code).getAccessToken();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("token", accessToken);
        restTemplate.postForObject(properties.getTokenRevokeUri(), params, String.class);
    }
}
