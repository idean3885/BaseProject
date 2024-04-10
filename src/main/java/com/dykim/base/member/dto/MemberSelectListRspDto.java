package com.dykim.base.member.dto;

import com.dykim.base.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "MemberSelectListRspDto", description = "회원 목록 조회 응답 Dto")
@Getter
@NoArgsConstructor
public class MemberSelectListRspDto {

    @Schema(description = "회원 목록", required = true, example = "조회되지 않은 경우 EmptyList 응답됌")
    private List<MemberSelectRspDto> list;

    public MemberSelectListRspDto(List<Member> memberList) {
        list = memberList.stream().map(MemberSelectRspDto::new).collect(Collectors.toList());
    }
}
