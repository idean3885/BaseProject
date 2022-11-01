package com.dykim.base.hello.v1.service;

import com.dykim.base.hello.v1.controller.dto.HelloInsertReqDto;
import com.dykim.base.hello.v1.controller.dto.HelloUpdateReqDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public void insert_return_HelloInsertRspDto() {
        // given
        var reqDto = HelloInsertReqDto.builder()
                .email("success@email.com")
                .name("name")
                .birthday(parseBirthday("1900-01-01"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
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
    public void insert_with_empty_email_throw_ConstraintViolationException() {
        // given
        var reqDto = HelloInsertReqDto.builder()
                .name("name")
                .birthday(parseBirthday("2001-01-01"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .build();

        // when-then
        assertThrows(ConstraintViolationException.class,
                () -> helloService.insert(reqDto)
        );
    }

    @Order(3)
    @Test
    public void insert_invalid_email_format_throw_ConstraintViolationException() {
        // given
        var reqDto = HelloInsertReqDto.builder()
                .email("email.com")
                .name("name")
                .birthday(parseBirthday("2001-01-01"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .build();

        // when-then
        assertThrows(ConstraintViolationException.class,
                () -> helloService.insert(reqDto)
        );
    }

    @Order(4)
    @Test
    public void insert_with_empty_name_throw_ConstraintViolationException() {
        // given
        var reqDto = HelloInsertReqDto.builder()
                .email("email.com")
                .birthday(parseBirthday("2001-01-01"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .build();

        // when-then
        assertThrows(ConstraintViolationException.class,
                () -> helloService.insert(reqDto)
        );
    }

    @Order(5)
    @Test
    public void find_by_id_return_HelloFindRspDto() {
        // setup
        var mockHelloInsertRspDto = helloService.insert(HelloInsertReqDto.builder()
                .email("mock1@email.com")
                .name("mock1")
                .birthday(parseBirthday("1993-07-24"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .build());
        // given
        var helloId = mockHelloInsertRspDto.getId();

        // when
        var helloFindRspDto = helloService.find(helloId);

        // then
        assertThat(helloFindRspDto.getName()).isEqualTo(mockHelloInsertRspDto.getName());
        assertThat(helloFindRspDto.getEmail()).isEqualTo(mockHelloInsertRspDto.getEmail());
        assertThat(helloFindRspDto.getBirthday()).isEqualTo(mockHelloInsertRspDto.getBirthday());
        assertThat(helloFindRspDto.getYyyyMMddHHmmssSSS()).isEqualTo(mockHelloInsertRspDto.getYyyyMMddHHmmssSSS());
    }

    /**
     * <h3>JPA null find 예외 테스트</h3>
     * <pre>
     * id 에 null 이 입력되는 케이스를 테스트한다.
     * 실제 컨트롤러를 통해 진입 시, PathVariable 로 입력받고
     * 자료형 검증도 이루어지기 때문에 발생할 가능성은 없지만 샘플 케이스로서 작성함.
     * </pre>
     * <b>참고</b>
     * <pre>
     * JPA Repository.find(null) 실행 시 명세와 달리
     * InvalidDataAccessApiUsageException 이 발생한다.
     *
     * 이는 EntityManagerFactoryUtils 이 내부적으로
     * IllegalArgumentException 을 변경시키기 때문이다.
     *
     * 스프링 내에서 JPA 는 AOP 로 동작하게 되며 JdkDynamicAopProxy 로
     * 실행하는 과정에서 위 오류 발생 시 예외를 변경시켜주고 있다.
     * </pre>
     */
    @Order(6)
    @Test
    public void find_with_null_id_throw_InvalidDataAccessApiUsageException() {
        // given
        Long helloId = null;

        // when-then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> helloService.find(helloId));
    }

    @Order(7)
    @Test
    public void update_with_DynamicUpdate_return_HelloUpdateRspDto() {
        // setup
        var helloInsertRspDto = helloService.insert(HelloInsertReqDto.builder()
                .email("mock@email.com")
                .name("mock")
                .birthday(parseBirthday("1900-01-01"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .build());

        // given
        var helloUpdateReqDto = HelloUpdateReqDto.builder()
                .name("update name")
                .birthday(parseBirthday("1993-07-24"))
                .build();

        // when
        var helloUpdateRspDto = helloService.update(helloInsertRspDto.getId(), helloUpdateReqDto);

        // then
        assertThat(helloUpdateRspDto.getName()).isEqualTo(helloUpdateReqDto.getName());
        assertThat(helloUpdateRspDto.getBirthday()).isEqualTo(helloUpdateReqDto.getBirthday());
        assertThat(helloUpdateRspDto.getEmail()).isEqualTo(helloInsertRspDto.getEmail());
    }

    private LocalDate parseBirthday(String formatDate) {
        var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(formatDate, dateTimeFormatter);
    }

    private LocalDateTime nowYyyyMMddHHmmssSSS() {
        var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return LocalDateTime.parse(
                LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter
        );
    }

}
