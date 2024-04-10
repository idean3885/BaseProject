package com.dykim.base.advice.common.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String msg) {
        super(msg);
    }
}
