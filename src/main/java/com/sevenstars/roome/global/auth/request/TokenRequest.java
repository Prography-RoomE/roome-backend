package com.sevenstars.roome.global.auth.request;

import lombok.Data;

@Data
public class TokenRequest {
    private String refreshToken;
}
