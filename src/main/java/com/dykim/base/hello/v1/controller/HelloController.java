package com.dykim.base.hello.v1.controller;

import com.dykim.base.hello.v1.controller.dto.ApiResult;
import com.dykim.base.hello.v1.controller.dto.HelloResponseDto;
import com.dykim.base.hello.v1.service.HelloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.dykim.base.hello.v1.controller.dto.ApiResult.ok;

@Tag(name = "Hello Controller", description = "초기 환경 구성용 테스트 컨트롤러")
@RestController
@RequestMapping("/hello/v1")
@RequiredArgsConstructor
public class HelloController {

    private final HelloService helloService;

    @Operation(summary = "helloPrint", description = "api test example")
    @GetMapping("/helloPrint")
    public String helloPrint() {
        return "hello!";
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = HelloResponseDto.class)))
    })
    @Operation(summary = "get helloResponseDto", description = "api responseDto example")
    @GetMapping("/helloDto")
    public ApiResult<HelloResponseDto> helloDto(
            @Parameter(description = "이름", required = true, example = "kdy") @RequestParam String name,
            @Parameter(description = "이메일", required = true, example = "test@email.com") @RequestParam String email
    ) {
        return ok(new HelloResponseDto(name, email));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "exception not occurred. param(isOccur) is false."),
            @ApiResponse(responseCode = "503", description = "exception occurred. param(isOccur) is true.")
    })
    @Operation(summary = "HelloException occur", description = "Exception occur for ControllerAdvice test.")
    @GetMapping("/occurException")
    public String occurException(boolean isOccur) {
        return helloService.occurException(isOccur);
    }

}
