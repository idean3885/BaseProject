package com.dykim.base.hello.v1.controller.dto;

import com.dykim.base.hello.v1.entity.Hello;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(name = "HelloInsertRspDto", description = "Hello 추가 응답 Dto")
@Getter
public class HelloInsertRspDto {

    @Schema(description = "id", required = true, example = "1")
    private final Long id;

    @Schema(description = "이메일", required = true, example = "test@email.com")
    private final String email;

    @Schema(description = "이름", required = true, example = "kdy")
    private final String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "생일", example = "1993-07-24")
    private final LocalDate birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Schema(description = "생성시간 샘플", example = "2022-10-07 00:16:50.599")
    private final LocalDateTime yyyyMMddHHmmssSSS;

    public HelloInsertRspDto(Hello hello) {
        id = hello.getId();
        email = hello.getEmail();
        name = hello.getName();
        birthday = hello.getBirthday();
        yyyyMMddHHmmssSSS = hello.getYyyyMMddHHmmssSSS();
    }

}
