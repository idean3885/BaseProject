package com.dykim.base.member.entity;

import com.dykim.base.member.dto.MemberUpdateReqDto;
import com.dykim.base.sample.hello.entity.Hello;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;

/**
 *
 *
 * <h3>Member 엔티티</h3>
 *
 * <pre>
 * - 컬럼 별 Bean Validator(@Eamil, @NotBlank) 로 검증한다.
 * - JPA Repository 작업 중 검증된다.
 *   검증 오류 발생 시, ConstraintViolationException 가 발생된다.
 * </pre>
 *
 * 참고1) {@link Hello 샘플엔티티 Hello}
 *
 * <p>참고2) <a href="https://www.erdcloud.com/d/ZG8wGTXTmkTyL8qdp">회원 엔티티 ErdCloud</a>
 */
// @DynamicUpdate 컬럼이 적어 비활성화(변화된 컬럼을 찾는데 비용이 더 많이 들 수 있다.)
@Getter
@NoArgsConstructor
@Entity
public class Member extends MemberBaseEntity implements Serializable {

    @Comment("ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("이메일")
    @Email
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Comment("비밀번호")
    @NotBlank
    @Column(nullable = false, length = 64)
    private String password;

    @Comment("이름")
    @NotBlank
    @Column(nullable = false, length = 50)
    private String name;

    @Comment("휴대폰 번호")
    @Column(length = 11)
    private String phoneNo;

    @Comment("도로명 주소")
    @Column(length = 200)
    private String roadNameAddress;

    @Comment("상세 주소")
    @Column(length = 200)
    private String detailAddress;

    @Comment("권한 목록")
    @Column(nullable = false, length = 50)
    private String roleList;

    @Comment("회원 여부")
    @Column(nullable = false, length = 1)
    private String useYn;

    @Builder
    public Member(
            String email,
            String password,
            String name,
            String phoneNo,
            String roadNameAddress,
            String detailAddress,
            String roleList,
            String useYn) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNo = phoneNo;
        this.roadNameAddress = roadNameAddress;
        this.detailAddress = detailAddress;
        this.roleList = roleList;
        this.useYn = useYn;
    }

    public Member insert() {
        useYn = "Y";
        return this;
    }

    public Member update(MemberUpdateReqDto reqDto) {
        password = StringUtils.isNoneBlank(reqDto.getPassword()) ? reqDto.getPassword() : password;
        phoneNo = StringUtils.isNoneBlank(reqDto.getPhoneNo()) ? reqDto.getPhoneNo() : phoneNo;
        roadNameAddress =
                StringUtils.isNoneBlank(reqDto.getRoadNameAddress())
                        ? reqDto.getRoadNameAddress()
                        : roadNameAddress;
        detailAddress =
                StringUtils.isNoneBlank(reqDto.getDetailAddress())
                        ? reqDto.getDetailAddress()
                        : detailAddress;
        return this;
    }

    public Member delete() {
        useYn = "N";
        return this;
    }
}
