package com.dykim.base.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(name = "MemberUpdateReqDto", description = "회원 수정 요청 Dto")
@ToString
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberUpdateReqDto {

    @Schema(description = "회원비밀번호")
    private String mbrPswd;

    @Schema(description = "회원전화번호", example = "01234567890")
    private String mbrTelno;

    @Schema(description = "회원도로명주소")
    private String mbrRoadNmAddr;

    @Schema(description = "회원상세주소")
    private String mbrDaddr;
}
