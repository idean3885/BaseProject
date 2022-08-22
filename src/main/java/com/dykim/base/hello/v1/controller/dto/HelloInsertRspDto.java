package com.dykim.base.hello.v1.controller.dto;

import com.dykim.base.hello.v1.domain.Hello;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "HelloInsertRspDto", description = "Hello 추가 응답 Dto")
@Getter
public class HelloInsertRspDto {

    @Schema(name = "id", required = true, example = "1")
    private final Long id;

    @Schema(name = "이메일", required = true, example = "test@email.com")
    private final String email;

    @Schema(name = "이름", required = true, example = "kdy")
    private final String name;

    @Schema(name = "생일", example = "19930724")
    private final String birthday;

    public HelloInsertRspDto(Hello hello) {
        id = hello.getId();
        email = hello.getEmail();
        name = hello.getName();
        birthday = hello.getBirthday();
    }

}
