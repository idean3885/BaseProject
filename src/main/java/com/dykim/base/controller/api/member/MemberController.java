package com.dykim.base.controller.api.member;

import static com.dykim.base.dto.ApiResult.ok;

import com.dykim.base.consts.uris.MemberApiUris;
import com.dykim.base.dto.ApiResult;
import com.dykim.base.dto.member.MemberDeleteRspDto;
import com.dykim.base.dto.member.MemberInsertReqDto;
import com.dykim.base.dto.member.MemberInsertRspDto;
import com.dykim.base.dto.member.MemberSelectListRspDto;
import com.dykim.base.dto.member.MemberSelectRspDto;
import com.dykim.base.dto.member.MemberUpdateReqDto;
import com.dykim.base.dto.member.MemberUpdateRspDto;
import com.dykim.base.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Member Controller", description = "회원 컨트롤러")
@RequiredArgsConstructor
@RestController
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
    @PutMapping(MemberApiUris.INSERT)
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
    @GetMapping(MemberApiUris.SELECT + "/{mbrId}")
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
    @GetMapping(MemberApiUris.SELECT_LIST)
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
    @PostMapping(MemberApiUris.UPDATE + "/{mbrId}")
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
    @DeleteMapping(MemberApiUris.DELETE + "/{mbrId}")
    public ApiResult<MemberDeleteRspDto> delete(@PathVariable Long mbrId) {
        return ApiResult.ok(memberService.delete(mbrId));
    }
}
