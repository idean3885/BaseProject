package com.dykim.base.hello.v1.service;

import com.dykim.base.hello.v1.controller.advice.exception.HelloException;
import com.dykim.base.hello.v1.controller.dto.HelloInsertReqDto;
import com.dykim.base.hello.v1.controller.dto.HelloInsertRspDto;
import com.dykim.base.hello.v1.domain.HelloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public HelloInsertRspDto insert(HelloInsertReqDto reqDto) {
        return new HelloInsertRspDto(helloRepository.save(reqDto.toEntity()));
    }

}
