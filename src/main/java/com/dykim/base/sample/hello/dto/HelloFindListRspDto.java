package com.dykim.base.sample.hello.dto;

import com.dykim.base.sample.hello.entity.Hello;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "HelloFindListRspDto", description = "Hello 목록 조회 응답 Dto")
@Getter
@NoArgsConstructor
public class HelloFindListRspDto {

    @Schema(description = "list", required = true, example = "조회되지 않은 경우 EmptyList")
    private List<HelloFindRspDto> list;

    public HelloFindListRspDto(List<Hello> helloList) {
        list = helloList.stream().map(HelloFindRspDto::new).collect(Collectors.toList());
    }
}
