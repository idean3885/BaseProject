package com.dykim.base.member.dto;

import com.dykim.base.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "MemberInsertRspDto", description = "회원 추가 응답 Dto")
@Getter
@NoArgsConstructor
public class MemberInsertRspDto {

    @Schema(description = "회원ID", required = true, example = "1")
    private Long mbrId;

    @Schema(description = "회원이메일", required = true, example = "test@email.com")
    private String mbrEml;

    @Schema(description = "회원비밀번호", required = true)
    private String mbrPswd;

    @Schema(description = "회원이름", required = true, example = "김동영")
    private String mbrNm;

    @Schema(description = "회원전화번호", example = "01012341234")
    private String mbrTelno;

    @Schema(description = "회원도로명주소")
    private String mbrRoadNmAddr;

    @Schema(description = "회원상세주소")
    private String mbrDaddr;

    @Schema(description = "사용여부")
    private String useYn;

    public MemberInsertRspDto(Member member) {
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
