package com.dykim.base.member.controller;

import com.dykim.base.member.dto.*;
import com.dykim.base.member.service.MemberService;
import com.dykim.base.sample.hello.dto.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.dykim.base.sample.hello.dto.ApiResult.ok;

@Tag(name = "Member Controller", description = "회원 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/member")
@Controller
public class MemberController {

    private final MemberService memberService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = MemberInsertRspDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or invalid insert data.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected exception occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @Operation(summary = "Insert Member", description = "회원 추가")
    @ResponseBody
    @PutMapping
    public ApiResult<MemberInsertRspDto> insert(@Valid @RequestBody MemberInsertReqDto reqDto) {
        return ok(memberService.insert(reqDto));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = MemberSelectRspDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid mbrId type. MethodArgumentTypeMismatchException occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected exception occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @Operation(summary = "Select Member", description = "회원 조회")
    @ResponseBody
    @GetMapping("{mbrId}")
    public ApiResult<MemberSelectRspDto> select(@PathVariable Long mbrId) {
        return ok(memberService.select(mbrId));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = MemberSelectListRspDto.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected exception occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @Operation(summary = "Select Member List", description = "회원 목록 조회")
    @ResponseBody
    @GetMapping
    public ApiResult<MemberSelectListRspDto> selectList(@RequestParam String mbrNm) {
        return ok(memberService.selectList(mbrNm));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = MemberUpdateRspDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters or invalid update data.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "Not Found member by mbrId.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected exception occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @Operation(summary = "update Member", description = "회원 수정(온전한 값이 입력된 컬럼만 업데이트)")
    @ResponseBody
    @PostMapping("/{mbrId}")
    public ApiResult<MemberUpdateRspDto> update(@PathVariable Long mbrId, @RequestBody MemberUpdateReqDto reqDto) {
        return ApiResult.ok(memberService.update(mbrId, reqDto));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success",
                    content = @Content(schema = @Schema(implementation = MemberDeleteRspDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid mbrId type. MethodArgumentTypeMismatchException occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "404", description = "Not Found member by mbrId.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "500", description = "Unexpected exception occurred.",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @Operation(summary = "delete Member", description = "회원 삭제(useYn=N 처리)")
    @ResponseBody
    @DeleteMapping("/{mbrId}")
    public ApiResult<MemberDeleteRspDto> delete(@PathVariable Long mbrId) {
        return ApiResult.ok(memberService.delete(mbrId));
    }

}
