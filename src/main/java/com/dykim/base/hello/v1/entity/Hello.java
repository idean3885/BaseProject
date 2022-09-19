package com.dykim.base.hello.v1.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Validated
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
