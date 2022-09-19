package com.dykim.base.hello.v1.controller.dto;

import com.dykim.base.hello.v1.entity.Hello;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Schema(description = "Hello 추가 요청 Dto")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HelloInsertReqDto {

    @Email(message = "{invalid.message}")
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