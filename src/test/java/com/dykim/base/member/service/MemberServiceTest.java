package com.dykim.base.member.service;

import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.member.dto.MemberInsertReqDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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
    public void insert_return_MemberInsertRspDto() {
        // given
        var reqDto = MemberInsertReqDto.builder()
                .mbrEml("email@base.com")
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

}
