package com.sevenstars.roome.global.common.exception;

import com.sevenstars.roome.global.common.response.Result;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomServerErrorException extends RuntimeException {
    private final Result result;
}
