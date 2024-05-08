package com.dykim.base.service.sample;

import com.dykim.base.advice.sample.exception.SampleAlreadyExistException;
import com.dykim.base.advice.sample.exception.SampleException;
import com.dykim.base.advice.sample.exception.SampleNotFoundException;
import com.dykim.base.dto.sample.SampleDeleteRspDto;
import com.dykim.base.dto.sample.SampleFindListRspDto;
import com.dykim.base.dto.sample.SampleFindRspDto;
import com.dykim.base.dto.sample.SampleInsertReqDto;
import com.dykim.base.dto.sample.SampleInsertRspDto;
import com.dykim.base.dto.sample.SampleUpdateReqDto;
import com.dykim.base.dto.sample.SampleUpdateRspDto;
import com.dykim.base.entity.sample.Sample;
import com.dykim.base.repository.sample.SampleRepository;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated // 서비스 메서드 진입 시 빈 벨리데이터를 통해 검증하기 위해 추가 -> Aop 로 동작함.
@RequiredArgsConstructor
@Service
public class SampleServiceImpl implements SampleService {

    private final SampleRepository sampleRepository;

    public String occurException(boolean isOccur) {
        if (isOccur) {
            throw new SampleException("HelloException is occurred by params(isOccur)");
        }

        return "param(isOccur) is false. HelloException is not occurred.";
    }

    public SampleInsertRspDto insert(@Valid SampleInsertReqDto reqDto) {
        sampleRepository
                .existsByEmailAndUseYn(reqDto.getEmail(), "Y")
                .filter(isExists -> isExists)
                .ifPresent(
                        isExists -> {
                            throw new SampleAlreadyExistException(
                                    String.format("Email '%s' already exists.", reqDto.getEmail()));
                        });
        var hello = reqDto.toEntity().insert();
        return new SampleInsertRspDto(sampleRepository.save(hello));
    }

    public SampleFindRspDto select(Long id) {
        return sampleRepository
                .findById(id)
                .map(SampleFindRspDto::new)
                .orElseGet(SampleFindRspDto::new);
    }

    public SampleFindListRspDto selectList(String name) {
        return sampleRepository
                .findAllByName(name)
                .map(SampleFindListRspDto::new)
                .orElseGet(SampleFindListRspDto::new);
    }

    public SampleUpdateRspDto update(Long id, SampleUpdateReqDto reqDto) {
        return sampleRepository
                .findByIdAndUseYn(id, "Y")
                .map(hello -> hello.update(reqDto))
                .map(sampleRepository::save)
                .map(SampleUpdateRspDto::new)
                .orElseThrow(() -> new SampleNotFoundException("Not Found Hello. id: " + id));
    }

    public SampleDeleteRspDto delete(Long id) {
        return sampleRepository
                .findByIdAndUseYn(id, "Y")
                .map(Sample::delete)
                .map(sampleRepository::save)
                .map(SampleDeleteRspDto::new)
                .orElseThrow(() -> new SampleNotFoundException("Not Found Hello. id: " + id));
    }
}
