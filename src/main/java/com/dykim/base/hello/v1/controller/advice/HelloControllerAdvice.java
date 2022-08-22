package com.dykim.base.hello.v1.controller.advice;

import com.dykim.base.hello.v1.controller.HelloController;
import com.dykim.base.hello.v1.controller.advice.exception.HelloException;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        // 엔티티 컬럼 검증없이 실제 DB와 통신하여 오류가 발생한 경우만 들어온다.
        // 통신 전 검증하여 잘못된 쿼리로 통신되는 케이스를 줄여야 한다.(DB 부하 예방)
        // 컬럼 검증 로직을 놓칠 경우를 대비해 예외처리함.
        return new ResponseEntity<>(error(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(error(e), HttpStatus.BAD_REQUEST);
    }

}
