package com.dykim.base.hello.v1.controller.advice.exception;

public class HelloAuditorAwareException extends RuntimeException {

    public HelloAuditorAwareException(String msg) {
        super(msg);
    }

}
