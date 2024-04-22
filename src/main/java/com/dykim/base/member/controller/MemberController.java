package com.dykim.base.member.controller;

import static com.dykim.base.sample.hello.dto.ApiResult.ok;

import com.dykim.base.member.dto.MemberDeleteRspDto;
import com.dykim.base.member.dto.MemberInsertReqDto;
import com.dykim.base.member.dto.MemberInsertRspDto;
import com.dykim.base.member.dto.MemberSelectListRspDto;
import com.dykim.base.member.dto.MemberSelectRspDto;
import com.dykim.base.member.dto.MemberUpdateReqDto;
import com.dykim.base.member.dto.MemberUpdateRspDto;
import com.dykim.base.member.service.MemberService;
import com.dykim.base.sample.hello.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Tag(name = "Member Controller", description = "회원 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/member")
@Controller
public class MemberController {

    private final MemberService memberService;

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
    @Operation(summary = "Insert Member", description = "회원 추가")
    @ResponseBody
    @PutMapping
    public ApiResult<MemberInsertRspDto> insert(@Valid @RequestBody MemberInsertReqDto reqDto) {
        return ok(memberService.insert(reqDto));
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid mbrId type. MethodArgumentTypeMismatchException occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Unexpected exception occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class)))
            })
    @Operation(summary = "Select Member", description = "회원 조회")
    @ResponseBody
    @GetMapping("{mbrId}")
    public ApiResult<MemberSelectRspDto> select(@PathVariable Long mbrId) {
        return ok(memberService.select(mbrId));
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
                @ApiResponse(
                        responseCode = "500",
                        description = "Unexpected exception occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class)))
            })
    @Operation(summary = "Select Member List", description = "회원 목록 조회")
    @ResponseBody
    @GetMapping
    public ApiResult<MemberSelectListRspDto> selectList(@RequestParam String name) {
        return ok(memberService.selectList(name));
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request parameters or invalid update data.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found member by mbrId.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Unexpected exception occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class)))
            })
    @Operation(summary = "update Member", description = "회원 수정(온전한 값이 입력된 컬럼만 업데이트)")
    @ResponseBody
    @PostMapping("/{mbrId}")
    public ApiResult<MemberUpdateRspDto> update(
            @PathVariable Long mbrId, @RequestBody MemberUpdateReqDto reqDto) {
        return ApiResult.ok(memberService.update(mbrId, reqDto));
    }

    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid mbrId type. MethodArgumentTypeMismatchException occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found member by mbrId.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class))),
                @ApiResponse(
                        responseCode = "500",
                        description = "Unexpected exception occurred.",
                        content = @Content(schema = @Schema(implementation = ApiResult.class)))
            })
    @Operation(summary = "delete Member", description = "회원 삭제(useYn=N 처리)")
    @ResponseBody
    @DeleteMapping("/{mbrId}")
    public ApiResult<MemberDeleteRspDto> delete(@PathVariable Long mbrId) {
        return ApiResult.ok(memberService.delete(mbrId));
    }
}
