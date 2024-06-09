package com.sevenstars.roome.domain.profile.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ThemeImportantFactorsRequest {
    @NotNull
    @NotEmpty
    private List<Long> ids;
}
