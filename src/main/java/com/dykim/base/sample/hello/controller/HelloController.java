package com.dykim.base.sample.hello.controller;

import com.dykim.base.config.annotation.Debounce;
import com.dykim.base.sample.hello.dto.*;
import com.dykim.base.sample.hello.service.HelloService;
import io.swagger.v3.core.util.Json;
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

@Tag(name = "Hello Controller", description = "초기 환경 구성용 테스트 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/sample/hello")
@RestController
public class HelloController {

    private final HelloService helloService;

    @Debounce(3000)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "hello!",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "429", description = "Too Many Requests when Debounce time millis.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
    })
    @Operation(summary = "helloPrintDebounce", description = "api call&debounce test example")
    @GetMapping("/helloPrintDebounce")
    public String helloPrintDebounce() {
        return Json.pretty("hello!");
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
        return ApiResult.ok(new HelloRspDto(name, email));
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
            @ApiResponse(responseCode = "200", description = "RequestSession is valid. return 'true'"),
            @ApiResponse(responseCode = "401", description = "RequestSession invalid.")
    })
    @Operation(summary = "Hello validSession", description = "Session validation SessionValidationInterceptor.")
    @GetMapping("/validSession")
    public String validSession() {
        return "true";
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
    @PutMapping
    public ApiResult<HelloInsertRspDto> insert(@Valid @RequestBody HelloInsertReqDto reqDto) {
        return ApiResult.ok(helloService.insert(reqDto));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = HelloFindRspDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or invalid insert data.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected exception occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @Operation(summary = "find Hello", description = "find hello")
    @GetMapping("/{id}")
    public ApiResult<HelloFindRspDto> find(@PathVariable Long id) {
        return ApiResult.ok(helloService.find(id));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = HelloFindListRspDto.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected exception occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @Operation(summary = "find Hello list", description = "find hello list")
    @GetMapping()
    public ApiResult<HelloFindListRspDto> findList(@RequestParam String name) {
        return ApiResult.ok(helloService.findList(name));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = HelloUpdateRspDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or invalid insert data.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "Not Found Hello by id.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected exception occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @Operation(summary = "update Hello", description = "update hello when valid params.")
    @PostMapping("/{id}")
    public ApiResult<HelloUpdateRspDto> update(@PathVariable Long id, @RequestBody HelloUpdateReqDto reqDto) {
        return ApiResult.ok(helloService.update(id, reqDto));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = HelloDeleteRspDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or invalid insert data.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "Not Found Hello by id.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected exception occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @Operation(summary = "delete Hello", description = "Delete processing by changing useYn=N")
    @DeleteMapping("/{id}")
    public ApiResult<HelloDeleteRspDto> delete(@PathVariable Long id) {
        return ApiResult.ok(helloService.delete(id));
    }

}
