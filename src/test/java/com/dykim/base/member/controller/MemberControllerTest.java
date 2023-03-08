package com.dykim.base.member.controller;

import com.dykim.base.advice.common.CommonControllerAdvice;
import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.member.dto.MemberInsertReqDto;
import com.dykim.base.member.dto.MemberInsertRspDto;
import com.dykim.base.member.entity.Member;
import com.dykim.base.member.entity.MemberRepository;
import com.dykim.base.member.service.MemberService;
import com.dykim.base.util.TestAdviceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberControllerTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new MemberController(memberService))
                .setControllerAdvice(new CommonControllerAdvice())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Order(1)
    @Test
    public void insert_member_return_MemberInsertRspDto() throws Exception {
        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml("email@base.com")
                .mbrPswd("pswd")
                .mbrNm("name")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("road address")
                .mbrDaddr("detail address")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);
        var member = Member.builder()
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
        mockMvc.perform(put("/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
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
    public void insert_member_with_invalid_mbrEml_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml("invalid.email.com")
                .mbrPswd("pswd")
                .mbrNm("name")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("road address")
                .mbrDaddr("detail address")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc.perform(put("/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                        .isEqualTo(MethodArgumentNotValidException.class)
                )
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(3)
    @Test
    public void insert_member_with_null_mbrPswd_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml("valid@email.com")
                .mbrNm("name")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("road address")
                .mbrDaddr("detail address")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc.perform(put("/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                        .isEqualTo(MethodArgumentNotValidException.class)
                )
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(4)
    @Test
    public void insert_member_with_blank_mbrPswd_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml("valid@email.com")
                .mbrPswd("  ")
                .mbrNm("name")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("road address")
                .mbrDaddr("detail address")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc.perform(put("/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                        .isEqualTo(MethodArgumentNotValidException.class)
                )
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(5)
    @Test
    public void insert_member_with_null_mbrNm_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml("valid@email.com")
                .mbrPswd("pswd")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("road address")
                .mbrDaddr("detail address")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc.perform(put("/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                        .isEqualTo(MethodArgumentNotValidException.class)
                )
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(6)
    @Test
    public void insert_member_with_blank_mbrNm_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml("valid@email.com")
                .mbrPswd("pswd")
                .mbrNm("  ")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("road address")
                .mbrDaddr("detail address")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc.perform(put("/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                        .isEqualTo(MethodArgumentNotValidException.class)
                )
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(7)
    @Test
    public void insert_member_with_11_over_mbrTelno_throw_MethodArgumentNotValidException() throws Exception {
        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml("valid@email.com")
                .mbrPswd("pswd")
                .mbrNm("name")
                .mbrTelno("012345678901")
                .mbrRoadNmAddr("road address")
                .mbrDaddr("detail address")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reqDto);

        // when
        mockMvc.perform(put("/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                        .isEqualTo(MethodArgumentNotValidException.class)
                )
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

    @Order(8)
    @Test
    public void insert_member_exists_mbrEml_useYn_Y_throw_AlreadyExistsException() throws Exception {
        // given
        var reqDto = MemberInsertReqDto.builder()
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
        mockMvc.perform(put("/member")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson)
                )
                // then
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                        .isEqualTo(AlreadyExistsException.class)
                )
                .andDo(TestAdviceUtil::printExceptionMessage);
    }

}
