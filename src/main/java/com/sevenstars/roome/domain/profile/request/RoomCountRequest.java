package com.sevenstars.roome.domain.profile.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomCountRequest {
    @NotNull
    private Integer count;
    @NotNull
    private Boolean isPlusEnabled;
}
