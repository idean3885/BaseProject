package com.dykim.base.v1.controller.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HelloResponseDtoTest {

    @Test
    void 롬복_기능_테스트() {
        // given
        String name = "test";
        int amount = 1000;

        // when
        HelloResponseDto rspDto = new HelloResponseDto(name, amount);

        // then
        assertThat(rspDto.getName()).isEqualTo(name);
        assertThat(rspDto.getAmount()).isEqualTo(amount);
    }
}
