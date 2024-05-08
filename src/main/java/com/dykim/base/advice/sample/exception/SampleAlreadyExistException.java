package com.dykim.base.advice.sample.exception;

public class SampleAlreadyExistException extends RuntimeException {

    public SampleAlreadyExistException(String msg) {
        super(msg);
    }
}
