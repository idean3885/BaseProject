package com.dykim.base.hello.v1.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class HellosRepositoryTest {

    @Autowired
    HellosRepository hellosRepository;

    @AfterEach
    public void cleanup() {
        hellosRepository.deleteAll();
    }

    @Order(1)
    @Test
    public void _1_사용자추가_조회() {
        // given
        var email = "dykimdev3885@gmail.com";
        var name = "김동영";

        hellosRepository.save(Hellos.builder()
                .email(email)
                .name(name)
                .build());

        // when
        var hellosList = hellosRepository.findAll();

        // then
        var hellos = hellosList.get(0);
        assertThat(hellos.getEmail()).isEqualTo(email);
        assertThat(hellos.getName()).isEqualTo(name);
        assertThat(hellos.getBirthday()).isNull();
    }

}
