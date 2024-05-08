package com.dykim.base.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.advice.common.exception.EntityNotFoundException;
import com.dykim.base.dto.member.MemberInsertReqDto;
import com.dykim.base.dto.member.MemberUpdateReqDto;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 *
 * <h3>Member Service Test</h3>
 *
 * 회원 서비스 로직을 테스트한다.
 *
 * <p><b>참고</b>
 *
 * <pre>
 *  - 컨트롤러에서 reqDto 를 검증하기 때문에 서비스에서는 @Valid 를 사용하지 않는다.
 *  - 따라서 서비스 테스트코드에선 reqDto 검증을 생략한다.
 * </pre>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("internal")
@SpringBootTest
public class MemberServiceImplTest {

    private final MemberServiceImpl memberServiceImpl;

    @Autowired
    public MemberServiceImplTest(MemberServiceImpl memberServiceImpl) {
        this.memberServiceImpl = memberServiceImpl;
    }

    @Order(1)
    @Test
    @Transactional
    public void insert_member_return_MemberInsertRspDto() {
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

        // when
        var rspDto = memberServiceImpl.insert(reqDto);

        // then
        assertThat(rspDto.getId()).isNotNull();
        assertThat(rspDto.getEmail()).isEqualTo(reqDto.getEmail());
        assertThat(rspDto.getPassword()).isEqualTo(reqDto.getPassword());
        assertThat(rspDto.getName()).isEqualTo(reqDto.getName());
        assertThat(rspDto.getPhoneNo()).isEqualTo(reqDto.getPhoneNo());
        assertThat(rspDto.getRoadNameAddress()).isEqualTo(reqDto.getRoadNameAddress());
        assertThat(rspDto.getDetailAddress()).isEqualTo(reqDto.getDetailAddress());
        assertThat(rspDto.getUseYn()).isEqualTo("Y");
    }

    @Order(2)
    @Test
    @Transactional
    public void insert_member_exists_mbrEml_useYn_Y_throw_AlreadyExistsException() {
        // setup
        var mockMemberInsertRspDto =
                memberServiceImpl.insert(
                        MemberInsertReqDto.builder()
                                .email("mock@email.com")
                                .password("mock-pswd")
                                .name("mock-name")
                                .phoneNo("01234567890")
                                .roadNameAddress("mock-roadNameAddress")
                                .detailAddress("mock-detailAddress")
                                .build());

        // given
        var reqDto =
                MemberInsertReqDto.builder()
                        .email(mockMemberInsertRspDto.getEmail())
                        .password("pswd")
                        .name("name")
                        .phoneNo("01234567890")
                        .roadNameAddress("road address")
                        .detailAddress("detail address")
                        .build();

        // when-then
        assertThrows(AlreadyExistsException.class, () -> memberServiceImpl.insert(reqDto));
    }

    @Order(3)
    @Test
    @Transactional
    public void select_member_return_MemberSelectRspDto() {
        // setup
        var mockMemberInsertRspDto =
                memberServiceImpl.insert(
                        MemberInsertReqDto.builder()
                                .email("mock@email.com")
                                .password("mock-pswd")
                                .name("mock-name")
                                .phoneNo("01234567890")
                                .roadNameAddress("mock-roadNameAddress")
                                .detailAddress("mock-detailAddress")
                                .build());

        // given
        var mbrId = mockMemberInsertRspDto.getId();

        // when
        var rspDto = memberServiceImpl.select(mbrId);

        // then
        assertThat(rspDto.getMbrId()).isEqualTo(mbrId);
        assertThat(rspDto.getEmail()).isEqualTo(mockMemberInsertRspDto.getEmail());
        assertThat(rspDto.getPassword()).isEqualTo(mockMemberInsertRspDto.getPassword());
        assertThat(rspDto.getName()).isEqualTo(mockMemberInsertRspDto.getName());
        assertThat(rspDto.getPhoneNo()).isEqualTo(mockMemberInsertRspDto.getPhoneNo());
        assertThat(rspDto.getRoadNameAddress()).isEqualTo(mockMemberInsertRspDto.getRoadNameAddress());
        assertThat(rspDto.getDetailAddress()).isEqualTo(mockMemberInsertRspDto.getDetailAddress());
        assertThat(rspDto.getUseYn()).isEqualTo("Y");
    }

