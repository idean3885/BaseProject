package com.dykim.base.hello.v1.service;

import com.dykim.base.hello.v1.controller.advice.exception.HelloException;
import com.dykim.base.hello.v1.controller.advice.exception.HelloNotFoundException;
import com.dykim.base.hello.v1.controller.dto.*;
import com.dykim.base.hello.v1.entity.Hello;
import com.dykim.base.hello.v1.entity.HelloRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

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

    // 서비스 메서드에서 Dto 검증 확인을 위해 Bean Validation 설정
    public HelloInsertRspDto insert(@Valid HelloInsertReqDto reqDto) {
        return new HelloInsertRspDto(helloRepository.save(reqDto.toEntity()));
    }

    public HelloFindRspDto find(Long id) {
        return helloRepository.findById(id)
                .map(HelloFindRspDto::new)
                .orElseGet(HelloFindRspDto::new);
    }

    public List<Hello> findAll() {
        return helloRepository.findAll();
    }

    public HelloUpdateRspDto update(Long id, HelloUpdateReqDto reqDto) {
        return helloRepository.findById(id)
                .map(hello -> hello.update(reqDto))
                .map(HelloUpdateRspDto::new)
                .orElseThrow(() -> new HelloNotFoundException("Not Found Hello. id: " + id));
    }

}
