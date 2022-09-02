package com.dykim.base.hello.v1.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@Validated
@Getter
@NoArgsConstructor
@Entity
public class Hello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "{custom.message}")
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 8)
    private String birthday;

    @Builder
    public Hello(String email, String name, String birthday) {
//        checkArgument(Objects.nonNull(email), "TODO: 메시지소스 설정");
//        checkArgument(Objects.nonNull(name), "TODO: 메시지소스 설정");

        this.email = email;
        this.name = name;
        this.birthday = birthday;
    }

}
