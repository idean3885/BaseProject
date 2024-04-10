package com.dykim.base.member.dto;

import com.dykim.base.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(name = "MemberDeleteRspDto", description = "회원 삭제 응답 Dto")
@Getter
public class MemberDeleteRspDto {

    @Schema(description = "회원ID", required = true, example = "1")
    private final Long mbrId;

    @Schema(description = "회원이메일", required = true, example = "test@email.com")
    private final String mbrEml;

    @Schema(description = "회원비밀번호", required = true)
    private final String mbrPswd;

    @Schema(description = "회원이름", required = true, example = "김동영")
    private final String mbrNm;

    @Schema(description = "회원전화번호", example = "01012341234")
    private final String mbrTelno;

    @Schema(description = "회원도로명주소")
    private final String mbrRoadNmAddr;

    @Schema(description = "회원상세주소")
    private final String mbrDaddr;

    @Schema(description = "사용여부")
    private final String useYn;

    public MemberDeleteRspDto(Member member) {
        this.mbrId = member.getMbrId();
        this.mbrEml = member.getMbrEml();
        this.mbrPswd = member.getMbrPswd();
        this.mbrNm = member.getMbrNm();
        this.mbrTelno = member.getMbrTelno();
        this.mbrRoadNmAddr = member.getMbrRoadNmAddr();
        this.mbrDaddr = member.getMbrDaddr();
        this.useYn = member.getUseYn();
    }
}
