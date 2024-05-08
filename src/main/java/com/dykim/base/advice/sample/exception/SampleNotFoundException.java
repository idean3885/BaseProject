package com.dykim.base.advice.sample.exception;

public class SampleNotFoundException extends RuntimeException {

    public SampleNotFoundException(String msg) {
        super(msg);
    }
}
