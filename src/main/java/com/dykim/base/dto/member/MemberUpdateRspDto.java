package com.dykim.base.dto.member;

import com.dykim.base.entity.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "MemberUpdateRspDto", description = "회원 수정 응답 Dto")
@Getter
public class MemberUpdateRspDto {

    @Schema(description = "회원ID", required = true, example = "1")
    private final Long mbrId;

    @Schema(description = "회원이메일", required = true, example = "test@email.com")
    private final String email;

    @Schema(description = "회원비밀번호", required = true)
    private final String password;

    @Schema(description = "회원이름", required = true, example = "김동영")
    private final String name;

    @Schema(description = "회원전화번호", example = "01012341234")
    private final String phoneNo;

    @Schema(description = "회원도로명주소")
    private final String roadNameAddress;

    @Schema(description = "회원상세주소")
    private final String detailAddress;

    @Schema(description = "사용여부")
    private final String useYn;

    public MemberUpdateRspDto(Member member) {
        this.mbrId = member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.name = member.getName();
        this.phoneNo = member.getPhoneNo();
        this.roadNameAddress = member.getRoadNameAddress();
        this.detailAddress = member.getDetailAddress();
        this.useYn = member.getUseYn();
    }
}
