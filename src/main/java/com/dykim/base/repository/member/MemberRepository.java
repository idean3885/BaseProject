package com.dykim.base.repository.member;

import com.dykim.base.entity.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Boolean> existsByEmailAndUseYn(String email, String useYn);

    Optional<Member> findByIdAndUseYn(Long mbrId, String useYn);

    Optional<List<Member>> findAllByName(String name);

    Optional<Member> findByEmailAndUseYn(String email, String useYn);
}
