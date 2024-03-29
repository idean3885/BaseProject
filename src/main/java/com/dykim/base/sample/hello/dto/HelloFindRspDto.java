package com.dykim.base.sample.hello.dto;

import com.dykim.base.sample.hello.entity.Hello;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(name = "HelloFindRspDto", description = "Hello 조회 응답 Dto")
@Getter
@NoArgsConstructor
public class HelloFindRspDto {

    @Schema(description = "id", required = true, example = "1")
    private Long id;

    @Schema(description = "이메일", required = true, example = "test@email.com")
    private String email;

    @Schema(description = "이름", required = true, example = "kdy")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "생일", example = "1993-07-24")
    private LocalDate birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Schema(description = "생성시간 샘플", example = "2022-10-07 15:16:50.599")
    private LocalDateTime yyyyMMddHHmmssSSS;

    @Schema(description = "사용여부", example = "Y")
    private String useYn;

    public HelloFindRspDto(Hello hello) {
        id = hello.getId();
        email = hello.getEmail();
        name = hello.getName();
        birthday = hello.getBirthday();
        yyyyMMddHHmmssSSS = hello.getYyyyMMddHHmmssSSS();
        useYn = hello.getUseYn();
    }

}
