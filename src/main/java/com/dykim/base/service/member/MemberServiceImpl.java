package com.dykim.base.service.member;

import com.dykim.base.advice.common.exception.AlreadyExistsException;
import com.dykim.base.advice.common.exception.EntityNotFoundException;
import com.dykim.base.dto.member.MemberDeleteRspDto;
import com.dykim.base.dto.member.MemberInsertReqDto;
import com.dykim.base.dto.member.MemberInsertRspDto;
import com.dykim.base.dto.member.MemberSelectListRspDto;
import com.dykim.base.dto.member.MemberSelectRspDto;
import com.dykim.base.dto.member.MemberUpdateReqDto;
import com.dykim.base.dto.member.MemberUpdateRspDto;
import com.dykim.base.entity.member.Member;
import com.dykim.base.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberInsertRspDto insert(MemberInsertReqDto reqDto) {
        memberRepository
                .existsByEmailAndUseYn(reqDto.getEmail(), "Y")
                .filter(isExists -> isExists)
                .ifPresent(
                        isExists -> {
                            throw new AlreadyExistsException(
                                    String.format("Member email '%s' already exists.", reqDto.getEmail()));
                        });
        var member = reqDto.toEntity().insert();
        return new MemberInsertRspDto(memberRepository.save(member));
    }

    public MemberSelectRspDto select(Long mbrId) {
        return memberRepository
                .findById(mbrId)
                .map(MemberSelectRspDto::new)
                .orElseGet(MemberSelectRspDto::new);
    }

    public MemberSelectListRspDto selectList(String name) {
        return memberRepository
                .findAllByName(name)
                .map(MemberSelectListRspDto::new)
                .orElseGet(MemberSelectListRspDto::new);
    }

    public MemberUpdateRspDto update(Long mbrId, MemberUpdateReqDto reqDto) {
        return memberRepository
                .findByIdAndUseYn(mbrId, "Y")
                .map(member -> member.update(reqDto))
                .map(memberRepository::save)
                .map(MemberUpdateRspDto::new)
                .orElseThrow(() -> new EntityNotFoundException("Not found used member. mbrId: " + mbrId));
    }

    public MemberDeleteRspDto delete(Long mbrId) {
        return memberRepository
                .findByIdAndUseYn(mbrId, "Y")
                .map(Member::delete)
                .map(memberRepository::save)
                .map(MemberDeleteRspDto::new)
                .orElseThrow(() -> new EntityNotFoundException("Not found used member. mbrId: " + mbrId));
    }
}
