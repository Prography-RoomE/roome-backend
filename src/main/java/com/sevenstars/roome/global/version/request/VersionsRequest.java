package com.sevenstars.roome.global.version.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VersionsRequest {
    @NotNull
    private String secret;
    @NotNull
    private String serverVersion;
    @NotNull
    private String aosVersion;
    @NotNull
    private String iosVersion;
}
