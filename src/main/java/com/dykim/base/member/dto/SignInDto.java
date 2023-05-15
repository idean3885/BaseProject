package com.dykim.base.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Schema(name = "SignInDto", description = "로그인 Dto")
@ToString
@Getter
@Setter // 타임리프 프론트에서 Setter 가 있어야만 렌더링하고 읽을 수 있어서 부득이 설정함.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInDto {

    @Email(message = "이메일을 정확히 입력해주세요.")
    @Max(value = 100, message = "이메일은 최대 100자까지만 입력할 수 있습니다.")
    @Schema(description = "회원 이메일", required = true, example = "email@valid.com")
    private String mbrEml;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Max(value = 64, message = "비밀번호는 최대 64자까지만 입력할 수 있습니다.")
    @Schema(description = "회원 비밀번호", required = true)
    private String mbrPswd;

}
