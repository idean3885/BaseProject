package com.dykim.base.hello.v1.service;

import com.dykim.base.hello.v1.controller.advice.exception.HelloException;
import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String occurException(boolean isOccur) {
        if (isOccur) {
            throw new HelloException("HelloException is occurred by params(isOccur)");
        }

        return "param(isOccur) is false. HelloException is not occurred.";
    }

}
