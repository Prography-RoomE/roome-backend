package com.sevenstars.roome.global.version.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VersionRequest {
    @NotNull
    private String secret;
    @NotNull
    private String version;
}
