package com.sevenstars.roome.domain.profile.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoomCountRangeRequest {
    @NotNull
    private Integer minCount;
    @NotNull
    private Integer maxCount;
}
