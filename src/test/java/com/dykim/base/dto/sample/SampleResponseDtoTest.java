package com.dykim.base.dto.sample;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SampleResponseDtoTest {

    @Test
    void lombok_apply_test() {
        // given
        String name = "test";
        String email = "test@email.com";

        // when
        SampleRspDto rspDto = new SampleRspDto(name, email);

        // then
        assertThat(rspDto.getName()).isEqualTo(name);
        assertThat(rspDto.getEmail()).isEqualTo(email);
    }
}
