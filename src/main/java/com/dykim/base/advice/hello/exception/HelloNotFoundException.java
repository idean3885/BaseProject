package com.dykim.base.advice.hello.exception;

public class HelloNotFoundException extends RuntimeException {

    public HelloNotFoundException(String msg) {
        super(msg);
    }

}
