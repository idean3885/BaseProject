package com.dykim.base.v1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/hello")
public class HelloController {

    @GetMapping("/helloPrint")
    public String helloPrint() {
        return "hello!";
    }
}
