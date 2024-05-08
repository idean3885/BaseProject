package com.dykim.base.controller.front;

import com.dykim.base.consts.uris.SampleFrontUris;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 *
 * <h3>Index controller</h3>
 *
 * @author dongyoung.kim
 * @since 1.0
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("frontIndex", true);
        return String.format("redirect:%s", SampleFrontUris.OVERVIEW);
    }
}
