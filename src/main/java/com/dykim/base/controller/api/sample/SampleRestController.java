package com.dykim.base.controller.api.sample;

import com.dykim.base.config.annotation.Debounce;
import com.dykim.base.consts.uris.SampleApiUris;
import com.dykim.base.dto.ApiResult;
import com.dykim.base.dto.sample.SampleDeleteRspDto;
import com.dykim.base.dto.sample.SampleFindListRspDto;
import com.dykim.base.dto.sample.SampleFindRspDto;
import com.dykim.base.dto.sample.SampleInsertReqDto;
import com.dykim.base.dto.sample.SampleInsertRspDto;
import com.dykim.base.dto.sample.SampleRspDto;
import com.dykim.base.dto.sample.SampleUpdateReqDto;
import com.dykim.base.dto.sample.SampleUpdateRspDto;
import com.dykim.base.service.sample.SampleService;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SampleRestController", description = "초기 환경 구성용 테스트 컨트롤러")
@RequiredArgsConstructor
@RestController
public class SampleRestController {

    private final SampleService sampleService;

    @Debounce(3000)
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "hello!",
                        content = @Content(schema = @Schema(implementation = String.class))),
                @ApiResponse(
                        responseCode = "429",
                        description = "Too Many Requests when Debounce time millis.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
            })
    @Operation(summary = "helloPrintDebounce", description = "api call&debounce test example")
    @GetMapping(SampleApiUris.DEBOUNCE)
    public String helloPrintDebounce() {
        return Json.pretty("hello!");
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
            })
    @Operation(summary = "get sampleResponseDto", description = "api responseDto example")
    @GetMapping(SampleApiUris.DTO)
    public ApiResult<SampleRspDto> sampleDto(
            @Parameter(description = "이름", required = true, example = "kdy") @RequestParam String name,
            @Parameter(description = "이메일", required = true, example = "test@email.com") @RequestParam
                    String email) {
        return ApiResult.ok(new SampleRspDto(name, email));
    }

    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "exception not occurred. param(isOccur) is false."),
                @ApiResponse(
                        responseCode = "503",
                        description = "exception occurred. param(isOccur) is true.")
            })
    @Operation(
            summary = "SampleException occur",
            description = "Exception occur for ControllerAdvice test.")
    @GetMapping(SampleApiUris.EXCEPTION)
    public String occurException(boolean isOccur) {
        return sampleService.occurException(isOccur);
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "RequestSession is valid. return 'true'"),
                @ApiResponse(responseCode = "401", description = "RequestSession invalid.")
            })
    @Operation(
            summary = "Sample validSession",
            description = "Session validation SessionValidationInterceptor.")
    @GetMapping(SampleApiUris.VALID_SESSION)
    public String validSession() {
        return "true";
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request parameters or invalid insert data.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Unexpected exception occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class)))
            })
    @Operation(summary = "Insert Sample", description = "Insert Sample")
    @PutMapping(SampleApiUris.INSERT)
    public ApiResult<SampleInsertRspDto> insert(@Valid @RequestBody SampleInsertReqDto reqDto) {
        return ApiResult.ok(sampleService.insert(reqDto));
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request parameters or invalid insert data.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Unexpected exception occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class)))
            })
    @Operation(summary = "select Sample", description = "Select Sample")
    @GetMapping(SampleApiUris.SELECT + "/{id}")
    public ApiResult<SampleFindRspDto> select(@PathVariable Long id) {
        return ApiResult.ok(sampleService.select(id));
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
                @ApiResponse(
                        responseCode = "500",
                        description = "Unexpected exception occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class)))
            })
    @Operation(summary = "select Sample list", description = "select sample list")
    @GetMapping(SampleApiUris.SELECT_LIST)
    public ApiResult<SampleFindListRspDto> selectList(@RequestParam String name) {
        return ApiResult.ok(sampleService.selectList(name));
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request parameters or invalid insert data.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found Sample by id.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Unexpected exception occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class)))
            })
    @Operation(summary = "update Sample", description = "update sample when valid params.")
    @PostMapping(SampleApiUris.UPDATE + "/{id}")
    public ApiResult<SampleUpdateRspDto> update(
            @PathVariable Long id, @RequestBody SampleUpdateReqDto reqDto) {
        return ApiResult.ok(sampleService.update(id, reqDto));
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request parameters or invalid insert data.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found Sample by id.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Unexpected exception occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class)))
            })
    @Operation(summary = "delete Sample", description = "Delete processing by changing useYn=N")
    @DeleteMapping(SampleApiUris.DELETE + "/{id}")
    public ApiResult<SampleDeleteRspDto> delete(@PathVariable Long id) {
        return ApiResult.ok(sampleService.delete(id));
    }
}
