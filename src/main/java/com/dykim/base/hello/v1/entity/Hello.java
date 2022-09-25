package com.dykim.base.hello.v1.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Hello 엔티티<p/>
 *  컬럼 별 Bean Validator(@Eamil, @NotBlank) 로 검증한다.<br/>
 *  JPA Repository 작업 중 검증된다.<br/>
 *  -> 검증 오류 발생 시, ConstraintViolationException 가 발생된다.<br/>
 * <p/>
 * 참고) <a href="https://velog.io/@idean3885/Dto-Entity-Validation-%EC%B2%98%EB%A6%AC#2-entity-validation---validated">Dto, Entity Validation 처리 내용 정리한 개인 블로그</a>
 */
@Getter
@NoArgsConstructor
@Entity
public class Hello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 8)
    private String birthday;

    @Builder
    public Hello(String email, String name, String birthday) {
        this.email = email;
        this.name = name;
        this.birthday = birthday;
    }

}
