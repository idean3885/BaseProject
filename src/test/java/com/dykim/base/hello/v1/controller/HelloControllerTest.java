package com.dykim.base.hello.v1.controller;

import com.dykim.base.hello.v1.controller.dto.*;
import com.dykim.base.hello.v1.entity.Hello;
import com.dykim.base.hello.v1.entity.HelloRepository;
import com.dykim.base.hello.v1.service.HelloService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HelloControllerTest {

    @Mock
    private HelloRepository helloRepository;

    @InjectMocks
    private HelloService helloService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new HelloController(helloService))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Order(1)
    @Test
    public void call_helloPrint_always_return_string() throws Exception {
        // then
        mockMvc.perform(get("/hello/v1/helloPrint"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello!"));
    }

    @Order(2)
    @Test
    public void call_helloDto_always_return_HelloRspDto() throws Exception {
        // given
        var name = "name";
        var email = "test@email.com";

        // then
        mockMvc.perform(get("/hello/v1/helloDto")
                        .param("name", name)
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(getRspName(HelloRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(name)))
                .andExpect(jsonPath("$.data.email", is(email)))
                .andDo(this::printRspDto);
    }

    @Order(3)
    @Test
    public void insert_hello_return_HelloInsertRspDto() throws Exception {
        // given
        var localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        var birthday = LocalDate.parse("1993-07-24", localDateFormat);
        var yyyyMMddHHmmssSSS = LocalDateTime.parse(
                LocalDateTime.now().format(localDateTimeFormat), localDateTimeFormat
        );
        var reqDto = HelloInsertReqDto.builder()
                .email("email@base.com")
                .name("name")
                .birthday(birthday)
                .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        var hello = Hello.builder()
                .email(reqDto.getEmail())
                .name(reqDto.getName())
                .birthday(reqDto.getBirthday())
                .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                .build();

        // when
        when(helloRepository.save(any())).thenReturn(hello);

        // then
        mockMvc.perform(put("/hello/v1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(getRspName(HelloInsertRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(reqDto.getName())))
                .andExpect(jsonPath("$.data.email", is(reqDto.getEmail())))
                .andExpect(jsonPath("$.data.birthday", is(reqDto.getBirthday().format(localDateFormat))))
                .andExpect(jsonPath("$.data.yyyyMMddHHmmssSSS",
                        is(reqDto.getYyyyMMddHHmmssSSS().format(localDateTimeFormat)))
                )
                .andDo(this::printRspDto);
    }

    @Order(4)
    @Test
    public void insert_hello_with_invalid_email_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var helloInsertReqDto = HelloInsertReqDto.builder()
                .email("email")
                .name("name")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(helloInsertReqDto);

        // then
        mockMvc.perform(put("/hello/v1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(getApiResultExceptionClass(result))
                        .isEqualTo(MethodArgumentNotValidException.class)
                )
                .andDo(this::printExceptionMessage);
    }

    @Order(5)
    @Test
    public void insert_hello_with_empty_name_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var helloInsertReqDto = HelloInsertReqDto.builder()
                .email("email@base.com")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(helloInsertReqDto);

        // then
        mockMvc.perform(put("/hello/v1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(getApiResultExceptionClass(result))
                        .isEqualTo(MethodArgumentNotValidException.class)
                )
                .andDo(this::printExceptionMessage);
    }

    @Order(6)
    @Test
    public void insert_hello_with_invalid_birthday_format_throw_HttpMessageNotReadableException() throws Exception {
        // given
        var map = new HashMap<String, String>();
        map.put("birthday", "19930724");
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

        // then
        mockMvc.perform(put("/hello/v1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(getApiResultExceptionClass(result))
                        .isEqualTo(HttpMessageNotReadableException.class)
                )
                .andDo(this::printExceptionMessage);
    }

    @Order(7)
    @Test
    public void insert_hello_with_invalid_yyyyMMddHHmmssSSS_format_throw_HttpMessageNotReadableException() throws Exception {
        // given
        var map = new HashMap<String, String>();
        map.put("yyyyMMddHHmmssSSS", "20221022000700111");
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

        // then
        mockMvc.perform(put("/hello/v1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(getApiResultExceptionClass(result))
                        .isEqualTo(HttpMessageNotReadableException.class)
                )
                .andDo(this::printExceptionMessage);
    }

    @Order(8)
    @Test
    public void find_hello_return_HelloFindRspDto() throws Exception {
        // given
        var localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        var birthday = LocalDate.parse("1993-07-24", localDateFormat);
        var yyyyMMddHHmmssSSS = LocalDateTime.parse(
                LocalDateTime.now().format(localDateTimeFormat), localDateTimeFormat
        );
        var reqDto = HelloInsertReqDto.builder()
                .email("email@base.com")
                .name("name")
                .birthday(birthday)
                .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                .build();
        var hello = Hello.builder()
                .email(reqDto.getEmail())
                .name(reqDto.getName())
                .birthday(reqDto.getBirthday())
                .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                .build();
        final var ANY_HELLO_ID = 1;

        // when
        when(helloRepository.findById(any())).thenReturn(Optional.of(hello));

        // then
        mockMvc.perform(get("/hello/v1/" + ANY_HELLO_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(getRspName(HelloFindRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(reqDto.getName())))
                .andExpect(jsonPath("$.data.email", is(reqDto.getEmail())))
                .andExpect(jsonPath("$.data.birthday", is(reqDto.getBirthday().format(localDateFormat))))
                .andExpect(jsonPath("$.data.yyyyMMddHHmmssSSS",
                        is(reqDto.getYyyyMMddHHmmssSSS().format(localDateTimeFormat)))
                )
                .andDo(this::printRspDto);
    }

    @Order(9)
    @Test
    public void find_hello_with_invalid_id_type_throw_MethodArgumentTypeMismatchException() throws Exception {
        // given
        final var WRONG_HELLO_ID = "Wrong Hello Id";

        // then
        mockMvc.perform(get("/hello/v1/" + WRONG_HELLO_ID))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(getApiResultExceptionClass(result))
                        .isEqualTo(MethodArgumentTypeMismatchException.class)
                )
                .andDo(this::printExceptionMessage);
    }

    @Order(10)
    @Test
    public void update_hello_return_HelloUpdateRspDto() throws Exception {
        // setup
        var localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        var yyyyMMddHHmmssSSS = LocalDateTime.parse(
                LocalDateTime.now().format(localDateTimeFormat), localDateTimeFormat
        );
        var hello = Hello.builder()
                .email("mock@base.com")
                .name("mock")
                .birthday(LocalDate.parse("1993-07-24", localDateFormat))
                .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                .build();

        // given
        var birthday = LocalDate.parse("1995-05-24", localDateFormat);
        var reqDto = HelloUpdateReqDto.builder()
                .name("name")
                .birthday(birthday)
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        final var ANY_HELLO_ID = 1;

        // when
        when(helloRepository.findById(any())).thenReturn(Optional.of(hello));

        // then
        mockMvc.perform(post("/hello/v1/" + ANY_HELLO_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(getRspName(HelloUpdateRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(reqDto.getName())))
                .andExpect(jsonPath("$.data.email", is(hello.getEmail())))
                .andExpect(jsonPath("$.data.birthday", is(reqDto.getBirthday().format(localDateFormat))))
                .andExpect(jsonPath("$.data.yyyyMMddHHmmssSSS",
                        is(hello.getYyyyMMddHHmmssSSS().format(localDateTimeFormat)))
                )
                .andDo(this::printRspDto);
    }

    private void printRspDto(MvcResult handler) {
        try {
            log.debug("result: {}", handler.getResponse().getContentAsString());
        } catch (UnsupportedEncodingException e) {
            log.error("Fail contentAsString from response.", e);
        }
    }

    private String getRspName(Class rspDtoClass) {
        return rspDtoClass.getSimpleName();
    }

    private Class<? extends Exception> getApiResultExceptionClass(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getClass();
    }

    private void printExceptionMessage(MvcResult result) {
        log.debug("result: {}", Objects.requireNonNull(result.getResolvedException()).getLocalizedMessage());
    }

}
