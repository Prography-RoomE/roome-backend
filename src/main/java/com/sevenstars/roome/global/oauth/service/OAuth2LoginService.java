package com.sevenstars.roome.global.oauth.service;

import com.sevenstars.roome.global.oauth.entity.OAuth2Provider;
import com.sevenstars.roome.global.oauth.request.SignInRequest;
import com.sevenstars.roome.global.oauth.request.WithdrawalRequest;

public interface OAuth2LoginService {
    boolean supports(OAuth2Provider provider);

    void signIn(SignInRequest request);

    void withdrawal(WithdrawalRequest request);
}
