package com.dykim.base.member.dto;

import com.dykim.base.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Schema(name = "SignUpDto", description = "회원가입 Dto")
@ToString
@Getter
@Setter // 타임리프 프론트에서 Setter 가 있어야만 렌더링하고 읽을 수 있어서 부득이 설정함.
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {

    @Email(message = "이메일을 정확히 입력해주세요.")
    @Schema(description = "회원 이메일", required = true, example = "email@valid.com")
    private String mbrEml;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "회원 비밀번호", required = true)
    private String mbrPswd;

    @NotBlank(message = "회원이름을 입력해주세요.")
    @Schema(description = "회원이름", required = true, example = "김동영")
    private String mbrNm;

    @NotBlank(message = "회원전화번호를 입력해주세요.")
    @Schema(description = "회원전화번호", required = true, example = "01012345678")
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
                .build();
    }

}
