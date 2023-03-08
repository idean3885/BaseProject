package com.dykim.base.member.entity;

import com.dykim.base.advice.hello.exception.HelloAuditorAwareException;
import com.dykim.base.config.BaseAuditorAware;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BaseAuditorAware baseAuditorAware;

    @Order(1)
    @Test
    public void save_return_Member() {
        // given
        var member = Member.builder()
                .mbrEml("valid@email.com")
                .mbrPswd("pswd")
                .mbrNm("mbrNm")
                .mbrTelno("mbrTelno")
                .mbrRoadNmAddr("mbrRoadNmAddr")
                .mbrDaddr("mbrDaddr")
                .useYn("Y")
                .build();

        // when
        var insertedMember = memberRepository.save(member);

        // then
        assertThat(insertedMember.getMbrId()).isNotNull();
        assertThat(insertedMember.getMbrEml()).isEqualTo(member.getMbrEml());
        assertThat(insertedMember.getMbrPswd()).isEqualTo(member.getMbrPswd());
        assertThat(insertedMember.getMbrNm()).isEqualTo(member.getMbrNm());
        assertThat(insertedMember.getMbrTelno()).isEqualTo(member.getMbrTelno());
        assertThat(insertedMember.getMbrRoadNmAddr()).isEqualTo(member.getMbrRoadNmAddr());
        assertThat(insertedMember.getMbrDaddr()).isEqualTo(member.getMbrDaddr());
        assertThat(insertedMember.getUseYn()).isEqualTo(member.getUseYn());
        assertBaseEntitySave(insertedMember);
    }

    @Order(2)
    @Test
    public void save_with_duplicate_mbrEml_throw_DataIntegrityViolationException() {
        // setup
        var mockMember = memberRepository.save(Member.builder()
                .mbrEml("mock@email.com")
                .mbrPswd("mock-pswd")
                .mbrNm("mock-mbrNm")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("mock-mbrRoadNmAddr")
                .mbrDaddr("mock-mbrDaddr")
                .useYn("Y")
                .build());

        // given
        var member = Member.builder()
                .mbrEml(mockMember.getMbrEml())
                .mbrPswd("pswd")
                .mbrNm("mbrNm")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("mbrRoadNmAddr")
                .mbrDaddr("mbrDaddr")
                .useYn("Y")
                .build();

        // when-then
        assertThrows(DataIntegrityViolationException.class,
                () -> memberRepository.save(member)
        );
    }

    @Order(3)
    @Test
    public void save_with_empty_require_parameter_throw_ConstraintViolationException() {
        // given
        var member = Member.builder()
                .mbrEml("valid@email.com")
//                .mbrPswd("pswd") // nullable=false 테스트용 주석
                .mbrNm("mbrNm")
                .mbrTelno("mbrTelno")
                .mbrRoadNmAddr("mbrRoadNmAddr")
                .mbrDaddr("mbrDaddr")
                .useYn("Y")
                .build();

        // when-then
        assertThrows(ConstraintViolationException.class,
                () -> memberRepository.save(member)
        );
    }

    @Order(4)
    @Test
    public void save_with_over_length_parameter_throw_DataIntegrityViolationException() {
        // given
        var member = Member.builder()
                .mbrEml("valid@email.com")
                .mbrPswd("pswd")
                .mbrNm("mbrNm")
                .mbrTelno("012345678901")
                .mbrRoadNmAddr("mbrRoadNmAddr")
                .mbrDaddr("mbrDaddr")
                .useYn("Y")
                .build();

        // when-then
        assertThrows(DataIntegrityViolationException.class,
                () -> memberRepository.save(member)
        );
    }

    private void assertBaseEntitySave(Member member) {
        assertBaseEntity(member, "SAVE");
    }

    private void assertBaseEntity(Member member, String type) {
        // given
        var auditorId = baseAuditorAware.getCurrentAuditor()
                .orElseThrow(() -> new HelloAuditorAwareException("Not found sessionUserId from Auditor"));

        // then
        // 1) common
        assertThat(member.getRegDt()).isNotNull();
        assertThat(member.getMdfcnDt()).isNotNull();

        // 2) each case
        switch (type) {
            case "SAVE":
                assertThat(member.getMdfcnDt()).isEqualTo(member.getRegDt());
                assertThat(member.getRegId()).isEqualTo(auditorId);
                assertThat(member.getMdfcnId()).isEqualTo(auditorId);
                break;
            default:
                assert false;
        }
    }

}
