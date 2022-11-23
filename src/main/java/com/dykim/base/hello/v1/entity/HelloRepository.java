package com.dykim.base.hello.v1.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HelloRepository extends JpaRepository<Hello, Long> {

    Optional<Boolean> existsByEmailAndUseYn(String email, String useYn);

}
