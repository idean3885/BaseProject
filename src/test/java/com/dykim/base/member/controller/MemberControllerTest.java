package com.dykim.base.member.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dykim.base.advice.common.CommonControllerAdvice;
import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.advice.common.exception.EntityNotFoundException;
import com.dykim.base.member.dto.*;
import com.dykim.base.member.entity.Member;
import com.dykim.base.member.entity.MemberRepository;
import com.dykim.base.member.service.MemberService;
import com.dykim.base.util.TestAdviceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberControllerTest {

    @Mock private MemberRepository memberRepository;

    @InjectMocks private MemberService memberService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new MemberController(memberService))
                        .setControllerAdvice(new CommonControllerAdvice())
                        .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Order(1)
    @Test
    public void insert_return_MemberInsertRspDto() throws Exception {
        // given
        var reqDto =
                MemberInsertReqDto.builder()
                        .email("email@base.com")
                        .password("pswd")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("road address")
                        .detailAddress("detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        var member =
                Member.builder()
                        .email(reqDto.getEmail())
                        .password(reqDto.getPassword())
                        .name(reqDto.getName())
                        .phoneNo(reqDto.getPhoneNo())
                        .roadNameAddress(reqDto.getRoadNameAddress())
                        .detailAddress(reqDto.getDetailAddress())
                        .useYn("Y")
                        .build();
        given(memberRepository.save(any())).willReturn(member);

        // when
        mockMvc
                .perform(put("/member").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(MemberInsertRspDto.class))))
                .andExpect(jsonPath("$.data.email", is(reqDto.getEmail())))
                .andExpect(jsonPath("$.data.password", is(reqDto.getPassword())))
                .andExpect(jsonPath("$.data.name", is(reqDto.getName())))
                .andExpect(jsonPath("$.data.phoneNo", is(reqDto.getPhoneNo())))
                .andExpect(jsonPath("$.data.roadNameAddress", is(reqDto.getRoadNameAddress())))
                .andExpect(jsonPath("$.data.detailAddress", is(reqDto.getDetailAddress())))
                .andExpect(jsonPath("$.data.useYn", is("Y")))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(2)
    @Test
    public void insert_with_invalid_mbrEml_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto =
                MemberInsertReqDto.builder()
                        .email("invalid.email.com")
                        .password("pswd")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("road address")
                        .detailAddress("detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc
                .perform(put("/member").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(MethodArgumentNotValidException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(3)
    @Test
    public void insert_with_null_mbrPswd_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto =
                MemberInsertReqDto.builder()
                        .email("valid@email.com")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("road address")
                        .detailAddress("detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc
                .perform(put("/member").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(MethodArgumentNotValidException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(4)
    @Test
    public void insert_with_blank_mbrPswd_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto =
                MemberInsertReqDto.builder()
                        .email("valid@email.com")
                        .password("  ")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("road address")
                        .detailAddress("detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc
                .perform(put("/member").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
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
    public void insert_with_null_mbrNm_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto =
                MemberInsertReqDto.builder()
                        .email("valid@email.com")
                        .password("pswd")
                        .phoneNo("01234567890")
                        .roadNameAddress("road address")
                        .detailAddress("detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc
                .perform(put("/member").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
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
    public void insert_with_blank_mbrNm_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto =
                MemberInsertReqDto.builder()
                        .email("valid@email.com")
                        .password("pswd")
                        .name("  ")
                        .phoneNo("01234567890")
                        .roadNameAddress("road address")
                        .detailAddress("detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc
                .perform(put("/member").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(MethodArgumentNotValidException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(7)
    @Test
    public void insert_with_11_over_mbrTelno_throw_MethodArgumentNotValidException()
            throws Exception {
        // given
        var reqDto =
                MemberInsertReqDto.builder()
                        .email("valid@email.com")
                        .password("pswd")
                        .name("name")
                        .phoneNo("012345678901")
                        .roadNameAddress("road address")
                        .detailAddress("detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc
                .perform(put("/member").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(MethodArgumentNotValidException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(8)
    @Test
    public void insert_exists_mbrEml_useYn_Y_throw_AlreadyExistsException() throws Exception {
        // given
        var reqDto =
                MemberInsertReqDto.builder()
                        .email("valid@email.com")
                        .password("pswd")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("road address")
                        .detailAddress("detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        given(memberRepository.existsByEmailAndUseYn(any(), any())).willReturn(Optional.of(true));

        // when
        mockMvc
                .perform(put("/member").contentType(MediaType.APPLICATION_JSON_VALUE).content(reqJson))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(AlreadyExistsException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(9)
    @Test
    public void select_return_MemberSelectRspDto() throws Exception {
        // given
        var member =
                Member.builder()
                        .email("valid@email.com")
                        .password("pswd")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("roadNameAddress")
                        .detailAddress("detailAddress")
                        .useYn("Y")
                        .build();
        final var ANY_MBR_ID = 1;
        given(memberRepository.findById(any())).willReturn(Optional.of(member));

        // when
        mockMvc
                .perform(get("/member/" + ANY_MBR_ID))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(MemberSelectRspDto.class))))
                .andExpect(jsonPath("$.data.email", is(member.getEmail())))
                .andExpect(jsonPath("$.data.password", is(member.getPassword())))
                .andExpect(jsonPath("$.data.name", is(member.getName())))
                .andExpect(jsonPath("$.data.phoneNo", is(member.getPhoneNo())))
                .andExpect(jsonPath("$.data.roadNameAddress", is(member.getRoadNameAddress())))
                .andExpect(jsonPath("$.data.detailAddress", is(member.getDetailAddress())))
                .andExpect(jsonPath("$.data.useYn", is("Y")))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(10)
    @Test
    public void select_with_invalid_mbrId_type_throw_MethodArgumentTypeMismatchException()
            throws Exception {
        // given
        final var WRONG_MBR_ID = "String Id!";

        // when
        mockMvc
                .perform(get("/member/" + WRONG_MBR_ID))
                // then
                .andExpect(status().isBadRequest())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(MethodArgumentTypeMismatchException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(11)
    @Test
    public void selectList_return_MemberSelectListRspDto() throws Exception {
        // given
        final IntFunction<String> initEmlByNumber = number -> String.format("mock%d@email.com", number);
        var sameSaveMember =
                Member.builder()
                        .password("mock-pswd")
                        .name("sameName")
                        .phoneNo("01234567890")
                        .roadNameAddress("mock-roadNameAddress")
                        .detailAddress("mock-detailAddress")
                        .useYn("Y")
                        .build();
        final var SAME_COUNT = 10;
        var sameMemberList =
                IntStream.range(1, SAME_COUNT + 1)
                        .mapToObj(
                                number ->
                                        Member.builder()
                                                .email(initEmlByNumber.apply(number))
                                                .password(sameSaveMember.getPassword())
                                                .name(sameSaveMember.getName())
                                                .phoneNo(sameSaveMember.getPhoneNo())
                                                .roadNameAddress(sameSaveMember.getRoadNameAddress())
                                                .detailAddress(sameSaveMember.getDetailAddress())
                                                .useYn(sameSaveMember.getUseYn())
                                                .build())
                        .collect(Collectors.toList());
        given(memberRepository.findAllByName(sameSaveMember.getName()))
                .willReturn(Optional.of(sameMemberList));
        var specimenIdx = new Random(System.currentTimeMillis()).nextInt(SAME_COUNT);
        var specimenMember = sameMemberList.get(specimenIdx);
        Function<String, String> specimenDataExp =
                key -> String.format("$.data.list[%d].%s", specimenIdx, key);

        // when
        mockMvc
                .perform(get("/member?name=" + sameSaveMember.getName()))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(MemberSelectListRspDto.class))))
                .andExpect(jsonPath(specimenDataExp.apply("email"), is(specimenMember.getEmail())))
                .andExpect(jsonPath(specimenDataExp.apply("password"), is(specimenMember.getPassword())))
                .andExpect(jsonPath(specimenDataExp.apply("name"), is(specimenMember.getName())))
                .andExpect(jsonPath(specimenDataExp.apply("phoneNo"), is(specimenMember.getPhoneNo())))
                .andExpect(
                        jsonPath(
                                specimenDataExp.apply("roadNameAddress"), is(specimenMember.getRoadNameAddress())))
                .andExpect(
                        jsonPath(specimenDataExp.apply("detailAddress"), is(specimenMember.getDetailAddress())))
                .andExpect(jsonPath(specimenDataExp.apply("useYn"), is(specimenMember.getUseYn())))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(12)
    @Test
    public void update_return_MemberUpdateRspDto() throws Exception {
        // given
        var member =
                Member.builder()
                        .email("valid@email.com")
                        .password("pswd")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("roadNameAddress")
                        .detailAddress("detailAddress")
                        .useYn("Y")
                        .build();
        given(memberRepository.findByIdAndUseYn(any(), any())).willReturn(Optional.of(member));

        var reqDto =
                MemberUpdateReqDto.builder()
                        .password("updPswd")
                        .phoneNo("11111111111")
                        .roadNameAddress("update roadNmAddr")
                        .detailAddress("update detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        var updatedMember =
                Member.builder()
                        .password(reqDto.getPassword())
                        .phoneNo(reqDto.getPhoneNo())
                        .roadNameAddress(reqDto.getRoadNameAddress())
                        .detailAddress(reqDto.getDetailAddress())
                        .build();
        given(memberRepository.save(any())).willReturn(updatedMember);
        final var ANY_MBR_ID = 1;

        // when
        mockMvc
                .perform(
                        post("/member/" + ANY_MBR_ID)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(reqJson))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(MemberUpdateRspDto.class))))
                .andExpect(jsonPath("$.data.password", is(reqDto.getPassword())))
                .andExpect(jsonPath("$.data.phoneNo", is(reqDto.getPhoneNo())))
                .andExpect(jsonPath("$.data.roadNameAddress", is(reqDto.getRoadNameAddress())))
                .andExpect(jsonPath("$.data.detailAddress", is(reqDto.getDetailAddress())))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(13)
    @Test
    public void delete_return_MemberDeleteRspDto() throws Exception {
        // given
        var member =
                Member.builder()
                        .email("valid@email.com")
                        .password("pswd")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("roadNameAddress")
                        .detailAddress("detailAddress")
                        .useYn("Y")
                        .build();
        given(memberRepository.findByIdAndUseYn(any(), any())).willReturn(Optional.of(member));

        var delelteMember =
                Member.builder()
                        .email("valid@email.com")
                        .password("pswd")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("roadNameAddress")
                        .detailAddress("detailAddress")
                        .useYn("N")
                        .build();
        given(memberRepository.save(any())).willReturn(delelteMember);
        final var ANY_MBR_ID = 1L;

        // when
        mockMvc
                .perform(delete("/member/" + ANY_MBR_ID))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(MemberDeleteRspDto.class))))
                .andExpect(jsonPath("$.data.useYn", is("N")))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(14)
    @Test
    public void delete_with_not_found_member_throw_EntityNotFoundException() throws Exception {
        // given
        given(memberRepository.findByIdAndUseYn(any(), any())).willReturn(Optional.empty());
        final var NOT_FOUND_MBR_ID = -1L;

        // when
        mockMvc
                .perform(delete("/member/" + NOT_FOUND_MBR_ID))
                // then
                .andExpect(status().isNotFound())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(EntityNotFoundException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }
}
