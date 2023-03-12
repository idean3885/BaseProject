package com.dykim.base.member.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Boolean> existsByMbrEmlAndUseYn(String mbrEml, String useYn);

    Optional<Member> findByMbrIdAndUseYn(Long mbrId, String useYn);

    Optional<List<Member>> findAllByMbrNm(String mbrNm);

}
