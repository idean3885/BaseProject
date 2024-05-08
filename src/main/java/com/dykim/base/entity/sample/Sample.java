package com.dykim.base.entity.sample;

import com.dykim.base.dto.sample.SampleUpdateReqDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicUpdate;

/**
 *
 *
 * <h3>Sample 엔티티</h3>
 *
 * <pre>
 * - 컬럼 별 Bean Validator(@Eamil, @NotBlank) 로 검증한다.
 * - JPA Repository 작업 중 검증된다.
 *   검증 오류 발생 시, ConstraintViolationException 가 발생된다.
 * </pre>
 *
 * 참고) <a
 * href="https://velog.io/@idean3885/Dto-Entity-Validation-%EC%B2%98%EB%A6%AC#2-entity-validation---validated">Dto,
 * Entity Validation 처리 내용 정리한 개인 블로그</a>
 */
@DynamicUpdate
@Getter
@NoArgsConstructor
@Entity
public class Sample extends SampleBaseEntity {

    @Comment("Sample ID")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("Sample 이메일")
    @Email
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Comment("Sample 명")
    @NotBlank
    @Column(length = 50, nullable = false)
    private String name;

    @Comment("Sample 생년월일")
    @Column(length = 10)
    private LocalDate birthday;

    @Comment("Sample 날짜")
    @Column(length = 23, nullable = false)
    private LocalDateTime yyyyMMddHHmmssSSS;

    @Comment("Sample 사용여부")
    @Column(length = 1, nullable = false)
    private String useYn;

    @Builder
    public Sample(
            String email,
            String name,
            LocalDate birthday,
            LocalDateTime yyyyMMddHHmmssSSS,
            String useYn) {
        this.email = email;
        this.name = name;
        this.birthday = birthday;
        this.yyyyMMddHHmmssSSS = yyyyMMddHHmmssSSS;
        this.useYn = useYn;
    }

    public Sample insert() {
        useYn = "Y";
        return this;
    }

    public Sample update(SampleUpdateReqDto reqDto) {
        name = StringUtils.isNoneBlank(reqDto.getName()) ? reqDto.getName() : name;
        birthday = Objects.nonNull(reqDto.getBirthday()) ? reqDto.getBirthday() : birthday;
        return this;
    }

    public Sample delete() {
        useYn = "N";
        return this;
    }
}