    @Order(4)
    @Test
    @Transactional
    public void selectList_member_return_MemberSelectListRspDto() {
        // given
        final IntFunction<String> initEmlByNumber = number -> String.format("mock%d@email.com", number);
        var sameInsertReqDto =
                MemberInsertReqDto.builder()
                        .password("mock-pswd")
                        .name("sameName")
                        .phoneNo("01234567890")
                        .roadNameAddress("mock-roadNameAddress")
                        .detailAddress("mock-detailAddress")
                        .build();
        final var SAME_COUNT = 10;
        IntStream.range(1, SAME_COUNT + 1)
                .forEach(
                        number ->
                                memberServiceImpl.insert(
                                        MemberInsertReqDto.builder()
                                                .email(initEmlByNumber.apply(number))
                                                .password(sameInsertReqDto.getPassword())
                                                .name(sameInsertReqDto.getName())
                                                .phoneNo(sameInsertReqDto.getPhoneNo())
                                                .roadNameAddress(sameInsertReqDto.getRoadNameAddress())
                                                .detailAddress(sameInsertReqDto.getDetailAddress())
                                                .build()));

        // when
        var rspDto = memberServiceImpl.selectList(sameInsertReqDto.getName());

        // then
        assertThat(rspDto.getList().size()).isEqualTo(SAME_COUNT);

        var specimenIdx = new Random(System.currentTimeMillis()).nextInt(SAME_COUNT);
        var specimenRspDto = rspDto.getList().get(specimenIdx);
        assertThat(specimenRspDto.getMbrId()).isNotNull();
        assertThat(specimenRspDto.getEmail()).isEqualTo(initEmlByNumber.apply(specimenIdx + 1));
        assertThat(specimenRspDto.getPassword()).isEqualTo(sameInsertReqDto.getPassword());
        assertThat(specimenRspDto.getName()).isEqualTo(sameInsertReqDto.getName());
        assertThat(specimenRspDto.getPhoneNo()).isEqualTo(sameInsertReqDto.getPhoneNo());
        assertThat(specimenRspDto.getRoadNameAddress())
                .isEqualTo(sameInsertReqDto.getRoadNameAddress());
        assertThat(specimenRspDto.getDetailAddress()).isEqualTo(sameInsertReqDto.getDetailAddress());
        assertThat(specimenRspDto.getUseYn()).isEqualTo("Y");
    }

    @Order(5)
    @Test
    public void selectList_member_with_not_exists_return_empty_ArrayList() {
        // given
        final var NOT_EXISTS_MBR_NM = "Not exist!";

        // when
        var rspDto = memberServiceImpl.selectList(NOT_EXISTS_MBR_NM);

        // then
        assertThat(rspDto.getList()).isNotNull();
        assertThat(rspDto.getList().isEmpty()).isTrue();
    }

    @Order(6)
    @Test
    @Transactional
    public void update_member_return_MemberUpdateRspDto() {
        // setup
        var mockMemberInsertRspDto =
                memberServiceImpl.insert(
                        MemberInsertReqDto.builder()
                                .email("mock@email.com")
                                .password("mock-pswd")
                                .name("mock-name")
                                .phoneNo("01234567890")
                                .roadNameAddress("mock-roadNameAddress")
                                .detailAddress("mock-detailAddress")
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
        var rspDto = memberServiceImpl.update(mockMemberInsertRspDto.getId(), reqDto);

        // then
        assertThat(rspDto.getPassword()).isEqualTo(reqDto.getPassword());
        assertThat(rspDto.getPhoneNo()).isEqualTo(reqDto.getPhoneNo());
        assertThat(rspDto.getRoadNameAddress()).isEqualTo(reqDto.getRoadNameAddress());
        assertThat(rspDto.getDetailAddress()).isEqualTo(reqDto.getDetailAddress());
    }

    @Order(7)
    @Test
    @Transactional
    public void update_with_not_exist_member_throw_EntityNotFoundException() {
        // given
        var reqDto =
                MemberUpdateReqDto.builder()
                        .password("udpate pswd")
                        .phoneNo("11111111111")
                        .roadNameAddress("update road address")
                        .detailAddress("update detail address")
                        .build();
        final var NOT_EXIST_MBR_ID = -1L;

        // when-then
        assertThrows(
                EntityNotFoundException.class, () -> memberServiceImpl.update(NOT_EXIST_MBR_ID, reqDto));
    }

    @Order(8)
    @Test
    @Transactional
    public void delete_member_return_MemberDeleteRspDto() {
        // setup
        var mockMemberInsertRspDto =
                memberServiceImpl.insert(
                        MemberInsertReqDto.builder()
                                .email("mock@email.com")
                                .password("mock-pswd")
                                .name("mock-name")
                                .phoneNo("01234567890")
                                .roadNameAddress("mock-roadNameAddress")
                                .detailAddress("mock-detailAddress")
                                .build());

        // given
        var mbrId = mockMemberInsertRspDto.getId();

        // when
        var rspDto = memberServiceImpl.delete(mbrId);

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
        assertThrows(EntityNotFoundException.class, () -> memberServiceImpl.delete(NOT_EXIST_MBR_ID));
    }
}
