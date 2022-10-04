package com.dykim.base.hello.v1.controller;

import com.dykim.base.hello.v1.controller.dto.ApiResult;
import com.dykim.base.hello.v1.controller.dto.HelloInsertReqDto;
import com.dykim.base.hello.v1.controller.dto.HelloInsertRspDto;
import com.dykim.base.hello.v1.controller.dto.HelloRspDto;
import com.dykim.base.hello.v1.service.HelloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.dykim.base.hello.v1.controller.dto.ApiResult.ok;

@Tag(name = "Hello Controller", description = "초기 환경 구성용 테스트 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/hello/v1")
@RestController
public class HelloController {

    private final HelloService helloService;

    @Operation(summary = "helloPrint", description = "api test example")
    @GetMapping("/helloPrint")
    public String helloPrint() {
        return "hello!";
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = HelloRspDto.class)))
    })
    @Operation(summary = "get helloResponseDto", description = "api responseDto example")
    @GetMapping("/helloDto")
    public ApiResult<HelloRspDto> helloDto(
            @Parameter(description = "이름", required = true, example = "kdy") @RequestParam String name,
            @Parameter(description = "이메일", required = true, example = "test@email.com") @RequestParam String email
    ) {
        return ok(new HelloRspDto(name, email));
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

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = HelloInsertRspDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or invalid insert data.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected exception occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @Operation(summary = "Insert Hello", description = "Insert hello")
    @PostMapping
    public ApiResult<HelloInsertRspDto> insert(@Valid @RequestBody HelloInsertReqDto reqDto) {
        return ok(helloService.insert(reqDto));
    }

}
