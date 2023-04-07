package com.dykim.base.member.dto;

import com.dykim.base.enums.AuthorityRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(name = "MemberSessionVo", description = "멤버 세션 Vo")
@ToString
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSessionVo {

    @NotNull
    @Schema(description = "회원ID", required = true)
    private Long mbrId;

    @Email
    @Schema(description = "회원이메일", required = true, example = "test@email.com")
    private String mbrEml;

    @NotBlank
    @Schema(description = "회원이름", required = true, example = "김동영")
    private String mbrNm;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Schema(description = "권한 직책", required = true, example = "ADMIN|DEVELOPER|GENERAL")
    private AuthorityRole authorityRole;

}
