package com.dykim.base.hello.v1.controller.dto;

import com.dykim.base.hello.v1.entity.Hello;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * HelloInsertReqDto Dto<p/>
 *  파라미터 별 Bean Validator(@Eamil, @NotBlank) 로 검증한다.<br/>
 *  컨트롤러 진입 전 @Valid 어노테이션을 감지하여 검증한다.<br/>
 *  -> 검증 오류 발생 시, MethodArgumentNotValidException 가 발생된다.<br/>
 * <p/>
 * 참고) <a href="https://velog.io/@idean3885/Dto-Entity-Validation-%EC%B2%98%EB%A6%AC#2-entity-validation---validated">Dto, Entity Validation 처리 내용 정리한 개인 블로그</a>
 */
@Schema(description = "Hello 추가 요청 Dto")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HelloInsertReqDto {

    @NotBlank
    @Email
    @Schema(description = "이메일", required = true, example = "test@email.com")
    private String email;

    @NotBlank
    @Schema(description = "이름", required = true, example = "kdy")
    private String name;

    @Schema(description = "생일", example = "19930724")
    private String birthday;

    public Hello toEntity() {
        return Hello.builder()
                .email(email)
                .name(name)
                .birthday(birthday)
                .build();
    }

}