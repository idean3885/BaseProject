package com.dykim.base.controller.front.sample;

import com.dykim.base.consts.uris.SampleFrontUris;
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

@Tag(name = "Front Controller", description = "thymeleaf 환경 구성용 컨트롤러")
@RequiredArgsConstructor
@Controller
public class SampleFrontController {

    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Success. Return view",
                        content = @Content(schema = @Schema(implementation = String.class)))
            })
    @Operation(summary = "request sample view", description = "SampleView with default layout")
    @GetMapping(SampleFrontUris.OVERVIEW)
    public String overview(Model model) {
        var resultList = List.of("AAA", "BBB", "CCC", "DDD", "EEE", "FFF");
        model.addAttribute("resultList", resultList);
        return "contents/sample/sampleOverview";
    }
}
