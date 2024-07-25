package com.sevenstars.roome.global.auth.service;

import com.sevenstars.roome.global.auth.entity.OAuth2Provider;
import com.sevenstars.roome.global.auth.request.DeactivateRequest;
import com.sevenstars.roome.global.auth.request.SignInRequest;
import com.sevenstars.roome.global.auth.response.TokenResponse;

public interface OAuth2LoginService {
    boolean supports(OAuth2Provider provider);

    TokenResponse signIn(SignInRequest request);

    void deactivate(Long id, DeactivateRequest request);
}
