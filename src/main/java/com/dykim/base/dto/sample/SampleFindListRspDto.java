package com.dykim.base.dto.sample;

import com.dykim.base.entity.sample.Sample;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "SampleFindListRspDto", description = "Sample 목록 조회 응답 Dto")
@Getter
@NoArgsConstructor
public class SampleFindListRspDto {

    @Schema(description = "list", required = true, example = "조회되지 않은 경우 EmptyList")
    private List<SampleFindRspDto> list;

    public SampleFindListRspDto(List<Sample> sampleList) {
        list = sampleList.stream().map(SampleFindRspDto::new).collect(Collectors.toList());
    }
}
