package com.dykim.base.advice.hello.exception;

public class HelloAlreadyExistException extends RuntimeException {

    public HelloAlreadyExistException(String msg) {
        super(msg);
    }
}
