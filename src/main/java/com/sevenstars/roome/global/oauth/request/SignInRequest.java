package com.sevenstars.roome.global.oauth.request;

import lombok.Data;

@Data
public class SignInRequest {
    private String provider;
    private String code;
    private String idToken;
}
