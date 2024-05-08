package com.dykim.base.advice.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dykim.base.advice.sample.exception.SampleException;
import com.dykim.base.controller.api.sample.SampleRestController;
import com.dykim.base.dto.sample.SampleInsertReqDto;
import com.dykim.base.service.sample.SampleService;
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
class SampleRestControllerAdviceTest {

    @Mock private SampleService sampleService;

    @InjectMocks private SampleRestController sampleRestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setMockMvc() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(sampleRestController)
                        .setControllerAdvice(new SampleControllerAdvice())
                        .build();
        objectMapper = new ObjectMapper();
    }

    @Order(1)
    @Test
    void call_occurException_always_throw_HelloException() throws Exception {
        // given
        given(sampleRestController.occurException(anyBoolean()))
                .willThrow(new SampleException("Exception!"));

        // when
        mockMvc
                .perform(get("/sample/hello/occurException").param("isOccur", "true"))

                // then
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(SampleException.class))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(2)
    @Test
    void call_insert_with_invalid_email_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var helloInsertReqDto =
                SampleInsertReqDto.builder().email("invalid.email.com").name("name").build();
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
