package com.dykim.base.hello.v1.config.advice.exception;

public class HandlerDebounceException extends RuntimeException {

    public HandlerDebounceException(String msg) {
        super(msg);
    }

}
