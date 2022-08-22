package com.dykim.base.hello.v1.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@NoArgsConstructor
@Entity
public class Hello {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 8)
    private String birthday;

    @Builder
    public Hello(String email, String name, String birthday) {
        checkArgument(Objects.nonNull(email), "TODO: 메시지소스 설정");
        checkArgument(Objects.nonNull(name), "TODO: 메시지소스 설정");

        this.email = email;
        this.name = name;
        this.birthday = birthday;
    }

}
