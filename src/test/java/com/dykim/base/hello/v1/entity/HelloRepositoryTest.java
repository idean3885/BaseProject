package com.dykim.base.hello.v1.entity;

import com.dykim.base.hello.v1.config.HelloAuditorAware;
import com.dykim.base.hello.v1.controller.advice.exception.HelloAuditorAwareException;
import com.dykim.base.hello.v1.controller.dto.HelloUpdateReqDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest
public class HelloRepositoryTest {

    @Autowired
    HelloRepository helloRepository;

    @Autowired
    HelloAuditorAware helloAuditorAware;

    @AfterEach
    public void clearAllHello() {
        helloRepository.deleteAll();
    }

    @Order(1)
    @Test
    public void findById_return_Hello() {
        // given
        var mockHello = helloRepository.save(Hello.builder()
                .email("mock@email.com")
                .name("mockName1")
                .birthday(LocalDate.parse("1993-07-24"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .useYn("Y")
                .build());

        // when
        var helloOps = helloRepository.findById(mockHello.getId());

        // then
        assertThat(helloOps.isPresent()).isTrue();

        var hello = helloOps.get();
        assertThat(hello.getEmail()).isEqualTo(mockHello.getEmail());
        assertThat(hello.getName()).isEqualTo(mockHello.getName());
        assertThat(hello.getBirthday()).isEqualTo(mockHello.getBirthday());
        assertBaseEntityFind(hello);
    }

    @Order(2)
    @Test
    public void findAll_return_Hello_list() {
        // given
        var mockHello1 = helloRepository.save(Hello.builder()
                .email("mock1@email.com")
                .name("mockName1")
                .birthday(LocalDate.parse("1993-07-24"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .useYn("Y")
                .build());

        var mockHello2 = helloRepository.save(Hello.builder()
                .email("mock2@email.com")
                .name("mockName2")
                .birthday(LocalDate.parse("1993-01-24"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .useYn("Y")
                .build());

        // when
        var helloList = helloRepository.findAll();

        // then -> 인서트 순서에 맞춰 저장되고 조회되기 때문에 순서를 보장함.
        var hello1 = helloList.get(0);
        assertThat(hello1.getEmail()).isEqualTo(mockHello1.getEmail());
        assertThat(hello1.getName()).isEqualTo(mockHello1.getName());
        assertThat(hello1.getBirthday()).isEqualTo(mockHello1.getBirthday());
        assertBaseEntityFind(hello1);

        var hello2 = helloList.get(1);
        assertThat(hello2.getEmail()).isEqualTo(mockHello2.getEmail());
        assertThat(hello2.getName()).isEqualTo(mockHello2.getName());
        assertThat(hello2.getBirthday()).isEqualTo(mockHello2.getBirthday());
        assertBaseEntityFind(hello2);
    }

    @Order(3)
    @Test
    public void save_return_Hello() {
        // given
        var hello = Hello.builder()
                .email("insertEmail@email.com")
                .name("insertName")
                .birthday(LocalDate.parse("1993-07-24"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .useYn("Y")
                .build();

        // when
        var insertedHello = helloRepository.save(hello);

        // then
        assertThat(insertedHello.getId()).isNotNull();
        assertThat(insertedHello.getEmail()).isEqualTo(hello.getEmail());
        assertThat(insertedHello.getName()).isEqualTo(hello.getName());
        assertThat(insertedHello.getBirthday()).isEqualTo(hello.getBirthday());
        assertBaseEntitySave(insertedHello);
    }

    @Order(4)
    @Test
    public void save_with_duplicate_email_throw_DataIntegrityViolationException() {
        // setup
        var mockHello = helloRepository.save(Hello.builder()
                .email("mock@email.com")
                .name("mockName1")
                .birthday(LocalDate.parse("1993-07-24"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .useYn("Y")
                .build());

        // given
        var hello = Hello.builder()
                .email(mockHello.getEmail())
                .name("insertName")
                .birthday(LocalDate.parse("1993-07-24"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .useYn("Y")
                .build();

        // when-then
        assertThrows(DataIntegrityViolationException.class,
                () -> helloRepository.save(hello)
        );
    }

    @Order(5)
    @Test
    public void save_with_empty_require_parameter_throw_ConstraintViolationException() {
        // given
        var hello = Hello.builder()
                .email("insertNameTest@email.com")
                .birthday(LocalDate.parse("1993-07-24"))
                .build();

        // when-then
        assertThrows(ConstraintViolationException.class,
                () -> helloRepository.save(hello)
        );
    }

    @Order(6)
    @Test
    public void update_with_DynamicUpdate_return_HelloUpdateRspDto() {
        // setup
        var mockHello = helloRepository.save(Hello.builder()
                .email("mock@email.com")
                .name("mockName1")
                .birthday(LocalDate.parse("1993-07-24"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .useYn("Y")
                .build());

        // given
        var helloUpdateReqDto = HelloUpdateReqDto.builder()
                .name("update Name")
                .birthday(LocalDate.parse("1900-07-24"))
                .build();

        // when
        var hello = helloRepository.save(mockHello.update(helloUpdateReqDto));

        // then
        assertThat(hello.getId()).isEqualTo(mockHello.getId());
        assertThat(hello.getName()).isEqualTo(helloUpdateReqDto.getName());
        assertThat(hello.getBirthday()).isEqualTo(helloUpdateReqDto.getBirthday());
        assertBaseEntityUpdate(hello);

        var findHelloOps = helloRepository.findById(hello.getId());
        assertThat(findHelloOps.isPresent()).isTrue();

        var findHello = findHelloOps.get();
        assertThat(findHello.getName()).isEqualTo(helloUpdateReqDto.getName());
        assertThat(findHello.getBirthday()).isEqualTo(helloUpdateReqDto.getBirthday());
        assertBaseEntityFind(findHello);
    }

    @Order(7)
    @Test
    public void delete_with_DynamicUpdate_return_Hello() {
        // setup
        var mockHello = helloRepository.save(Hello.builder()
                .email("mock@email.com")
                .name("mockName1")
                .birthday(LocalDate.parse("1993-07-24"))
                .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                .useYn("Y")
                .build());

        // given
        mockHello.delete();

        // when
        var hello = helloRepository.save(mockHello);

        // then
        assertThat(hello.getId()).isEqualTo(mockHello.getId());
        assertThat(hello.getUseYn()).isEqualTo("N");
        assertBaseEntityDelete(hello);

        var findHelloOps = helloRepository.findById(mockHello.getId());
        assertThat(findHelloOps.isPresent()).isTrue();

        var findHello = findHelloOps.get();
        assertThat(findHello.getUseYn()).isEqualTo("N");
        assertBaseEntityFind(findHello);
    }

    private LocalDateTime nowYyyyMMddHHmmssSSS() {
        var localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return LocalDateTime.parse(
                LocalDateTime.now().format(localDateTimeFormat), localDateTimeFormat
        );
    }

    private void assertBaseEntitySave(Hello hello) {
        assertBaseEntity(hello, "SAVE");
    }

    private void assertBaseEntityFind(Hello hello) {
        assertBaseEntity(hello, "FIND");
    }

    private void assertBaseEntityUpdate(Hello hello) {
        assertBaseEntity(hello, "UPDATE");
    }

    private void assertBaseEntityDelete(Hello hello) {
        assertBaseEntity(hello, "DELETE");
    }

    private void assertBaseEntity(Hello hello, String type) {
        // given
        var auditorId = helloAuditorAware.getCurrentAuditor()
                .orElseThrow(() -> new HelloAuditorAwareException("Not found sessionUserId from Auditor"));

        // then
        // 1) common
        assertThat(hello.getCreatedDateTime()).isNotNull();
        assertThat(hello.getUpdatedDateTime()).isNotNull();

        // 2) each case
        switch (type) {
            case "SAVE":
                assertThat(hello.getUpdatedDateTime()).isEqualTo(hello.getCreatedDateTime());
                assertThat(hello.getCreatedId()).isEqualTo(auditorId);
                assertThat(hello.getUpdatedId()).isEqualTo(auditorId);
                break;
            case "FIND":
                assertThat(hello.getUpdatedDateTime()).isAfterOrEqualTo(hello.getCreatedDateTime());
                break;
            case "UPDATE":
            case "DELETE":
                assertThat(hello.getUpdatedId()).isEqualTo(auditorId);
                assertThat(hello.getUpdatedDateTime()).isAfterOrEqualTo(hello.getCreatedDateTime());
                break;
            default:
                assert false;
        }
    }

}
