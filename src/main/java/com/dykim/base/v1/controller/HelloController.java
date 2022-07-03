package com.dykim.base.v1.controller;

import com.dykim.base.v1.controller.dto.HelloResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Hello Controller", description = "초기 환경 구성용 테스트 컨트롤러")
@RestController
@RequestMapping("/v1/hello")
public class HelloController {

    @Operation(summary = "helloPrint", description = "api test example")
    @GetMapping("/helloPrint")
    public String helloPrint() {
        return "hello!";
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = HelloResponseDto.class)))
    })
    @Operation(summary = "get helloResponseDto", description = "api responseDto example")
    @GetMapping("/helloDto")
    public HelloResponseDto helloDto(
            @Parameter(description = "이름", required = true, example = "kdy") @RequestParam String name,
            @Parameter(description = "이메일", required = true, example = "test@email.com") @RequestParam String email
    ) {
        return new HelloResponseDto(name, email);
    }

}
