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

    @Schema(description = "비밀번호")
    private String password;

    @Schema(description = "휴대폰 번호", example = "01234567890")
    private String phoneNo;

    @Schema(description = "도로명 주소")
    private String roadNameAddress;

    @Schema(description = "상세 주소")
    private String detailAddress;
}
