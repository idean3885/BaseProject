package com.dykim.base.member.service;

import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.advice.common.exception.EntityNotFoundException;
import com.dykim.base.member.dto.MemberInsertReqDto;
import com.dykim.base.member.dto.MemberUpdateReqDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * <h3>Member Service Test</h3>
 * 회원 서비스 로직을 테스트한다.<p/>
 *
 * <b>참고</b>
 * <pre>
 *  - 컨트롤러에서 reqDto 를 검증하기 때문에 서비스에서는 @Valid 를 사용하지 않는다.
 *  - 따라서 서비스 테스트코드에선 reqDto 검증을 생략한다.
 * </pre>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest
public class MemberServiceTest {

    private final MemberService memberService;

    @Autowired
    public MemberServiceTest(MemberService memberService) {
        this.memberService = memberService;
    }

    @Order(1)
    @Test
    @Transactional
    public void insert_member_return_MemberInsertRspDto() {
        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml("valid@email.com")
                .mbrPswd("pswd")
                .mbrNm("name")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("road address")
                .mbrDaddr("detail address")
                .build();

        // when
        var rspDto = memberService.insert(reqDto);

        // then
        assertThat(rspDto.getMbrId()).isNotNull();
        assertThat(rspDto.getMbrEml()).isEqualTo(reqDto.getMbrEml());
        assertThat(rspDto.getMbrPswd()).isEqualTo(reqDto.getMbrPswd());
        assertThat(rspDto.getMbrNm()).isEqualTo(reqDto.getMbrNm());
        assertThat(rspDto.getMbrTelno()).isEqualTo(reqDto.getMbrTelno());
        assertThat(rspDto.getMbrRoadNmAddr()).isEqualTo(reqDto.getMbrRoadNmAddr());
        assertThat(rspDto.getMbrDaddr()).isEqualTo(reqDto.getMbrDaddr());
        assertThat(rspDto.getUseYn()).isEqualTo("Y");
    }

    @Order(2)
    @Test
    @Transactional
    public void insert_member_exists_mbrEml_useYn_Y_throw_AlreadyExistsException() {
        // setup
        var mockMemberInsertRspDto = memberService.insert(MemberInsertReqDto.builder()
                .mbrEml("mock@email.com")
                .mbrPswd("mock-pswd")
                .mbrNm("mock-mbrNm")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("mock-mbrRoadNmAddr")
                .mbrDaddr("mock-mbrDaddr")
                .build());

        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml(mockMemberInsertRspDto.getMbrEml())
                .mbrPswd("pswd")
                .mbrNm("name")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("road address")
                .mbrDaddr("detail address")
                .build();

        // when-then
        assertThrows(AlreadyExistsException.class, () -> memberService.insert(reqDto));
    }


    @Order(3)
    @Test
    @Transactional
    public void select_member_return_MemberSelectRspDto() {
        // setup
        var mockMemberInsertRspDto = memberService.insert(MemberInsertReqDto.builder()
                .mbrEml("mock@email.com")
                .mbrPswd("mock-pswd")
                .mbrNm("mock-mbrNm")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("mock-mbrRoadNmAddr")
                .mbrDaddr("mock-mbrDaddr")
                .build());

        // given
        var mbrId = mockMemberInsertRspDto.getMbrId();

        // when
        var rspDto = memberService.select(mbrId);

        // then
        assertThat(rspDto.getMbrId()).isEqualTo(mbrId);
        assertThat(rspDto.getMbrEml()).isEqualTo(mockMemberInsertRspDto.getMbrEml());
        assertThat(rspDto.getMbrPswd()).isEqualTo(mockMemberInsertRspDto.getMbrPswd());
        assertThat(rspDto.getMbrNm()).isEqualTo(mockMemberInsertRspDto.getMbrNm());
        assertThat(rspDto.getMbrTelno()).isEqualTo(mockMemberInsertRspDto.getMbrTelno());
        assertThat(rspDto.getMbrRoadNmAddr()).isEqualTo(mockMemberInsertRspDto.getMbrRoadNmAddr());
        assertThat(rspDto.getMbrDaddr()).isEqualTo(mockMemberInsertRspDto.getMbrDaddr());
        assertThat(rspDto.getUseYn()).isEqualTo("Y");
    }

