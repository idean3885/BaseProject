package com.dykim.base.hello.v1.advice;

import com.dykim.base.hello.v1.controller.HelloController;
import com.dykim.base.hello.v1.controller.advice.HelloControllerAdvice;
import com.dykim.base.hello.v1.controller.advice.exception.HelloException;
import com.dykim.base.hello.v1.controller.dto.HelloInsertReqDto;
import com.dykim.base.hello.v1.service.HelloService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(helloController)
                .setControllerAdvice(new HelloControllerAdvice())
                .build();

        objectMapper = new ObjectMapper();
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(result -> assertThat(getApiResultExceptionClass(result)).isEqualTo(HelloException.class))
                .andDo(this::printContent);
    }

    @Order(2)
    @Test
    public void MethodArgumentNotValidException_이메일형식() throws Exception {
        // given
        var helloInsertReqDto = HelloInsertReqDto.builder()
                .email("invalid.email.com")
                .name("name")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(helloInsertReqDto);

        // then
        mockMvc.perform(post("/hello/v1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(getApiResultExceptionClass(result))
                        .isEqualTo(MethodArgumentNotValidException.class)
                )
                .andDo(this::printContent);
    }

    private Class<? extends Exception> getApiResultExceptionClass(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getClass();
    }

    private void printContent(MvcResult result) {
        try {
            log.debug("result: {}", result.getResponse().getContentAsString());
        } catch (UnsupportedEncodingException e) {
            log.error("Print response contents fail.");
        }
    }

}
