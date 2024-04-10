package com.dykim.base.sample.hello.entity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelloRepository extends JpaRepository<Hello, Long> {

    Optional<Boolean> existsByEmailAndUseYn(String email, String useYn);

    Optional<Hello> findByIdAndUseYn(Long id, String useYn);

    Optional<List<Hello>> findAllByName(String name);
}
