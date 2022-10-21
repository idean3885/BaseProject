package com.dykim.base.hello.v1.controller.advice;

import com.dykim.base.hello.v1.controller.HelloController;
import com.dykim.base.hello.v1.controller.advice.exception.HelloException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

import static com.dykim.base.hello.v1.controller.dto.ApiResult.error;

@Slf4j
@RestControllerAdvice(assignableTypes = HelloController.class)
public class HelloControllerAdvice {

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(HelloException.class)
    public ResponseEntity<?> handleHelloException(HelloException e) {
        return new ResponseEntity<>(error(e), HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * <h3>BAD_REQUEST Exception Handler</h3>
     * <pre>
     * 1. DataIntegrityViolationException.class
     *  엔티티 컬럼 검증없이 실제 DB와 통신하여 오류가 발생한 경우만 들어온다.
     *  통신 전 검증하여 잘못된 쿼리로 통신되는 케이스를 줄여야 한다.(DB 부하 예방)
     *  컬럼 검증 로직을 놓칠 경우를 대비해 예외처리함.
     *
     * 2. ConstraintViolationException.class
     *  AOP 방식으로 동작하며 @Validated 또는 validator.validate() 등 벨리데이터를 직접사용하는 경우 발생한다.
     *  또한 JPA Entity 작업 전 AOPProxy 객체의 PreInsert() 를 통해 검증 예외 발생 시, 해당 예외가 발생한다.
     *
     * 3. MethodArgumentTypeMismatchException.class
     *  MethodArgumentTypeMismatchException: 컨트롤러 진입 시 파라미터 타입(자료형) 이 잘못 설정된 경우 발생함.
     *
     * 4. HttpMessageNotReadableException.class
     *  요청데이터 형식이 맞지 않는 경우 발생
     *  예) 날짜 포맷이 yyyy-MM-dd 인데, yyyyMMdd 로 전달받은 경우
     * </pre>
     *
     * @param e BadRequest 관련 예외
     * @return ApiResult
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            DataIntegrityViolationException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<?> handleBadRequestException(Exception e) {
        return new ResponseEntity<>(error(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // @Valid 어노테이션으로 Dto 를 검증하는 경우 발생한다.
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }

        log.error(builder.toString());

        return new ResponseEntity<>(error(e), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(error(e), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
