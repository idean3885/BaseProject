package com.dykim.base.member.dto;

import com.dykim.base.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Schema(name = "MemberInsertReqDto", description = "회원 추가 요청 Dto")
@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberInsertReqDto {

    @NotBlank
    @Email
    @Schema(description = "회원이메일", required = true, example = "test@email.com")
    private String mbrEml;

    @NotBlank
    @Schema(description = "회원비밀번호", required = true)
    private String mbrPswd;

    @NotBlank
    @Schema(description = "회원이름", required = true, example = "김동영")
    private String mbrNm;

    @Length(min = 11, max = 11)
    @Schema(description = "회원전화번호", example = "01012341234")
    private String mbrTelno;

    @Schema(description = "회원도로명주소")
    private String mbrRoadNmAddr;

    @Schema(description = "회원상세주소")
    private String mbrDaddr;

    public Member toEntity() {
        return Member.builder()
                .mbrEml(mbrEml)
                .mbrPswd(mbrPswd)
                .mbrNm(mbrNm)
                .mbrTelno(mbrTelno)
                .mbrRoadNmAddr(mbrRoadNmAddr)
                .mbrDaddr(mbrDaddr)
                .useYn("Y")
                .build();
    }
}
