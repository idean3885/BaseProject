package com.dykim.base.dto.member;

import com.dykim.base.entity.member.Member;
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
    @Schema(description = "이메일", required = true, example = "test@email.com")
    private String email;

    @NotBlank
    @Schema(description = "비밀번호", required = true)
    private String password;

    @NotBlank
    @Schema(description = "이름", required = true, example = "김동영")
    private String name;

    @Length(min = 11, max = 11)
    @Schema(description = "휴대폰 번호", example = "01012341234")
    private String phoneNo;

    @Schema(description = "도로명 주소")
    private String roadNameAddress;

    @Schema(description = "회원상세주소")
    private String detailAddress;

    public Member toEntity() {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .phoneNo(phoneNo)
                .roadNameAddress(roadNameAddress)
                .detailAddress(detailAddress)
                .useYn("Y")
                .build();
    }
}
