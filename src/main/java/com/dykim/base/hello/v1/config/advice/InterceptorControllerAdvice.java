package com.dykim.base.hello.v1.config.advice;

import com.dykim.base.hello.v1.config.advice.exception.HandlerDebounceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.dykim.base.hello.v1.controller.dto.ApiResult.error;

@RestControllerAdvice
public class InterceptorControllerAdvice {

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(HandlerDebounceException.class)
    public ResponseEntity<?> handleHandlerDebounceException(HandlerDebounceException e) {
        return new ResponseEntity<>(error(e), HttpStatus.TOO_MANY_REQUESTS);
    }

}
