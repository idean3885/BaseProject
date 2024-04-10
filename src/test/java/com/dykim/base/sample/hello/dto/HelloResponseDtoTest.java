package com.dykim.base.sample.hello.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class HelloResponseDtoTest {

    @Test
    void lombok_apply_test() {
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
