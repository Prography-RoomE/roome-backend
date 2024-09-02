package com.sevenstars.roome.domain.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NicknameRequest {
    @NotNull
    private String nickname;
}
