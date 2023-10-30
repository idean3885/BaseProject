package com.dykim.base.advice.hello;

import com.dykim.base.advice.hello.exception.HelloAlreadyExistException;
import com.dykim.base.advice.hello.exception.HelloAuditorAwareException;
import com.dykim.base.advice.hello.exception.HelloException;
import com.dykim.base.advice.hello.exception.HelloNotFoundException;
import com.dykim.base.sample.hello.controller.HelloController;
import com.dykim.base.sample.hello.dto.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <h3>Hello ControllerAdvice</h3>
 * Hello 컨트롤러에서 발생하는 예외를 처리한다.
 */
@Slf4j
@RestControllerAdvice(assignableTypes = HelloController.class)
public class HelloControllerAdvice {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(HelloException.class)
    public ResponseEntity<ApiResult<String>> handleHelloException(HelloException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(HelloAlreadyExistException.class)
    public ResponseEntity<ApiResult<String>> handleHelloAlreadyExistException(HelloAlreadyExistException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(HelloAuditorAwareException.class)
    public ResponseEntity<ApiResult<String>> handleHelloAuditorAwareException(HelloAuditorAwareException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(HelloNotFoundException.class)
    public ResponseEntity<ApiResult<String>> handleHelloNotFoundException(HelloNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResult<String>> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResult<String>> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
