package com.dykim.base.controller.sample;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dykim.base.controller.api.sample.SampleRestController;
import com.dykim.base.dto.sample.SampleDeleteRspDto;
import com.dykim.base.dto.sample.SampleFindListRspDto;
import com.dykim.base.dto.sample.SampleFindRspDto;
import com.dykim.base.dto.sample.SampleInsertReqDto;
import com.dykim.base.dto.sample.SampleInsertRspDto;
import com.dykim.base.dto.sample.SampleRspDto;
import com.dykim.base.dto.sample.SampleUpdateReqDto;
import com.dykim.base.dto.sample.SampleUpdateRspDto;
import com.dykim.base.entity.sample.Sample;
import com.dykim.base.repository.sample.SampleRepository;
import com.dykim.base.service.sample.SampleService;
import com.dykim.base.util.TestAdviceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.core.util.Json;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SampleRestControllerTest {

    @Mock private SampleRepository sampleRepository;

    @InjectMocks private SampleService sampleService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new SampleRestController(sampleService)).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Order(1)
    @Test
    void call_helloPrintDebounce_always_return_string() throws Exception {
        // when
        mockMvc
                .perform(get("/sample/hello/helloPrintDebounce"))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(Json.pretty("hello!")));
    }

    @Order(2)
    @Test
    void call_helloDto_always_return_HelloRspDto() throws Exception {
        // given
        var name = "name";
        var email = "test@email.com";

        // when
        mockMvc
                .perform(get("/sample/hello/helloDto").param("name", name).param("email", email))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(SampleRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(name)))
                .andExpect(jsonPath("$.data.email", is(email)))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(3)
    @Test
    void insert_return_HelloInsertRspDto() throws Exception {
        // given
        var localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        var birthday = LocalDate.parse("1993-07-24", localDateFormat);
        var yyyyMMddHHmmssSSS =
                LocalDateTime.parse(LocalDateTime.now().format(localDateTimeFormat), localDateTimeFormat);
        var reqDto =
                SampleInsertReqDto.builder()
                        .email("email@base.com")
                        .name("name")
                        .birthday(birthday)
                        .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        var hello =
                Sample.builder()
                        .email(reqDto.getEmail())
                        .name(reqDto.getName())
                        .birthday(reqDto.getBirthday())
                        .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                        .build();
        given(sampleRepository.save(any())).willReturn(hello);

        // when
        mockMvc
                .perform(
                        put("/sample/hello").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(SampleInsertRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(reqDto.getName())))
                .andExpect(jsonPath("$.data.email", is(reqDto.getEmail())))
                .andExpect(jsonPath("$.data.birthday", is(reqDto.getBirthday().format(localDateFormat))))
                .andExpect(
                        jsonPath(
                                "$.data.yyyyMMddHHmmssSSS",
                                is(reqDto.getYyyyMMddHHmmssSSS().format(localDateTimeFormat))))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(4)
    @Test
    void insert_with_invalid_email_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var helloInsertReqDto = SampleInsertReqDto.builder().email("email").name("name").build();
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
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(5)
    @Test
    void insert_with_empty_name_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var helloInsertReqDto = SampleInsertReqDto.builder().email("email@base.com").build();
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
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(6)
    @Test
    void insert_with_invalid_birthday_format_throw_HttpMessageNotReadableException()
            throws Exception {
        // given
        var map = new HashMap<String, String>();
        map.put("birthday", "19930724");
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

        // when
        mockMvc
                .perform(
                        put("/sample/hello").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(HttpMessageNotReadableException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(7)
    @Test
    void insert_with_invalid_yyyyMMddHHmmssSSS_format_throw_HttpMessageNotReadableException()
            throws Exception {
        // given
        var map = new HashMap<String, String>();
        map.put("yyyyMMddHHmmssSSS", "20221022000700111");
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

        // when
        mockMvc
                .perform(
                        put("/sample/hello").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(HttpMessageNotReadableException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(8)
    @Test
    void find_return_HelloFindRspDto() throws Exception {
        // given
        var localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        var birthday = LocalDate.parse("1993-07-24", localDateFormat);
        var yyyyMMddHHmmssSSS =
                LocalDateTime.parse(LocalDateTime.now().format(localDateTimeFormat), localDateTimeFormat);
        var reqDto =
                SampleInsertReqDto.builder()
                        .email("email@base.com")
                        .name("name")
                        .birthday(birthday)
                        .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                        .build();
        var hello =
                Sample.builder()
                        .email(reqDto.getEmail())
                        .name(reqDto.getName())
                        .birthday(reqDto.getBirthday())
                        .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                        .build();
        final var ANY_HELLO_ID = 1;
        given(sampleRepository.findById(any())).willReturn(Optional.of(hello));

        // when
        mockMvc
                .perform(get("/sample/hello/" + ANY_HELLO_ID))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(SampleFindRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(reqDto.getName())))
                .andExpect(jsonPath("$.data.email", is(reqDto.getEmail())))
                .andExpect(jsonPath("$.data.birthday", is(reqDto.getBirthday().format(localDateFormat))))
                .andExpect(
                        jsonPath(
                                "$.data.yyyyMMddHHmmssSSS",
                                is(reqDto.getYyyyMMddHHmmssSSS().format(localDateTimeFormat))))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(9)
    @Test
    void find_with_invalid_id_type_throw_MethodArgumentTypeMismatchException() throws Exception {
        // given
        final var WRONG_HELLO_ID = "Wrong Hello Id";

        // when
        mockMvc
                .perform(get("/sample/hello/" + WRONG_HELLO_ID))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(MethodArgumentTypeMismatchException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(10)
    @Test
    void findList_return_HelloFindListRspDto() throws Exception {
        // given
        var localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        final IntFunction<String> initEmailByNumber =
                number -> String.format("mock%d@email.com", number);
        var sameSaveHello =
                Sample.builder()
                        .name("mockName1")
                        .birthday(LocalDate.parse("1993-07-24"))
                        .yyyyMMddHHmmssSSS(nowYyyyMMddHHmmssSSS())
                        .useYn("Y")
                        .build();
        final var SAME_COUNT = 10;
        var sameHelloList =
                IntStream.range(1, SAME_COUNT + 1)
                        .mapToObj(
                                number ->
                                        Sample.builder()
                                                .email(initEmailByNumber.apply(number))
                                                .name(sameSaveHello.getName())
                                                .birthday(sameSaveHello.getBirthday())
                                                .yyyyMMddHHmmssSSS(sameSaveHello.getYyyyMMddHHmmssSSS())
                                                .useYn(sameSaveHello.getUseYn())
                                                .build())
                        .collect(Collectors.toList());
        given(sampleRepository.findAllByName(sameSaveHello.getName()))
                .willReturn(Optional.of(sameHelloList));

        // id, unique 값 외 모든 컬럼 값이 동일하기 때문에 전체 조사 대신 표본 1개만 검증한다.
        var specimenIdx = new Random(System.currentTimeMillis()).nextInt(SAME_COUNT);
        var specimenMember = sameHelloList.get(specimenIdx);
        Function<String, String> specimenDataExp =
                key -> String.format("$.data.list[%d].%s", specimenIdx, key);

        // when
        mockMvc
                .perform(get("/sample/hello?name=" + sameSaveHello.getName()))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(SampleFindListRspDto.class))))
                .andExpect(jsonPath(specimenDataExp.apply("name"), is(specimenMember.getName())))
                .andExpect(
                        jsonPath(specimenDataExp.apply("email"), is(initEmailByNumber.apply(specimenIdx + 1))))
                .andExpect(
                        jsonPath(
                                specimenDataExp.apply("birthday"),
                                is(specimenMember.getBirthday().format(localDateFormat))))
                .andExpect(
                        jsonPath(
                                specimenDataExp.apply("yyyyMMddHHmmssSSS"),
                                is(specimenMember.getYyyyMMddHHmmssSSS().format(localDateTimeFormat))))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(11)
    @Test
    void update_return_HelloUpdateRspDto() throws Exception {
        // setup
        var localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        var yyyyMMddHHmmssSSS =
                LocalDateTime.parse(LocalDateTime.now().format(localDateTimeFormat), localDateTimeFormat);
        var hello =
                Sample.builder()
                        .email("mock@base.com")
                        .name("mock")
                        .birthday(LocalDate.parse("1993-07-24", localDateFormat))
                        .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                        .build();

        // given
        var birthday = LocalDate.parse("1995-05-24", localDateFormat);
        var reqDto = SampleUpdateReqDto.builder().name("name").birthday(birthday).build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        final var ANY_HELLO_ID = 1;
        given(sampleRepository.findByIdAndUseYn(any(), any())).willReturn(Optional.of(hello));
        given(sampleRepository.save(any())).willReturn(hello);

        // when
        mockMvc
                .perform(
                        post("/sample/hello/" + ANY_HELLO_ID)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(reqJson))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(SampleUpdateRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(reqDto.getName())))
                .andExpect(jsonPath("$.data.email", is(hello.getEmail())))
                .andExpect(jsonPath("$.data.birthday", is(reqDto.getBirthday().format(localDateFormat))))
                .andExpect(
                        jsonPath(
                                "$.data.yyyyMMddHHmmssSSS",
                                is(hello.getYyyyMMddHHmmssSSS().format(localDateTimeFormat))))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(12)
    @Test
    void delete_return_HelloDeleteRspDto() throws Exception {
        // given
        var localDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var localDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        var birthday = LocalDate.parse("1993-07-24", localDateFormat);
        var yyyyMMddHHmmssSSS =
                LocalDateTime.parse(LocalDateTime.now().format(localDateTimeFormat), localDateTimeFormat);
        var reqDto =
                SampleInsertReqDto.builder()
                        .email("email@base.com")
                        .name("name")
                        .birthday(birthday)
                        .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                        .build();
        var hello =
                Sample.builder()
                        .email(reqDto.getEmail())
                        .name(reqDto.getName())
                        .birthday(reqDto.getBirthday())
                        .yyyyMMddHHmmssSSS(yyyyMMddHHmmssSSS)
                        .build();
        final var ANY_HELLO_ID = 1;
        given(sampleRepository.findByIdAndUseYn(any(), any())).willReturn(Optional.of(hello));
        given(sampleRepository.save(any())).willReturn(hello);

        // when
        mockMvc
                .perform(delete("/sample/hello/" + ANY_HELLO_ID))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(SampleDeleteRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(reqDto.getName())))
                .andExpect(jsonPath("$.data.email", is(reqDto.getEmail())))
                .andExpect(jsonPath("$.data.birthday", is(reqDto.getBirthday().format(localDateFormat))))
                .andExpect(
                        jsonPath(
                                "$.data.yyyyMMddHHmmssSSS",
                                is(reqDto.getYyyyMMddHHmmssSSS().format(localDateTimeFormat))))
                .andExpect(jsonPath("$.data.useYn", is("N")))
                .andDo(TestAdviceUtil::printRspDto);
    }

    private LocalDateTime nowYyyyMMddHHmmssSSS() {
        var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return LocalDateTime.parse(LocalDateTime.now().format(dateTimeFormatter), dateTimeFormatter);
    }
}
