package com.dykim.base.hello.v1.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Hellos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 8)
    private String birthday;

    @Builder
    public Hellos(String email, String name, String birthday) {
        this.email = email;
        this.name = name;
        this.birthday = birthday;
    }

}
