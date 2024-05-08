package com.dykim.base.dto.member;

import com.dykim.base.entity.member.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "MemberSelectRspDto", description = "회원 조회 응답 Dto")
@Getter
@NoArgsConstructor
public class MemberSelectRspDto {

    @Schema(description = "회원ID", required = true, example = "1")
    private Long mbrId;

    @Schema(description = "회원이메일", required = true, example = "test@email.com")
    private String email;

    @Schema(description = "회원비밀번호", required = true)
    private String password;

    @Schema(description = "회원이름", required = true, example = "김동영")
    private String name;

    @Schema(description = "회원전화번호", example = "01012341234")
    private String phoneNo;

    @Schema(description = "회원도로명주소")
    private String roadNameAddress;

    @Schema(description = "회원상세주소")
    private String detailAddress;

    @Schema(description = "사용여부")
    private String useYn;

    public MemberSelectRspDto(Member member) {
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
