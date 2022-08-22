package com.dykim.base.hello.v1.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloResponseDtoTest {

    @Test
    void 롬복_기능_테스트() {
        // given
        String name = "test";
        String email = "test@email.com";

        // when
        HelloRspDto rspDto = new HelloRspDto(name, email);

        // then
        assertThat(rspDto.getName()).isEqualTo(name);
        assertThat(rspDto.getEmail()).isEqualTo(email);
    }

}
