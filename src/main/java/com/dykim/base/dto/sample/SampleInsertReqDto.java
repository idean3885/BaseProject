package com.dykim.base.dto.sample;

import com.dykim.base.entity.sample.Sample;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.*;

/**
 *
 *
 * <h3>Hello 추가 요청 Dto</h3>
 *
 * <pre>
 *  파라미터 별 Bean Validation(@Eamil, @NotBlank) 와 @Valid 로 검증한다.
 *  -> 컨트롤러 진입 전 검증 오류 발생 시, MethodArgumentNotValidException 가 발생된다.
 *  -> 서비스 진입 전 검증 오류 발생 시, ConstraintViolationException 가 발생된다.
 * </pre>
 *
 * 참고) <a
 * href="https://velog.io/@idean3885/Dto-Entity-Validation-%EC%B2%98%EB%A6%AC#2-entity-validation---validated">Dto,
 * Entity Validation 처리 내용 정리한 개인 블로그</a>
 */
@Schema(name = "SampleInsertReqDto", description = "Sample 추가 요청 Dto")
@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SampleInsertReqDto {

    @NotBlank
    @Email
    @Schema(description = "이메일", required = true, example = "test@email.com")
    private String email;

    @NotBlank
    @Schema(description = "이름", required = true, example = "kdy")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "생일", example = "1993-07-24")
    private LocalDate birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Schema(description = "생성시간 샘플", example = "2022-10-07 15:16:50.599")
    private LocalDateTime yyyyMMddHHmmssSSS;

    public Sample toEntity() {
        return Sample.builder()
                .email(email)
                .name(name)
                .birthday(birthday)
                .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                .build();
    }
}
