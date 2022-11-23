package com.dykim.base.hello.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

/**
 * <h3>Hello 수정 요청 Dto</h3>
 * <pre>
 *  파라미터 별 Bean Validation(@Eamil, @NotBlank) 와 @Valid 로 검증한다.
 *  -> 컨트롤러 진입 전 검증 오류 발생 시, MethodArgumentNotValidException 가 발생된다.
 *  -> 서비스 진입 전 검증 오류 발생 시, ConstraintViolationException 가 발생된다.
 * </pre>
 * 참고) <a href="https://velog.io/@idean3885/Dto-Entity-Validation-%EC%B2%98%EB%A6%AC#2-entity-validation---validated">Dto, Entity Validation 처리 내용 정리한 개인 블로그</a>
 */
@Schema(name = "HelloUpdateReqDto", description = "Hello 수정 요청 Dto")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HelloUpdateReqDto {

    @Schema(description = "이름", required = true, example = "kdy")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "생일", example = "1993-07-24")
    private LocalDate birthday;

}
