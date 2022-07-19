package com.dykim.base.hello.v1.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HellosRepository extends JpaRepository<Hellos, Long> {
}
