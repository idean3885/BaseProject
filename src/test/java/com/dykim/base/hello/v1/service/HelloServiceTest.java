package com.dykim.base.hello.v1.service;

import com.dykim.base.hello.v1.controller.dto.HelloInsertReqDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest
public class HelloServiceTest {

    private final HelloService helloService;

    @Autowired
    public HelloServiceTest(HelloService helloService) {
        this.helloService = helloService;
    }

    @Order(1)
    @Test
    public void 헬로추가_성공() {
        // given
        var reqDto = HelloInsertReqDto.builder()
                .email("success@email.com")
                .name("name")
                .birthday("0724")
                .build();

        // when
        var rspDto = helloService.insert(reqDto);

        // then
        assertThat(rspDto.getEmail()).isEqualTo(reqDto.getEmail());
        assertThat(rspDto.getName()).isEqualTo(reqDto.getName());
        assertThat(rspDto.getBirthday()).isEqualTo(reqDto.getBirthday());
    }

    @Order(2)
    @Test
    public void 헬로추가_실패_이메일없음() {
        // given
        var reqDto = HelloInsertReqDto.builder()
                .name("name")
                .birthday("0724")
                .build();

        // when
        assertThrows(IllegalArgumentException.class,
                () -> helloService.insert(reqDto)
        );
    }

    // TODO: JPA Repository 에러지만 캐치하지 않아 서비스에서도 발생하는 오류 -> 여기서도 잡는게 맞는가?
    // TODO: 괜히 테스트케이스만 더 늘리는게 아닌지...(given 데이터 미스는 잡을 수 있으나 코드가 너무 길어질수도?)
    @Order(3)
    @Test
    public void 헬로추가_실패_잘못된이메일형식() {
        // given
        var reqDto = HelloInsertReqDto.builder()
                .email("email.com")
                .name("name")
                .birthday("0724")
                .build();

        // when
        assertThrows(ConstraintViolationException.class,
                () -> helloService.insert(reqDto)
        );
    }

    @Order(4)
    @Test
    public void 헬로추가_실패_이름없음() {
        // given
        var reqDto = HelloInsertReqDto.builder()
                .email("email.com")
                .birthday("0724")
                .build();

        // when
        assertThrows(ConstraintViolationException.class,
                () -> helloService.insert(reqDto)
        );
    }

}
