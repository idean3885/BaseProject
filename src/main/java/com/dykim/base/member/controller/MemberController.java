package com.dykim.base.member.controller;

import com.dykim.base.member.dto.MemberInsertReqDto;
import com.dykim.base.member.dto.MemberInsertRspDto;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
