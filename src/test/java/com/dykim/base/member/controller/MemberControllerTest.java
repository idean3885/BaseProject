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
                        .mbrEml("email@base.com")
                        .mbrPswd("pswd")
                        .mbrNm("name")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("road address")
                        .mbrDaddr("detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        var member =
                Member.builder()
                        .mbrEml(reqDto.getMbrEml())
                        .mbrPswd(reqDto.getMbrPswd())
                        .mbrNm(reqDto.getMbrNm())
                        .mbrTelno(reqDto.getMbrTelno())
                        .mbrRoadNmAddr(reqDto.getMbrRoadNmAddr())
                        .mbrDaddr(reqDto.getMbrDaddr())
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
                .andExpect(jsonPath("$.data.mbrEml", is(reqDto.getMbrEml())))
                .andExpect(jsonPath("$.data.mbrPswd", is(reqDto.getMbrPswd())))
                .andExpect(jsonPath("$.data.mbrNm", is(reqDto.getMbrNm())))
                .andExpect(jsonPath("$.data.mbrTelno", is(reqDto.getMbrTelno())))
                .andExpect(jsonPath("$.data.mbrRoadNmAddr", is(reqDto.getMbrRoadNmAddr())))
                .andExpect(jsonPath("$.data.mbrDaddr", is(reqDto.getMbrDaddr())))
                .andExpect(jsonPath("$.data.useYn", is("Y")))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(2)
    @Test
    public void insert_with_invalid_mbrEml_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto =
                MemberInsertReqDto.builder()
                        .mbrEml("invalid.email.com")
                        .mbrPswd("pswd")
                        .mbrNm("name")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("road address")
                        .mbrDaddr("detail address")
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
                        .mbrEml("valid@email.com")
                        .mbrNm("name")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("road address")
                        .mbrDaddr("detail address")
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
                        .mbrEml("valid@email.com")
                        .mbrPswd("  ")
                        .mbrNm("name")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("road address")
                        .mbrDaddr("detail address")
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
                        .mbrEml("valid@email.com")
                        .mbrPswd("pswd")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("road address")
                        .mbrDaddr("detail address")
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
                        .mbrEml("valid@email.com")
                        .mbrPswd("pswd")
                        .mbrNm("  ")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("road address")
                        .mbrDaddr("detail address")
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
                        .mbrEml("valid@email.com")
                        .mbrPswd("pswd")
                        .mbrNm("name")
                        .mbrTelno("012345678901")
                        .mbrRoadNmAddr("road address")
                        .mbrDaddr("detail address")
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
                        .mbrEml("valid@email.com")
                        .mbrPswd("pswd")
                        .mbrNm("name")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("road address")
                        .mbrDaddr("detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        given(memberRepository.existsByMbrEmlAndUseYn(any(), any())).willReturn(Optional.of(true));

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
                        .mbrEml("valid@email.com")
                        .mbrPswd("pswd")
                        .mbrNm("mbrNm")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("mbrRoadNmAddr")
                        .mbrDaddr("mbrDaddr")
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
                .andExpect(jsonPath("$.data.mbrEml", is(member.getMbrEml())))
                .andExpect(jsonPath("$.data.mbrPswd", is(member.getMbrPswd())))
                .andExpect(jsonPath("$.data.mbrNm", is(member.getMbrNm())))
                .andExpect(jsonPath("$.data.mbrTelno", is(member.getMbrTelno())))
                .andExpect(jsonPath("$.data.mbrRoadNmAddr", is(member.getMbrRoadNmAddr())))
                .andExpect(jsonPath("$.data.mbrDaddr", is(member.getMbrDaddr())))
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
                        .mbrPswd("mock-pswd")
                        .mbrNm("sameName")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("mock-mbrRoadNmAddr")
                        .mbrDaddr("mock-mbrDaddr")
                        .useYn("Y")
                        .build();
        final var SAME_COUNT = 10;
        var sameMemberList =
                IntStream.range(1, SAME_COUNT + 1)
                        .mapToObj(
                                number ->
                                        Member.builder()
                                                .mbrEml(initEmlByNumber.apply(number))
                                                .mbrPswd(sameSaveMember.getMbrPswd())
                                                .mbrNm(sameSaveMember.getMbrNm())
                                                .mbrTelno(sameSaveMember.getMbrTelno())
                                                .mbrRoadNmAddr(sameSaveMember.getMbrRoadNmAddr())
                                                .mbrDaddr(sameSaveMember.getMbrDaddr())
                                                .useYn(sameSaveMember.getUseYn())
                                                .build())
                        .collect(Collectors.toList());
        given(memberRepository.findAllByMbrNm(sameSaveMember.getMbrNm()))
                .willReturn(Optional.of(sameMemberList));
        var specimenIdx = new Random(System.currentTimeMillis()).nextInt(SAME_COUNT);
        var specimenMember = sameMemberList.get(specimenIdx);
        Function<String, String> specimenDataExp =
                key -> String.format("$.data.list[%d].%s", specimenIdx, key);

        // when
        mockMvc
                .perform(get("/member?mbrNm=" + sameSaveMember.getMbrNm()))
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(TestAdviceUtil.getRspName(MemberSelectListRspDto.class))))
                .andExpect(jsonPath(specimenDataExp.apply("mbrEml"), is(specimenMember.getMbrEml())))
                .andExpect(jsonPath(specimenDataExp.apply("mbrPswd"), is(specimenMember.getMbrPswd())))
                .andExpect(jsonPath(specimenDataExp.apply("mbrNm"), is(specimenMember.getMbrNm())))
                .andExpect(jsonPath(specimenDataExp.apply("mbrTelno"), is(specimenMember.getMbrTelno())))
                .andExpect(
                        jsonPath(specimenDataExp.apply("mbrRoadNmAddr"), is(specimenMember.getMbrRoadNmAddr())))
                .andExpect(jsonPath(specimenDataExp.apply("mbrDaddr"), is(specimenMember.getMbrDaddr())))
                .andExpect(jsonPath(specimenDataExp.apply("useYn"), is(specimenMember.getUseYn())))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(12)
    @Test
    public void update_return_MemberUpdateRspDto() throws Exception {
        // given
        var member =
                Member.builder()
                        .mbrEml("valid@email.com")
                        .mbrPswd("pswd")
                        .mbrNm("mbrNm")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("mbrRoadNmAddr")
                        .mbrDaddr("mbrDaddr")
                        .useYn("Y")
                        .build();
        given(memberRepository.findByMbrIdAndUseYn(any(), any())).willReturn(Optional.of(member));

        var reqDto =
                MemberUpdateReqDto.builder()
                        .mbrPswd("updPswd")
                        .mbrTelno("11111111111")
                        .mbrRoadNmAddr("update roadNmAddr")
                        .mbrDaddr("update detail address")
                        .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        var updatedMember =
                Member.builder()
                        .mbrPswd(reqDto.getMbrPswd())
                        .mbrTelno(reqDto.getMbrTelno())
                        .mbrRoadNmAddr(reqDto.getMbrRoadNmAddr())
                        .mbrDaddr(reqDto.getMbrDaddr())
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
                .andExpect(jsonPath("$.data.mbrPswd", is(reqDto.getMbrPswd())))
                .andExpect(jsonPath("$.data.mbrTelno", is(reqDto.getMbrTelno())))
                .andExpect(jsonPath("$.data.mbrRoadNmAddr", is(reqDto.getMbrRoadNmAddr())))
                .andExpect(jsonPath("$.data.mbrDaddr", is(reqDto.getMbrDaddr())))
                .andDo(TestAdviceUtil::printRspDto);
    }

    @Order(13)
    @Test
    public void delete_return_MemberDeleteRspDto() throws Exception {
        // given
        var member =
                Member.builder()
                        .mbrEml("valid@email.com")
                        .mbrPswd("pswd")
                        .mbrNm("mbrNm")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("mbrRoadNmAddr")
                        .mbrDaddr("mbrDaddr")
                        .useYn("Y")
                        .build();
        given(memberRepository.findByMbrIdAndUseYn(any(), any())).willReturn(Optional.of(member));

        var delelteMember =
                Member.builder()
                        .mbrEml("valid@email.com")
                        .mbrPswd("pswd")
                        .mbrNm("mbrNm")
                        .mbrTelno("01234567890")
                        .mbrRoadNmAddr("mbrRoadNmAddr")
                        .mbrDaddr("mbrDaddr")
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
        given(memberRepository.findByMbrIdAndUseYn(any(), any())).willReturn(Optional.empty());
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
