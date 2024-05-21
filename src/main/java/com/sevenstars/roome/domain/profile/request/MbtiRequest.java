package com.sevenstars.roome.domain.profile.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MbtiRequest {
    @NotNull
    private String mbti;
}
