package com.sevenstars.roome.global.common.exception;

import lombok.Getter;

@Getter
public class CustomServerErrorException extends RuntimeException {
    public CustomServerErrorException(String message) {
        super(message);
    }
}
