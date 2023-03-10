package com.dykim.base.member.entity;


import com.dykim.base.member.dto.MemberUpdateReqDto;
import com.dykim.base.sample.hello.entity.Hello;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * <h3>Member 엔티티</h3>
 * <pre>
 * - 컬럼 별 Bean Validator(@Eamil, @NotBlank) 로 검증한다.
 * - JPA Repository 작업 중 검증된다.
 *   검증 오류 발생 시, ConstraintViolationException 가 발생된다.
 * </pre>
 * 참고1) {@link Hello 샘플엔티티 Hello}<p/>
 * 참고2) <a href="https://www.erdcloud.com/d/ZG8wGTXTmkTyL8qdp">회원 엔티티 ErdCloud</a>
 */
//@DynamicUpdate 컬럼이 적어 비활성화(변화된 컬럼을 찾는데 비용이 더 많이 들 수 있다.)
@Getter
@NoArgsConstructor
@Entity
public class Member extends MemberBaseEntity {

    @Comment("회원ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mbrId;

    @Comment("회원이메일")
    @Email
    @Column(unique = true, nullable = false, length = 100)
    private String mbrEml;

    @Comment("회원비밀번호")
    @NotBlank
    @Column(nullable = false, length = 64)
    private String mbrPswd;

    @Comment("회원이름")
    @NotBlank
    @Column(nullable = false, length = 50)
    private String mbrNm;

    @Comment("회원전화번호")
    @Column(length = 11)
    private String mbrTelno;

    @Comment("회원도로명주소")
    @Column(length = 200)
    private String mbrRoadNmAddr;

    @Comment("회원상세주소")
    @Column(length = 200)
    private String mbrDaddr;

    @Comment("사용여부")
    @Column(nullable = false, length = 1)
    private String useYn;

    @Builder
    public Member(String mbrEml, String mbrPswd, String mbrNm, String mbrTelno, String mbrRoadNmAddr, String mbrDaddr, String useYn) {
        this.mbrEml = mbrEml;
        this.mbrPswd = mbrPswd;
        this.mbrNm = mbrNm;
        this.mbrTelno = mbrTelno;
        this.mbrRoadNmAddr = mbrRoadNmAddr;
        this.mbrDaddr = mbrDaddr;
        this.useYn = useYn;
    }

    public Member insert() {
        useYn = "Y";
        return this;
    }

    public Member update(MemberUpdateReqDto reqDto) {
        mbrPswd = StringUtils.isNoneBlank(reqDto.getMbrPswd()) ? reqDto.getMbrPswd() : mbrPswd;
        mbrTelno = StringUtils.isNoneBlank(reqDto.getMbrTelno()) ? reqDto.getMbrTelno() : mbrTelno;
        mbrRoadNmAddr = StringUtils.isNoneBlank(reqDto.getMbrRoadNmAddr()) ? reqDto.getMbrRoadNmAddr() : mbrRoadNmAddr;
        mbrDaddr = StringUtils.isNoneBlank(reqDto.getMbrDaddr()) ? reqDto.getMbrDaddr() : mbrDaddr;
        return this;
    }

    public Member delete() {
        useYn = "N";
        return this;
    }

}
