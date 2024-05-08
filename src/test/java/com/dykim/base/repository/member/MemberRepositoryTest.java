package com.dykim.base.repository.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dykim.base.advice.sample.exception.SampleAuditorAwareException;
import com.dykim.base.config.entity.BaseAuditorAware;
import com.dykim.base.dto.member.MemberUpdateReqDto;
import com.dykim.base.entity.member.Member;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("internal")
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    @Autowired BaseAuditorAware baseAuditorAware;

    @AfterEach
    public void clearAllMember() {
        // @Transactional 활용한 테스트케이스가 추후 생길 수 있기 때문에
        // @Transactional 로 케이스 별 rollback 대신 데이터 삭제로 케이스 별 영향도를 없앤다.
        // 스프링 가이드에 따르면 온전히 동작하지 않을수도 있다고 한다.(근거가 부족하여 공통처리로 사용함)
        memberRepository.deleteAll();
    }

    @Order(1)
    @Test
    public void save_return_Member() {
        // given
        var member =
                Member.builder()
                        .email("valid@email.com")
                        .password("pswd")
                        .name("name")
                        .phoneNo("phoneNo")
                        .roadNameAddress("roadNameAddress")
                        .detailAddress("detailAddress")
                        .useYn("Y")
                        .build();

        // when
        var insertedMember = memberRepository.save(member);

        // then
        assertThat(insertedMember.getId()).isNotNull();
        assertThat(insertedMember.getEmail()).isEqualTo(member.getEmail());
        assertThat(insertedMember.getPassword()).isEqualTo(member.getPassword());
        assertThat(insertedMember.getName()).isEqualTo(member.getName());
        assertThat(insertedMember.getPhoneNo()).isEqualTo(member.getPhoneNo());
        assertThat(insertedMember.getRoadNameAddress()).isEqualTo(member.getRoadNameAddress());
        assertThat(insertedMember.getDetailAddress()).isEqualTo(member.getDetailAddress());
        assertThat(insertedMember.getUseYn()).isEqualTo(member.getUseYn());
        assertBaseEntitySave(insertedMember);
    }

    @Order(2)
    @Test
    public void save_with_duplicate_mbrEml_throw_DataIntegrityViolationException() {
        // setup
        var mockMember =
                memberRepository.save(
                        Member.builder()
                                .email("mock@email.com")
                                .password("mock-pswd")
                                .name("mock-name")
                                .phoneNo("01234567890")
                                .roadNameAddress("mock-roadNameAddress")
                                .detailAddress("mock-detailAddress")
                                .useYn("Y")
                                .build());

        // given
        var member =
                Member.builder()
                        .email(mockMember.getEmail())
                        .password("pswd")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("roadNameAddress")
                        .detailAddress("detailAddress")
                        .useYn("Y")
                        .build();

        // when-then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.save(member));
    }

    @Order(3)
    @Test
    public void save_with_empty_require_parameter_throw_ConstraintViolationException() {
        // given
        var member =
                Member.builder()
                        .email("valid@email.com")
                        //                .password("pswd") // nullable=false 테스트용 주석
                        .name("name")
                        .phoneNo("phoneNo")
                        .roadNameAddress("roadNameAddress")
                        .detailAddress("detailAddress")
                        .useYn("Y")
                        .build();

        // when-then
        assertThrows(ConstraintViolationException.class, () -> memberRepository.save(member));
    }

    @Order(4)
    @Test
    public void save_with_over_length_parameter_throw_DataIntegrityViolationException() {
        // given
        var member =
                Member.builder()
                        .email("valid@email.com")
                        .password("pswd")
                        .name("name")
                        .phoneNo("012345678901")
                        .roadNameAddress("roadNameAddress")
                        .detailAddress("detailAddress")
                        .useYn("Y")
                        .build();

        // when-then
        assertThrows(DataIntegrityViolationException.class, () -> memberRepository.save(member));
    }

    @Order(5)
    @Test
    public void findById_return_member() {
        // given
        var mockMember =
                memberRepository.save(
                        Member.builder()
                                .email("mock@email.com")
                                .password("mock-pswd")
                                .name("mock-name")
                                .phoneNo("01234567890")
                                .roadNameAddress("mock-roadNameAddress")
                                .detailAddress("mock-detailAddress")
                                .useYn("Y")
                                .build());

        // when
        var memberOps = memberRepository.findById(mockMember.getId());

        // then
        assertThat(memberOps.isPresent()).isTrue();

        var member = memberOps.get();
        assertThat(member.getEmail()).isEqualTo(mockMember.getEmail());
        assertThat(member.getPassword()).isEqualTo(mockMember.getPassword());
        assertThat(member.getName()).isEqualTo(mockMember.getName());
        assertThat(member.getPhoneNo()).isEqualTo(mockMember.getPhoneNo());
        assertThat(member.getRoadNameAddress()).isEqualTo(mockMember.getRoadNameAddress());
        assertThat(member.getDetailAddress()).isEqualTo(mockMember.getDetailAddress());
        assertThat(member.getUseYn()).isEqualTo(mockMember.getUseYn());
        assertBaseEntitySelect(member);
    }

    @Order(6)
    @Test
    public void findAllByname_return_member_list() {
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
        IntStream.range(1, SAME_COUNT + 1)
                .forEach(
                        number ->
                                memberRepository.save(
                                        Member.builder()
                                                .email(initEmlByNumber.apply(number))
                                                .password(sameSaveMember.getPassword())
                                                .name(sameSaveMember.getName())
                                                .phoneNo(sameSaveMember.getPhoneNo())
                                                .roadNameAddress(sameSaveMember.getRoadNameAddress())
                                                .detailAddress(sameSaveMember.getDetailAddress())
                                                .useYn(sameSaveMember.getUseYn())
                                                .build()));

        // when
        var memberListOps = memberRepository.findAllByName(sameSaveMember.getName());

        // then
        assertThat(memberListOps.isPresent()).isTrue();

        var memberList = memberListOps.get();
        assertThat(memberList.size()).isEqualTo(SAME_COUNT);

        var specimenIdx = new Random(System.currentTimeMillis()).nextInt(SAME_COUNT);
        var specimenMember = memberList.get(specimenIdx);
        assertThat(specimenMember.getEmail()).isEqualTo(initEmlByNumber.apply(specimenIdx + 1));
        assertThat(specimenMember.getPassword()).isEqualTo(sameSaveMember.getPassword());
        assertThat(specimenMember.getName()).isEqualTo(sameSaveMember.getName());
        assertThat(specimenMember.getPhoneNo()).isEqualTo(sameSaveMember.getPhoneNo());
        assertThat(specimenMember.getRoadNameAddress()).isEqualTo(sameSaveMember.getRoadNameAddress());
        assertThat(specimenMember.getDetailAddress()).isEqualTo(sameSaveMember.getDetailAddress());
        assertThat(specimenMember.getUseYn()).isEqualTo(sameSaveMember.getUseYn());
        assertBaseEntitySelect(specimenMember);
    }

    @Order(7)
    @Test
    public void update_to_save_return_MemberUpdateRspDto() {
        // setup
        var mockMember =
                memberRepository.save(
                        Member.builder()
                                .email("mock@email.com")
                                .password("mock-pswd")
                                .name("mock-name")
                                .phoneNo("01234567890")
                                .roadNameAddress("mock-roadNameAddress")
                                .detailAddress("mock-detailAddress")
                                .useYn("Y")
                                .build());

        // given
        var reqDto =
                MemberUpdateReqDto.builder()
                        .password("udpate pswd")
                        .phoneNo("11111111111")
                        .roadNameAddress("update road address")
                        .detailAddress("update detail address")
                        .build();

        // when
        var member = memberRepository.save(mockMember.update(reqDto));

        // then
        assertThat(member.getPassword()).isEqualTo(reqDto.getPassword());
        assertThat(member.getPhoneNo()).isEqualTo(reqDto.getPhoneNo());
        assertThat(member.getRoadNameAddress()).isEqualTo(reqDto.getRoadNameAddress());
        assertThat(member.getDetailAddress()).isEqualTo(reqDto.getDetailAddress());
        assertBaseEntityUpdate(member);

        // cross-check select
        var selectMemberOps = memberRepository.findById(member.getId());
        assertThat(selectMemberOps.isPresent()).isTrue();

        var selectMember = selectMemberOps.get();
        assertThat(selectMember.getPassword()).isEqualTo(reqDto.getPassword());
        assertThat(selectMember.getPhoneNo()).isEqualTo(reqDto.getPhoneNo());
        assertThat(selectMember.getRoadNameAddress()).isEqualTo(reqDto.getRoadNameAddress());
        assertThat(selectMember.getDetailAddress()).isEqualTo(reqDto.getDetailAddress());
        assertBaseEntitySelect(selectMember);
    }

    @Order(8)
    @Test
    public void delete_to_save_return_MemberDeleteRspDto() {
        // setup
        var mockMember =
                memberRepository.save(
                        Member.builder()
                                .email("mock@email.com")
                                .password("mock-pswd")
                                .name("mock-name")
                                .phoneNo("01234567890")
                                .roadNameAddress("mock-roadNameAddress")
                                .detailAddress("mock-detailAddress")
                                .useYn("Y")
                                .build());

        // given
        mockMember.delete();

        // when
        var member = memberRepository.save(mockMember);

        // then
        assertThat(member.getId()).isEqualTo(mockMember.getId());
        assertThat(member.getUseYn()).isEqualTo("N");
        assertBaseEntityDelete(member);

        // cross-check select
        var selectMemberOps = memberRepository.findById(member.getId());
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
        var auditorId =
                baseAuditorAware
                        .getCurrentAuditor()
                        .orElseThrow(
                                () -> new SampleAuditorAwareException("Not found sessionUserId from Auditor"));

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
