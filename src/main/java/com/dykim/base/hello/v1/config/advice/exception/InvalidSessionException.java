package com.dykim.base.hello.v1.config.advice.exception;

public class InvalidSessionException extends RuntimeException {

    public InvalidSessionException(String msg) {
        super(msg);
    }

}
