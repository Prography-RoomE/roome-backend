package com.sevenstars.roome.global.auth.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawalRequest {
    @NotNull
    private String provider;
    private String code;
}
