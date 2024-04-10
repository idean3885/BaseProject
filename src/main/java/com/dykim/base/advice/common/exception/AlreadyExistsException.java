package com.dykim.base.advice.common.exception;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String msg) {
        super(msg);
    }
}
