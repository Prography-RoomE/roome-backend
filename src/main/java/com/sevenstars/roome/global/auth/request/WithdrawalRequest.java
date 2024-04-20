package com.sevenstars.roome.global.auth.request;

import lombok.Data;

@Data
public class WithdrawalRequest {
    private String provider;
    private String code;
}
