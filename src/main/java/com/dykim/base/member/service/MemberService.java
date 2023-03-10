package com.dykim.base.member.service;

import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.advice.common.exception.EntityNotFoundException;
import com.dykim.base.member.dto.*;
import com.dykim.base.member.entity.Member;
import com.dykim.base.member.entity.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * <h3>회원 추가</h3>
     *
     * @param reqDto {@link MemberInsertReqDto 회원 추가 요청 Dto}
     * @return {@link MemberInsertRspDto 회원 추가 응답 Dto}
     */
    public MemberInsertRspDto insert(MemberInsertReqDto reqDto) {
        memberRepository.existsByMbrEmlAndUseYn(reqDto.getMbrEml(), "Y")
                .filter(isExists -> isExists)
                .ifPresent(isExists -> {
                    throw new AlreadyExistsException(
                            String.format("Member email '%s' already exists.", reqDto.getMbrEml()));
                });
        var member = reqDto.toEntity().insert();
        return new MemberInsertRspDto(memberRepository.save(member));
    }

    /**
     * <h3>회원 조회</h3>
     * 단일 회원 조회
     *
     * @param mbrId 회원ID
     * @return {@link MemberSelectRspDto 회원 조회 응답 Dto}
     */
    public MemberSelectRspDto select(Long mbrId) {
        return memberRepository.findById(mbrId)
                .map(MemberSelectRspDto::new)
                .orElseGet(MemberSelectRspDto::new);
    }

    /**
     * <h3>회원 수정</h3>
     * 입력된 값 중 유효한 값만 수정 처리한다.
     *  - 유효값 조건: NonBlank
     *
     * @param mbrId 회원ID
     * @param reqDto {@link MemberUpdateReqDto 회원 수정 요청 Dto}
     * @return {@link MemberUpdateRspDto 회원 수정 응답 Dto}
     */
    public MemberUpdateRspDto update(Long mbrId, MemberUpdateReqDto reqDto) {
        return memberRepository.findByMbrIdAndUseYn(mbrId, "Y")
                .map(member -> member.update(reqDto))
                .map(memberRepository::save)
                .map(MemberUpdateRspDto::new)
                .orElseThrow(() -> new EntityNotFoundException("Not found used member. mbrId: " + mbrId));
    }

    /**
     * <h3>회원 삭제</h3>
     * useYn=N 으로 변경하여 삭제처리한다.
     *
     * @param mbrId 회원Id
     * @return {@link MemberDeleteRspDto 회원 삭제 응답 Dto}
     * @throws EntityNotFoundException useYn=Y 인 회원이 없는 경우
     */
    public MemberDeleteRspDto delete(Long mbrId) {
        return memberRepository.findByMbrIdAndUseYn(mbrId, "Y")
                .map(Member::delete)
                .map(memberRepository::save)
                .map(MemberDeleteRspDto::new)
                .orElseThrow(() -> new EntityNotFoundException("Not found used member. mbrId: " + mbrId));
    }

}
