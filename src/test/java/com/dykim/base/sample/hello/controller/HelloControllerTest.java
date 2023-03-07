package com.dykim.base.sample.hello.controller;

import com.dykim.base.sample.hello.dto.*;
import com.dykim.base.sample.hello.entity.Hello;
import com.dykim.base.sample.hello.entity.HelloRepository;
import com.dykim.base.sample.hello.service.HelloService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.core.util.Json;
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
import static org.mockito.BDDMockito.given;
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
    public void call_helloPrintDebounce_always_return_string() throws Exception {
        // when
        mockMvc.perform(get("/sample/hello/helloPrintDebounce"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(Json.pretty("hello!")));
    }

    @Order(2)
    @Test
    public void call_helloDto_always_return_HelloRspDto() throws Exception {
        // given
        var name = "name";
        var email = "test@email.com";

        // when
        mockMvc.perform(get("/sample/hello/helloDto")
                        .param("name", name)
                        .param("email", email))
                // then
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
        given(helloRepository.save(any())).willReturn(hello);

        // when
        mockMvc.perform(put("/sample/hello")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
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

        // when
        mockMvc.perform(put("/sample/hello")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
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

        // when
        mockMvc.perform(put("/sample/hello")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
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

        // when
        mockMvc.perform(put("/sample/hello")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
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

        // when
        mockMvc.perform(put("/sample/hello")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
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
        given(helloRepository.findById(any())).willReturn(Optional.of(hello));

        // when
        mockMvc.perform(get("/sample/hello/" + ANY_HELLO_ID))
                // then
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

        // when
        mockMvc.perform(get("/sample/hello/" + WRONG_HELLO_ID))
                // then
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
        given(helloRepository.findByIdAndUseYn(any(), any())).willReturn(Optional.of(hello));
        given(helloRepository.save(any())).willReturn(hello);

        // when
        mockMvc.perform(post("/sample/hello/" + ANY_HELLO_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
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

    @Order(11)
    @Test
    public void delete_hello_return_HelloDeleteRspDto() throws Exception {
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
        given(helloRepository.findByIdAndUseYn(any(), any())).willReturn(Optional.of(hello));
        given(helloRepository.save(any())).willReturn(hello);

        // when
        mockMvc.perform(delete("/sample/hello/" + ANY_HELLO_ID))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(getRspName(HelloDeleteRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(reqDto.getName())))
                .andExpect(jsonPath("$.data.email", is(reqDto.getEmail())))
                .andExpect(jsonPath("$.data.birthday", is(reqDto.getBirthday().format(localDateFormat))))
                .andExpect(jsonPath("$.data.yyyyMMddHHmmssSSS",
                        is(reqDto.getYyyyMMddHHmmssSSS().format(localDateTimeFormat)))
                )
                .andExpect(jsonPath("$.data.useYn", is("N")))
                .andDo(this::printRspDto);
    }

    private void printRspDto(MvcResult handler) {
        try {
            log.debug("result: {}", handler.getResponse().getContentAsString());
        } catch (UnsupportedEncodingException e) {
            log.error("Fail contentAsString from response.", e);
        }
    }

    private <T> String getRspName(Class<T> rspDto) {
        return rspDto.getSimpleName();
    }

    private Class<? extends Exception> getApiResultExceptionClass(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getClass();
    }

    private void printExceptionMessage(MvcResult result) {
        log.debug("result: {}", Objects.requireNonNull(result.getResolvedException()).getLocalizedMessage());
    }

}
