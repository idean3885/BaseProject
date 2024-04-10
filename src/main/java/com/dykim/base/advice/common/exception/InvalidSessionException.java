package com.dykim.base.advice.common.exception;

public class InvalidSessionException extends RuntimeException {

    public InvalidSessionException(String msg) {
        super(msg);
    }
}
