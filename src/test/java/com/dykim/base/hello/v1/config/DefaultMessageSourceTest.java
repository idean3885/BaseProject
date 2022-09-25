package com.dykim.base.hello.v1.config;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class DefaultMessageSourceTest {

    private final MessageSource messageSource;

    @Autowired
    public DefaultMessageSourceTest(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Order(1)
    @Test
    public void getMessage() {
        System.out.println(messageSource.getClass().getName());
        System.out.println(messageSource.getMessage("invalid.email", null, Locale.KOREA));
    }

}
