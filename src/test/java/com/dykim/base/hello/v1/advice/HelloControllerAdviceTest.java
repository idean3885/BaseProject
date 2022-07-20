package com.dykim.base.hello.v1.advice;

import com.dykim.base.hello.v1.controller.HelloController;
import com.dykim.base.hello.v1.controller.advice.HelloControllerAdvice;
import com.dykim.base.hello.v1.controller.advice.exception.HelloException;
import com.dykim.base.hello.v1.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class HelloControllerAdviceTest {

    @Mock
    private HelloService helloService;

    @InjectMocks
    private HelloController helloController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setMockMvc() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(helloController)
                .setControllerAdvice(new HelloControllerAdvice())
                .build();
    }

    @Order(1)
    @Test
    public void HelloException_발생() throws Exception {
        // when
        when(helloController.occurException(anyBoolean())).thenThrow(new HelloException("Exception!"));

        // then
        mockMvc.perform(get("/hello/v1/occurException")
                        .param("isOccur", "true")
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(result -> assertThat(getApiResultExceptionClass(result)).isEqualTo(HelloException.class))
                .andDo(handler -> log.debug("result: {}", handler.getResponse().getContentAsString()));
    }

    private Class<? extends Exception> getApiResultExceptionClass(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getClass();
    }

}
