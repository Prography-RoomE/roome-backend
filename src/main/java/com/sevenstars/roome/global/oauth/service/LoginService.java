package com.sevenstars.roome.global.oauth.service;

import com.sevenstars.roome.global.oauth.entity.OAuth2Provider;
import com.sevenstars.roome.global.oauth.request.SignInRequest;
import com.sevenstars.roome.global.oauth.request.WithdrawalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.sevenstars.roome.global.common.response.ExceptionMessage.PROVIDER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final List<OAuth2LoginService> oAuth2LoginServices;

    public void signIn(SignInRequest request) {

        OAuth2Provider oAuth2Provider = Arrays.stream(OAuth2Provider.values())
                .filter(provider -> provider.getName().equals(request.getProvider()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(PROVIDER_NOT_FOUND.getMessage()));

        OAuth2LoginService oAuth2LoginService = oAuth2LoginServices.stream()
                .filter(service -> service.supports(oAuth2Provider))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(PROVIDER_NOT_FOUND.getMessage()));

        oAuth2LoginService.signIn(request);
    }

    public void withdrawal(WithdrawalRequest request) {

        OAuth2Provider oAuth2Provider = Arrays.stream(OAuth2Provider.values())
                .filter(provider -> provider.getName().equals(request.getProvider()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(PROVIDER_NOT_FOUND.getMessage()));

        OAuth2LoginService oAuth2LoginService = oAuth2LoginServices.stream()
                .filter(service -> service.supports(oAuth2Provider))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(PROVIDER_NOT_FOUND.getMessage()));

        oAuth2LoginService.withdrawal(request);
    }
}
