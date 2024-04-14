package com.sevenstars.roome.global.oauth.request;

import lombok.Data;

@Data
public class WithdrawalRequest {
    private String provider;
    private String code;
}
