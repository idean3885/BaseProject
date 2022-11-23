package com.dykim.base.hello.v1.controller.advice.exception;

public class HelloNotFoundException extends RuntimeException {

    public HelloNotFoundException(String msg) {
        super(msg);
    }

}
