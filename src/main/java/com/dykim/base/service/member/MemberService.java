package com.dykim.base.service.member;

import com.dykim.base.advice.common.exception.EntityNotFoundException;
import com.dykim.base.dto.member.MemberDeleteRspDto;
import com.dykim.base.dto.member.MemberInsertReqDto;
import com.dykim.base.dto.member.MemberInsertRspDto;
import com.dykim.base.dto.member.MemberSelectListRspDto;
import com.dykim.base.dto.member.MemberSelectRspDto;
import com.dykim.base.dto.member.MemberUpdateReqDto;
import com.dykim.base.dto.member.MemberUpdateRspDto;

/**
 *
 *
 * <h3>Member service</h3>
 *
 * @author dongyoung.kim
 * @since 1.0
 */
public interface MemberService {

    /**
     *
     *
     * <h3>회원 추가</h3>
     *
     * @param reqDto {@link MemberInsertReqDto 회원 추가 요청 Dto}
     * @return {@link MemberInsertRspDto 회원 추가 응답 Dto}
     */
    MemberInsertRspDto insert(MemberInsertReqDto reqDto);

    /**
     *
     *
     * <h3>회원 조회</h3>
     *
     * <p>단일 회원 조회
     *
     * @param mbrId 회원ID
     * @return {@link MemberSelectRspDto 회원 조회 응답 Dto}
     */
    MemberSelectRspDto select(Long mbrId);

    /**
     *
     *
     * <h3>회원 목록 조회</h3>
     *
     * <p>조건에 맞는 회원 목록을 조회한다.
     *
     * <p>조건은 Equals 로 검색한다.
     *
     * @param name 회원이름
     * @return {@link MemberSelectRspDto 조회된 회원 List}
     */
    MemberSelectListRspDto selectList(String name);

    /**
     *
     *
     * <h3>회원 수정</h3>
     *
     * <p>입력된 값 중 유효한 값만 수정 처리한다. - 유효값 조건: NonBlank
     *
     * @param mbrId 회원ID
     * @param reqDto {@link MemberUpdateReqDto 회원 수정 요청 Dto}
     * @return {@link MemberUpdateRspDto 회원 수정 응답 Dto}
     */
    MemberUpdateRspDto update(Long mbrId, MemberUpdateReqDto reqDto);

    /**
     *
     *
     * <h3>회원 삭제</h3>
     *
     * <p>useYn=N 으로 변경하여 삭제처리한다.
     *
     * @param mbrId 회원Id
     * @return {@link MemberDeleteRspDto 회원 삭제 응답 Dto}
     * @throws EntityNotFoundException useYn=Y 인 회원이 없는 경우
     */
    MemberDeleteRspDto delete(Long mbrId);
}
