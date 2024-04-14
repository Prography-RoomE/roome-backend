package com.sevenstars.roome.global.common.exception;

import com.sevenstars.roome.global.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        log.error("[handleException]: ", exception);
        return ApiResponse.error();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomServerErrorException.class)
    public ApiResponse<Void> handleCustomServerErrorException(CustomServerErrorException exception) {
        log.error("[CustomServerErrorException]: ", exception);
        return ApiResponse.error(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("[handleIllegalArgumentException]: {}", exception.getMessage());
        return ApiResponse.fail();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ApiResponse<Void> handleIllegalStateException(IllegalStateException exception) {
        log.error("[handleIllegalStateException]: {}", exception.getMessage());
        return ApiResponse.fail(exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error("[handleMethodArgumentNotValidException]: {}", exception.getMessage());
        return ApiResponse.fail();
    }
}
