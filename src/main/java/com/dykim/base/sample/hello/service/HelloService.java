package com.dykim.base.sample.hello.service;

import com.dykim.base.advice.hello.exception.HelloAlreadyExistException;
import com.dykim.base.advice.hello.exception.HelloException;
import com.dykim.base.advice.hello.exception.HelloNotFoundException;
import com.dykim.base.sample.hello.dto.*;
import com.dykim.base.sample.hello.entity.Hello;
import com.dykim.base.sample.hello.entity.HelloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Slf4j
@Validated // 서비스 메서드 진입 시 빈 벨리데이터를 통해 검증하기 위해 추가 -> Aop 로 동작함.
@RequiredArgsConstructor
@Service
public class HelloService {

    private final HelloRepository helloRepository;

    public String occurException(boolean isOccur) {
        if (isOccur) {
            throw new HelloException("HelloException is occurred by params(isOccur)");
        }

        return "param(isOccur) is false. HelloException is not occurred.";
    }

    /**
     * <h3>Hello 삽입</h3>
     * <pre>
     *  - 검색 - 검증 - 인서트 방식으로 진행
     *  - 총 두번의 쿼리를 실행하지만 인서트 오류를 명확히 설정하기 위해 위와 같이 작업함.
     *  - 인서트 도중 예외가 발생하는 경우 DataIntegrityViolationException 이 발생
     *  - 이 예외는 인서트, 업데이트 중 발생하는 포괄적인 예외이기 때문에 다른 작업 중에도 발생할 수 있음.
     *  - 따라서 인서트 예외를 특정하여 사이드 이펙트를 줄이기 위해 커스텀 예외를 발생시킨다.
     * </pre>
     * 참고) 서비스 메서드에서 Dto 검증 확인을 위해 Bean Validation 설정
     *
     * @param reqDto Hello 추가 요청 Dto
     * @return Hello 추가 응답 Dto | HelloAlreadyExistException
     */
    public HelloInsertRspDto insert(@Valid HelloInsertReqDto reqDto) {
        helloRepository.existsByEmailAndUseYn(reqDto.getEmail(), "Y")
                .filter(isExists -> isExists)
                .ifPresent(isExists -> {
                    throw new HelloAlreadyExistException(
                            String.format("Email '%s' already exists.", reqDto.getEmail()));
                });
        var hello = reqDto.toEntity().insert();
        return new HelloInsertRspDto(helloRepository.save(hello));
    }

    public HelloFindRspDto find(Long id) {
        return helloRepository.findById(id)
                .map(HelloFindRspDto::new)
                .orElseGet(HelloFindRspDto::new);
    }

    public HelloFindListRspDto findList(String name) {
        return helloRepository.findAllByName(name)
                .map(HelloFindListRspDto::new)
                .orElseGet(HelloFindListRspDto::new);
    }

    public HelloUpdateRspDto update(Long id, HelloUpdateReqDto reqDto) {
        return helloRepository.findByIdAndUseYn(id, "Y")
                .map(hello -> hello.update(reqDto))
                .map(helloRepository::save)
                .map(HelloUpdateRspDto::new)
                .orElseThrow(() -> new HelloNotFoundException("Not Found Hello. id: " + id));
    }

    public HelloDeleteRspDto delete(Long id) {
        return helloRepository.findByIdAndUseYn(id, "Y")
                .map(Hello::delete)
                .map(helloRepository::save)
                .map(HelloDeleteRspDto::new)
                .orElseThrow(() -> new HelloNotFoundException("Not Found Hello. id: " + id));
    }

}