    @Order(4)
    @Test
    @Transactional
    public void selectList_member_return_MemberSelectListRspDto() {
        // given
        final IntFunction<String> initEmlByNumber = number -> String.format("mock%d@email.com", number);
        var sameInsertReqDto = MemberInsertReqDto.builder()
                .mbrPswd("mock-pswd")
                .mbrNm("sameName")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("mock-mbrRoadNmAddr")
                .mbrDaddr("mock-mbrDaddr")
                .build();
        final var SAME_COUNT = 10;
        IntStream.range(1, SAME_COUNT + 1).forEach(
                number -> memberService.insert(MemberInsertReqDto.builder()
                        .mbrEml(initEmlByNumber.apply(number))
                        .mbrPswd(sameInsertReqDto.getMbrPswd())
                        .mbrNm(sameInsertReqDto.getMbrNm())
                        .mbrTelno(sameInsertReqDto.getMbrTelno())
                        .mbrRoadNmAddr(sameInsertReqDto.getMbrRoadNmAddr())
                        .mbrDaddr(sameInsertReqDto.getMbrDaddr())
                        .build()));

        // when
        var rspDto = memberService.selectList(sameInsertReqDto.getMbrNm());

        // then
        assertThat(rspDto.getList().size()).isEqualTo(SAME_COUNT);

        var specimenIdx = new Random(System.currentTimeMillis()).nextInt(SAME_COUNT);
        var specimenRspDto = rspDto.getList().get(specimenIdx);
        assertThat(specimenRspDto.getMbrId()).isNotNull();
        assertThat(specimenRspDto.getMbrEml()).isEqualTo(initEmlByNumber.apply(specimenIdx + 1));
        assertThat(specimenRspDto.getMbrPswd()).isEqualTo(sameInsertReqDto.getMbrPswd());
        assertThat(specimenRspDto.getMbrNm()).isEqualTo(sameInsertReqDto.getMbrNm());
        assertThat(specimenRspDto.getMbrTelno()).isEqualTo(sameInsertReqDto.getMbrTelno());
        assertThat(specimenRspDto.getMbrRoadNmAddr()).isEqualTo(sameInsertReqDto.getMbrRoadNmAddr());
        assertThat(specimenRspDto.getMbrDaddr()).isEqualTo(sameInsertReqDto.getMbrDaddr());
        assertThat(specimenRspDto.getUseYn()).isEqualTo("Y");
    }

    @Order(5)
    @Test
    public void selectList_member_with_not_exists_return_empty_ArrayList() {
        // given
        final var NOT_EXISTS_MBR_NM = "Not exist!";

        // when
        var rspDto = memberService.selectList(NOT_EXISTS_MBR_NM);

        // then
        assertThat(rspDto.getList()).isNotNull();
        assertThat(rspDto.getList().isEmpty()).isTrue();
    }

    @Order(6)
    @Test
    @Transactional
    public void update_member_return_MemberUpdateRspDto() {
        // setup
        var mockMemberInsertRspDto = memberService.insert(MemberInsertReqDto.builder()
                .mbrEml("mock@email.com")
                .mbrPswd("mock-pswd")
                .mbrNm("mock-mbrNm")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("mock-mbrRoadNmAddr")
                .mbrDaddr("mock-mbrDaddr")
                .build());

        // given
        var reqDto = MemberUpdateReqDto.builder()
                .mbrPswd("udpate pswd")
                .mbrTelno("11111111111")
                .mbrRoadNmAddr("update road address")
                .mbrDaddr("update detail address")
                .build();

        // when
        var rspDto = memberService.update(mockMemberInsertRspDto.getMbrId(), reqDto);

        // then
        assertThat(rspDto.getMbrPswd()).isEqualTo(reqDto.getMbrPswd());
        assertThat(rspDto.getMbrTelno()).isEqualTo(reqDto.getMbrTelno());
        assertThat(rspDto.getMbrRoadNmAddr()).isEqualTo(reqDto.getMbrRoadNmAddr());
        assertThat(rspDto.getMbrDaddr()).isEqualTo(reqDto.getMbrDaddr());
    }

    @Order(7)
    @Test
    @Transactional
    public void update_with_not_exist_member_throw_EntityNotFoundException() {
        // given
        var reqDto = MemberUpdateReqDto.builder()
                .mbrPswd("udpate pswd")
                .mbrTelno("11111111111")
                .mbrRoadNmAddr("update road address")
                .mbrDaddr("update detail address")
                .build();
        final var NOT_EXIST_MBR_ID = -1L;

        // when-then
        assertThrows(EntityNotFoundException.class, () -> memberService.update(NOT_EXIST_MBR_ID, reqDto));
    }

    @Order(8)
    @Test
    @Transactional
    public void delete_member_return_MemberDeleteRspDto() {
        // setup
        var mockMemberInsertRspDto = memberService.insert(MemberInsertReqDto.builder()
                .mbrEml("mock@email.com")
                .mbrPswd("mock-pswd")
                .mbrNm("mock-mbrNm")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("mock-mbrRoadNmAddr")
                .mbrDaddr("mock-mbrDaddr")
                .build());

        // given
        var mbrId = mockMemberInsertRspDto.getMbrId();

        // when
        var rspDto = memberService.delete(mbrId);

        // then
        assertThat(rspDto.getUseYn()).isEqualTo("N");
    }

    @Order(9)
    @Test
    @Transactional
    public void delete_with_not_exist_member_throw_EntityNotFoundException() {
        // given
        final var NOT_EXIST_MBR_ID = -1L;

        // when-then
        assertThrows(EntityNotFoundException.class, () -> memberService.delete(NOT_EXIST_MBR_ID));
    }

}
