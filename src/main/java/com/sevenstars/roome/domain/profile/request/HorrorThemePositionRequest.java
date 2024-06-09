package com.sevenstars.roome.domain.profile.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HorrorThemePositionRequest {
    @NotNull
    private Long id;
}
