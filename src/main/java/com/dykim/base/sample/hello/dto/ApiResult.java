package com.dykim.base.sample.hello.dto;

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

    public static ApiResult<String> error(Exception e) {
        return error(e.getClass().getSimpleName(), e.getMessage());
    }

    public static ApiResult<String> error(Exception e, String message) {
        return error(e.getClass().getSimpleName(), message);
    }

    private static <T> ApiResult<T> error(String name, T data) {
        return new ApiResult<>(name, data);
    }

}
