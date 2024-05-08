package com.dykim.base.advice.common;

import static com.dykim.base.dto.ApiResult.error;

import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.advice.common.exception.EntityNotFoundException;
import com.dykim.base.advice.common.exception.HandlerDebounceException;
import com.dykim.base.advice.common.exception.InvalidSessionException;
import com.dykim.base.dto.ApiResult;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 *
 * <h3>Common ControllerAdvice</h3>
 *
 * 모든 도메인에서 발생가능한 공통 예외를 처리한다.
 *
 * <pre>
 *  1. 공통 예외
 *  2. 인터셉터 단계에서 발생하는 예외
 *    - 인터셉터 단계에서 예외 발생 시, 핸들러 기준으로 컨트롤러어드바이스가 매핑된다.
 *    - 따라서 모든 핸들러에 대해 예외처리가 가능해야하므로 공통 어드바이스로 처리한다.
 * </pre>
 */
@Slf4j
@RestControllerAdvice
public class CommonControllerAdvice {

    /**
     *
     *
     * <h3>BAD_REQUEST Exception Handler</h3>
     *
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
     * 3. HttpMessageNotReadableException.class
     *  요청데이터 형식이 맞지 않는 경우 발생
     *  예) 날짜 포맷이 yyyy-MM-dd 인데, yyyyMMdd 로 전달받은 경우
     * </pre>
     *
     * @param e BadRequest 관련 예외
     * @return ApiResult
     */
    @ExceptionHandler({
        DataIntegrityViolationException.class,
        ConstraintViolationException.class,
        HttpMessageNotReadableException.class,
        AlreadyExistsException.class
    })
    public ResponseEntity<ApiResult<String>> handleBadRequestException(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.BAD_REQUEST);
    }

    /**
     *
     *
     * <h3>MethodArgumentNotValidException Handler</h3>
     *
     * <pre>
     * 컨트롤러 진입 시 파라미터 타입(자료형) 이 잘못 설정된 경우 발생함.
     * </pre>
     *
     * @param e BadRequest 관련 예외
     * @return ApiResult
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        // @Valid 어노테이션으로 Dto 를 검증하는 경우 발생한다.
        var bindingResult = e.getBindingResult();

        var builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
        }
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e, builder.toString()), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResult<String>> handleEntityNotFoundException(
            EntityNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(ApiResult.error(e), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(HandlerDebounceException.class)
    public ResponseEntity<ApiResult<String>> handleHandlerDebounceException(
            HandlerDebounceException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(error(e), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidSessionException.class)
    public ResponseEntity<ApiResult<String>> handleInvalidSessionException(
            InvalidSessionException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(error(e), HttpStatus.UNAUTHORIZED);
    }
}
