package com.dykim.base.member.service;

import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.member.dto.MemberInsertReqDto;
import com.dykim.base.member.dto.MemberInsertRspDto;
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

}
