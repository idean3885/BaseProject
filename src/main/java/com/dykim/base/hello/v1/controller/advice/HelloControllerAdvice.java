package com.dykim.base.hello.v1.controller.advice;

import com.dykim.base.hello.v1.controller.HelloController;
import com.dykim.base.hello.v1.controller.advice.exception.HelloException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.dykim.base.hello.v1.controller.dto.ApiResult.error;

@RestControllerAdvice(assignableTypes = HelloController.class)
public class HelloControllerAdvice {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(HelloException.class)
    public ResponseEntity<?> handleHelloException(HelloException e) {
        return new ResponseEntity<>(error(e), HttpStatus.SERVICE_UNAVAILABLE);
    }

}
