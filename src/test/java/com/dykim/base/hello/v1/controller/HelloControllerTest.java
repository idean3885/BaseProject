package com.dykim.base.hello.v1.controller;

import com.dykim.base.hello.v1.service.HelloService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HelloControllerTest {

    private MockMvc mockMvc;

    @BeforeAll
    public void setMockMvc() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new HelloController(new HelloService()))
                .build();
    }

    @Order(1)
    @Test
    public void 헬로출력() throws Exception{
        String hello = "hello!";

        mockMvc.perform(get("/hello/v1/helloPrint"))
                .andExpect(status().isOk())
                .andExpect(content().string(hello));
    }

    @Order(2)
    @Test
    public void 헬로_응답_DTO() throws Exception {
        var name = "name";
        var email = "test@email.com";

        // when
        mockMvc.perform(get("/hello/v1/helloDto")
                .param("name", name)
                .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.name", is(name)))
                .andExpect(jsonPath("$.response.email", is(email)));
    }

}
