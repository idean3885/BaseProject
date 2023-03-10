package com.dykim.base.advice.common;

import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.advice.common.exception.EntityNotFoundException;
import com.dykim.base.advice.common.exception.HandlerDebounceException;
import com.dykim.base.advice.common.exception.InvalidSessionException;
import com.dykim.base.sample.hello.dto.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.dykim.base.sample.hello.dto.ApiResult.error;

/**
 * <h3>Common ControllerAdvice</h3>
 * 모든 도메인에서 발생가능한 공통 예외를 처리한다.
 * <pre>
 *  1. 공통 예외
 *  2. 인터셉터 단계에서 발생하는 예외
 *    - 인터셉터 단계에서 예외 발생 시, 핸들러 기준으로 컨트롤러어드바이스가 매핑된다.
 *    - 따라서 모든 핸들러에 대해 예외처리가 가능해야하므로 공통 어드바이스로 처리한다.
 * </pre>
 */
@RestControllerAdvice
public class CommonControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<?> handleAlreadyExistsException(AlreadyExistsException e) {
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(HandlerDebounceException.class)
    public ResponseEntity<?> handleHandlerDebounceException(HandlerDebounceException e) {
        return new ResponseEntity<>(error(e), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidSessionException.class)
    public ResponseEntity<?> handleInvalidSessionException(InvalidSessionException e) {
        return new ResponseEntity<>(error(e), HttpStatus.UNAUTHORIZED);
    }

}
