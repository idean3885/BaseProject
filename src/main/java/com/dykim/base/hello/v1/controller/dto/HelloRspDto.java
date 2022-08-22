package com.dykim.base.hello.v1.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Schema(name = "HelloRspDto", description = "테스트용 응답 Dto")
@Getter
@RequiredArgsConstructor
public class HelloRspDto {

    @Schema(name = "이름", required = true, example = "kdy")
    private final String name;

    @Schema(name = "이메일", required = true, example = "test@email.com")
    private final String email;

}
