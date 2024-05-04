package com.dykim.base.sample.front.controller;

import com.dykim.base.consts.FrontUris.Front;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Tag(name = "Front Controller", description = "thymeleaf 환경 구성용 컨트롤러")
@RequiredArgsConstructor
@Controller
public class FrontController {

    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Success. Return view",
                        content = @Content(schema = @Schema(implementation = String.class)))
            })
    @Operation(summary = "request sample view", description = "SampleView with default layout")
    @GetMapping(Front.OVERVIEW)
    public ModelAndView sampleWithDefaultLayout() {
        var mav = new ModelAndView();
        var resultList = List.of("AAA", "BBB", "CCC", "DDD", "EEE", "FFF");
        mav.addObject("resultList", resultList);
        mav.setViewName("contents/sampleWithDefaultLayout");
        return mav;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("fronIndex", true);
        return String.format("redirect:%s", Front.OVERVIEW);
    }
}
