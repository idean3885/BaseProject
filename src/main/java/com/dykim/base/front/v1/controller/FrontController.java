package com.dykim.base.front.v1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Tag(name = "Front Controller", description = "thymeleaf 환경 구성용 컨트롤러")
@RequiredArgsConstructor
@RequestMapping("/front/v1")
@Controller
public class FrontController {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success. Return view",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @Operation(summary = "request home view", description = "request home view code")
    @GetMapping("/home")
    public ModelAndView goHome() {
        var mav = new ModelAndView();
        var resultList = List.of("AAA", "BBB", "CCC", "DDD", "EEE", "FFF");
        mav.addObject("resultList", resultList);
        mav.setViewName("content/home");
        return mav;
    }

}
