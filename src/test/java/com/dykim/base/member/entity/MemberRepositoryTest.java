package com.dykim.base.member.entity;

import com.dykim.base.advice.hello.exception.HelloAuditorAwareException;
import com.dykim.base.config.BaseAuditorAware;
import com.dykim.base.member.dto.MemberUpdateReqDto;
import org.junit.jupiter.api.*;
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

    @AfterEach
    public void clearAllMember() {
        // 매 케이스 종료 후 데이터를 삭제하여 다른 케이스에 영향이 없도록 한다.
        // 스프링 가이드에 따르면 온전히 동작하지 않을수도 있다고 한다.(근거가 부족하여 공통처리로 사용함)
        memberRepository.deleteAll();
    }

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

    @Order(5)
    @Test
    public void findById_return_member() {
        // given
        var mockMember = memberRepository.save(Member.builder()
                .mbrEml("mock@email.com")
                .mbrPswd("mock-pswd")
                .mbrNm("mock-mbrNm")
                .mbrTelno("01234567890")
                .mbrRoadNmAddr("mock-mbrRoadNmAddr")
                .mbrDaddr("mock-mbrDaddr")
                .useYn("Y")
                .build());

        // when
        var memberOps = memberRepository.findById(mockMember.getMbrId());

        // then
        assertThat(memberOps.isPresent()).isTrue();

        var member = memberOps.get();
        assertThat(member.getMbrEml()).isEqualTo(mockMember.getMbrEml());
        assertThat(member.getMbrPswd()).isEqualTo(mockMember.getMbrPswd());
        assertThat(member.getMbrNm()).isEqualTo(mockMember.getMbrNm());
        assertThat(member.getMbrTelno()).isEqualTo(mockMember.getMbrTelno());
        assertThat(member.getMbrRoadNmAddr()).isEqualTo(mockMember.getMbrRoadNmAddr());
        assertThat(member.getMbrDaddr()).isEqualTo(mockMember.getMbrDaddr());
        assertThat(member.getUseYn()).isEqualTo(mockMember.getUseYn());
        assertBaseEntitySelect(member);
    }

    @Order(6)
    @Test
    public void update_to_save_return_MemberUpdateRspDto() {
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
        var reqDto = MemberUpdateReqDto.builder()
                .mbrPswd("udpate pswd")
                .mbrTelno("11111111111")
                .mbrRoadNmAddr("update road address")
                .mbrDaddr("update detail address")
                .build();

        // when
        var member = memberRepository.save(mockMember.update(reqDto));

        // then
        assertThat(member.getMbrPswd()).isEqualTo(reqDto.getMbrPswd());
        assertThat(member.getMbrTelno()).isEqualTo(reqDto.getMbrTelno());
        assertThat(member.getMbrRoadNmAddr()).isEqualTo(reqDto.getMbrRoadNmAddr());
        assertThat(member.getMbrDaddr()).isEqualTo(reqDto.getMbrDaddr());
        assertBaseEntityUpdate(member);

        // cross-check select
        var selectMemberOps = memberRepository.findById(member.getMbrId());
        assertThat(selectMemberOps.isPresent()).isTrue();

        var selectMember = selectMemberOps.get();
        assertThat(selectMember.getMbrPswd()).isEqualTo(reqDto.getMbrPswd());
        assertThat(selectMember.getMbrTelno()).isEqualTo(reqDto.getMbrTelno());
        assertThat(selectMember.getMbrRoadNmAddr()).isEqualTo(reqDto.getMbrRoadNmAddr());
        assertThat(selectMember.getMbrDaddr()).isEqualTo(reqDto.getMbrDaddr());
        assertBaseEntitySelect(selectMember);
    }

    @Order(7)
    @Test
    public void delete_to_save_return_MemberDeleteRspDto() {
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
        mockMember.delete();

        // when
        var member = memberRepository.save(mockMember);

        // then
        assertThat(member.getMbrId()).isEqualTo(mockMember.getMbrId());
        assertThat(member.getUseYn()).isEqualTo("N");
        assertBaseEntityDelete(member);

        // cross-check select
        var selectMemberOps = memberRepository.findById(member.getMbrId());
        assertThat(selectMemberOps.isPresent()).isTrue();

        var selectMember = selectMemberOps.get();
        assertThat(selectMember.getUseYn()).isEqualTo("N");
        assertBaseEntitySelect(selectMember);
    }

    private void assertBaseEntitySave(Member member) {
        assertBaseEntity(member, "SAVE");
    }

    private void assertBaseEntitySelect(Member member) {
        assertBaseEntity(member, "SELECT");
    }

    private void assertBaseEntityUpdate(Member member) {
        assertBaseEntity(member, "UPDATE");
    }

    private void assertBaseEntityDelete(Member member) {
        assertBaseEntity(member, "DELETE");
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
            case "SELECT":
                assertThat(member.getMdfcnDt()).isAfterOrEqualTo(member.getRegDt());
                break;
            case "UPDATE":
            case "DELETE":
                assertThat(member.getMdfcnId()).isEqualTo(auditorId);
                assertThat(member.getMdfcnDt()).isAfterOrEqualTo(member.getRegDt());
                break;
            default:
                assert false;
        }
    }

}
