package com.dykim.base.hello.v1.advice;

import com.dykim.base.hello.v1.controller.HelloController;
import com.dykim.base.hello.v1.controller.advice.HelloControllerAdvice;
import com.dykim.base.hello.v1.controller.advice.exception.HelloException;
import com.dykim.base.hello.v1.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HelloControllerAdviceTest {

    private MockMvc mockMvc;

    @BeforeAll
    public void setMockMvc() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new HelloController(new HelloService()))
                .setControllerAdvice(new HelloControllerAdvice())
                .build();
    }

    @Order(1)
    @Test
    public void _1_HelloException_발생() throws Exception {
        mockMvc.perform(get("/hello/v1/occurException")
                        .param("isOccur", "true")
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(result -> Assertions.assertThat(getApiResultExceptionClass(result)).isEqualTo(HelloException.class))
                .andDo(handler -> log.debug("result: {}", handler.getResponse().getContentAsString()));
    }

    private Class<? extends Exception> getApiResultExceptionClass(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getClass();
    }

}
