package com.dykim.base.hello.v1.controller.advice.exception;

public class HelloAlreadyExistException extends RuntimeException {

    public HelloAlreadyExistException(String msg) {
        super(msg);
    }

}
