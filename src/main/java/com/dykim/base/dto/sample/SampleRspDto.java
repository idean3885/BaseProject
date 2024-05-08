package com.dykim.base.dto.sample;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Schema(name = "SampleRspDto", description = "테스트용 응답 Dto")
@Getter
@RequiredArgsConstructor
public class SampleRspDto {

    @Schema(description = "이름", required = true, example = "kdy")
    private final String name;

    @Schema(description = "이메일", required = true, example = "test@email.com")
    private final String email;
}
