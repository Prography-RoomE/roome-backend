package com.sevenstars.roome.global.crawler.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrawlingRequest {
    @NotNull
    private String secret;
}
