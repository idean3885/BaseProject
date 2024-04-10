package com.dykim.base.advice.hello;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dykim.base.advice.hello.exception.HelloException;
import com.dykim.base.sample.hello.controller.HelloController;
import com.dykim.base.sample.hello.dto.HelloInsertReqDto;
import com.dykim.base.sample.hello.service.HelloService;
import com.dykim.base.util.TestAdviceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class HelloControllerAdviceTest {

    @Mock private HelloService helloService;

    @InjectMocks private HelloController helloController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setMockMvc() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(helloController)
                        .setControllerAdvice(new HelloControllerAdvice())
                        .build();
        objectMapper = new ObjectMapper();
    }

    @Order(1)
    @Test
    public void call_occurException_always_throw_HelloException() throws Exception {
        // given
        given(helloController.occurException(anyBoolean())).willThrow(new HelloException("Exception!"));

        // when
        mockMvc
                .perform(get("/sample/hello/occurException").param("isOccur", "true"))

                // then
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(HelloException.class))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(2)
    @Test
    public void call_insert_with_invalid_email_throw_MethodArgumentNotValidException()
            throws Exception {
        // given
        var helloInsertReqDto =
                HelloInsertReqDto.builder().email("invalid.email.com").name("name").build();
        var reqJson =
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(helloInsertReqDto);

        // when
        mockMvc
                .perform(
                        put("/sample/hello").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(MethodArgumentNotValidException.class))
                .andDo(TestAdviceUtil::printRspDto);
    }
}
