package com.dykim.base.member.dto;

import com.dykim.base.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "MemberInsertRspDto", description = "회원 추가 응답 Dto")
@Getter
@NoArgsConstructor
public class MemberInsertRspDto {

    @Schema(description = "회원 ID", required = true, example = "1")
    private Long id;

    @Schema(description = "이메일", required = true, example = "test@email.com")
    private String email;

    @Schema(description = "비밀번호", required = true)
    private String password;

    @Schema(description = "이름", required = true, example = "김동영")
    private String name;

    @Schema(description = "휴대폰 번호", example = "01012341234")
    private String phoneNo;

    @Schema(description = "도로명 주소")
    private String roadNameAddress;

    @Schema(description = "상세 주소")
    private String detailAddress;

    @Schema(description = "회원 여부")
    private String useYn;

    public MemberInsertRspDto(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.name = member.getName();
        this.phoneNo = member.getPhoneNo();
        this.roadNameAddress = member.getRoadNameAddress();
        this.detailAddress = member.getDetailAddress();
        this.useYn = member.getUseYn();
    }
}
