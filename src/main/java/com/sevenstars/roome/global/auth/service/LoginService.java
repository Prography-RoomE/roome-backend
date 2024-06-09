package com.sevenstars.roome.global.auth.service;

import com.sevenstars.roome.global.auth.entity.OAuth2Provider;
import com.sevenstars.roome.global.auth.request.SignInRequest;
import com.sevenstars.roome.global.auth.request.WithdrawalRequest;
import com.sevenstars.roome.global.auth.response.TokenResponse;
import com.sevenstars.roome.global.common.exception.CustomClientErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static com.sevenstars.roome.global.common.response.Result.PROVIDER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final List<OAuth2LoginService> oAuth2LoginServices;

    public TokenResponse signIn(SignInRequest request) {

        OAuth2Provider oAuth2Provider = Arrays.stream(OAuth2Provider.values())
                .filter(provider -> provider.getName().equals(request.getProvider()))
                .findAny()
                .orElseThrow(() -> new CustomClientErrorException(PROVIDER_NOT_FOUND));

        OAuth2LoginService oAuth2LoginService = oAuth2LoginServices.stream()
                .filter(service -> service.supports(oAuth2Provider))
                .findAny()
                .orElseThrow(() -> new CustomClientErrorException(PROVIDER_NOT_FOUND));

        return oAuth2LoginService.signIn(request);
    }

    public void withdraw(Long id, WithdrawalRequest request) {

        OAuth2Provider oAuth2Provider = Arrays.stream(OAuth2Provider.values())
                .filter(provider -> provider.getName().equals(request.getProvider()))
                .findAny()
                .orElseThrow(() -> new CustomClientErrorException(PROVIDER_NOT_FOUND));

        OAuth2LoginService oAuth2LoginService = oAuth2LoginServices.stream()
                .filter(service -> service.supports(oAuth2Provider))
                .findAny()
                .orElseThrow(() -> new CustomClientErrorException(PROVIDER_NOT_FOUND));

        oAuth2LoginService.withdraw(id, request);
    }
}
