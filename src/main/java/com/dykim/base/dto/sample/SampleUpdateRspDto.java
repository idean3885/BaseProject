package com.dykim.base.dto.sample;

import com.dykim.base.entity.sample.Sample;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "SampleUpdateRspDto", description = "Sample 수정 응답 Dto")
@Getter
@NoArgsConstructor
public class SampleUpdateRspDto {

    @Schema(description = "id", required = true, example = "1")
    private Long id;

    @Schema(description = "이메일", required = true, example = "test@email.com")
    private String email;

    @Schema(description = "이름", required = true, example = "kdy")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "생일", example = "1993-07-24")
    private LocalDate birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    @Schema(description = "생성시간 샘플", example = "2022-10-07 15:16:50.599")
    private LocalDateTime yyyyMMddHHmmssSSS;

    public SampleUpdateRspDto(Sample sample) {
        id = sample.getId();
        email = sample.getEmail();
        name = sample.getName();
        birthday = sample.getBirthday();
        yyyyMMddHHmmssSSS = sample.getYyyyMMddHHmmssSSS();
    }
}
