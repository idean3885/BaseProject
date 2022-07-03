package com.dykim.base.hello.v1.controller.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResult<T> {

    private String name;

    private final T data;

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(
                data.getClass().getSimpleName(),
                data
        );
    }

    public static ApiResult<String> error(Exception exception) {
        return new ApiResult<>(
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
    }

}
