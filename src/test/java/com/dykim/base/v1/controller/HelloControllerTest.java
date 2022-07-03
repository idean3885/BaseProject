package com.dykim.base.v1.controller;

import static org.hamcrest.Matchers.is;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = HelloController.class)
public class HelloControllerTest {

    @Autowired
    private MockMvc mvc;

    @Order(1)
    @Test
    public void _1_헬로출력() throws Exception{
        String hello = "hello!";

        mvc.perform(get("/v1/hello/helloPrint"))
                .andExpect(status().isOk())
                .andExpect(content().string(hello));
    }

    @Order(2)
    @Test
    public void _2_헬로_응답_DTO() throws Exception {
        var name = "name";
        var email = "test@email.com";

        // when
        mvc.perform(get("/v1/hello/helloDto")
                .param("name", name)
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.email", is(email)));
    }
}
