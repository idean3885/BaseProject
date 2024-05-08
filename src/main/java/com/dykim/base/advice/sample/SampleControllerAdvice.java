package com.dykim.base.advice.sample;

import com.dykim.base.advice.sample.exception.SampleAlreadyExistException;
import com.dykim.base.advice.sample.exception.SampleAuditorAwareException;
import com.dykim.base.advice.sample.exception.SampleException;
import com.dykim.base.advice.sample.exception.SampleNotFoundException;
import com.dykim.base.controller.api.sample.SampleRestController;
import com.dykim.base.dto.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 *
 * <h3>Hello ControllerAdvice</h3>
 *
 * Hello 컨트롤러에서 발생하는 예외를 처리한다.
 */
@Slf4j
@RestControllerAdvice(assignableTypes = SampleRestController.class)
public class SampleControllerAdvice {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(SampleException.class)
    public ResponseEntity<ApiResult<String>> handleHelloException(SampleException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(SampleAlreadyExistException.class)
    public ResponseEntity<ApiResult<String>> handleHelloAlreadyExistException(
            SampleAlreadyExistException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SampleAuditorAwareException.class)
    public ResponseEntity<ApiResult<String>> handleHelloAuditorAwareException(
            SampleAuditorAwareException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SampleNotFoundException.class)
    public ResponseEntity<ApiResult<String>> handleHelloNotFoundException(SampleNotFoundException e) {
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
