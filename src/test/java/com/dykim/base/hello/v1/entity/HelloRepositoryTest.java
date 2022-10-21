package com.dykim.base.hello.v1.entity;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest
public class HelloRepositoryTest {

    @Autowired
    HelloRepository hellosRepository;

    private Hello mockHello1;
    private Hello mockHello2;

    @BeforeEach
    public void setup() {
        mockHello1 = hellosRepository.save(Hello.builder()
                .email("mock1@email.com")
                .name("mockName1")
                .birthday(LocalDate.parse("19930724"))
                .build());
        mockHello2 = hellosRepository.save(Hello.builder()
                .email("mock2@email.com")
                .name("mockName2")
                .build());
    }

    @AfterEach
    public void clear() {
        hellosRepository.deleteAll();
    }

    @Order(1)
    @Test
    public void 사용자조회_단일() {
        // when
        var helloOps = hellosRepository.findById(mockHello1.getId());

        // then
        assertThat(helloOps.isPresent()).isTrue();

        var hello = helloOps.get();
        assertThat(hello.getEmail()).isEqualTo(mockHello1.getEmail());
        assertThat(hello.getName()).isEqualTo(mockHello1.getName());
        assertThat(hello.getBirthday()).isEqualTo(mockHello1.getBirthday());
    }

    @Order(2)
    @Test
    public void 사용자조회_목록() {
        // when
        var helloList = hellosRepository.findAll();

        // then -> 인서트 순서에 맞춰 저장되고 조회되기 때문에 순서를 보장함.
        var hello1 = helloList.get(0);
        assertThat(hello1.getEmail()).isEqualTo(mockHello1.getEmail());
        assertThat(hello1.getName()).isEqualTo(mockHello1.getName());
        assertThat(hello1.getBirthday()).isEqualTo(mockHello1.getBirthday());

        var hello2 = helloList.get(1);
        assertThat(hello2.getEmail()).isEqualTo(mockHello2.getEmail());
        assertThat(hello2.getName()).isEqualTo(mockHello2.getName());
        assertThat(hello2.getBirthday()).isEqualTo(mockHello2.getBirthday());
    }

    @Order(3)
    @Test
    public void 사용자추가_성공() {
        // given
        var hello = Hello.builder()
                .email("insertEmail@email.com")
                .name("insertName")
                .birthday(LocalDate.parse("19930724"))
                .build();

        // when
        var insertedHello = hellosRepository.save(hello);

        // then
        assertThat(insertedHello.getId()).isNotNull();
        assertThat(insertedHello.getEmail()).isEqualTo(hello.getEmail());
        assertThat(insertedHello.getName()).isEqualTo(hello.getName());
        assertThat(insertedHello.getBirthday()).isEqualTo(hello.getBirthday());
    }

    @Order(4)
    @Test
    public void 사용자추가_실패_중복된이메일() {
        // given
        var hello = Hello.builder()
                .email(mockHello1.getEmail())
                .name("insertName")
                .birthday(LocalDate.parse("19930724"))
                .build();

        // when
        assertThrows(DataIntegrityViolationException.class,
                () -> hellosRepository.save(hello)
        );
    }

    @Order(5)
    @Test
    public void 사용자추가_실패_필수값누락_이름() {
        // given
        var hello = Hello.builder()
                .email("insertNameTest@email.com")
                .birthday(LocalDate.parse("19930724"))
                .build();

        // when
        assertThrows(ConstraintViolationException.class,
                () -> hellosRepository.save(hello)
        );
    }

}
