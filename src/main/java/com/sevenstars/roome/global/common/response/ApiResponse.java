package com.sevenstars.roome.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.sevenstars.roome.global.common.response.Result.*;

@Getter
public class ApiResponse<T> {
    private final Integer code;
    private final String message;
    @JsonInclude(NON_NULL)
    private final T data;

    public static <T> ApiResponse<T> success(T data) {
        return result(SUCCESS, data);
    }

    public static <T> ApiResponse<T> success() {
        return result(SUCCESS);
    }

    public static <T> ApiResponse<T> fail() {
        return result(FAIL);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return ApiResponse.<T>builder()
                .code(Result.FAIL.getCode())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error() {
        return result(ERROR);
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code(Result.ERROR.getCode())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> unauthorized() {
        return result(UNAUTHORIZED);
    }

    public static <T> ApiResponse<T> forbidden() {
        return result(FORBIDDEN);
    }

    public static <T> ApiResponse<T> result(Result result) {
        return ApiResponse.<T>builder()
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> result(Result result, T data) {
        return ApiResponse.<T>builder()
                .result(result)
                .data(data)
                .build();
    }

    @Builder
    public ApiResponse(Result result, Integer code, String message, T data) {
        this.code = (result == null) ? code : result.getCode();
        this.message = (result == null) ? message : result.getMessage();
        this.data = data;
    }
}
