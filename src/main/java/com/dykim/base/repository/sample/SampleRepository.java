package com.dykim.base.repository.sample;

import com.dykim.base.entity.sample.Sample;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleRepository extends JpaRepository<Sample, Long> {

    Optional<Boolean> existsByEmailAndUseYn(String email, String useYn);

    Optional<Sample> findByIdAndUseYn(Long id, String useYn);

    Optional<List<Sample>> findAllByName(String name);
}
