package com.dykim.base.hello.v1.controller;

import com.dykim.base.hello.v1.controller.dto.HelloInsertReqDto;
import com.dykim.base.hello.v1.controller.dto.HelloInsertRspDto;
import com.dykim.base.hello.v1.controller.dto.HelloRspDto;
import com.dykim.base.hello.v1.domain.Hello;
import com.dykim.base.hello.v1.domain.HelloRepository;
import com.dykim.base.hello.v1.service.HelloService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HelloControllerTest {

    @Mock
    private HelloRepository helloRepository;

    @InjectMocks
    private HelloService helloService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new HelloController(helloService))
                .build();

        objectMapper = new ObjectMapper();
    }

    @Order(1)
    @Test
    public void 헬로출력() throws Exception{
        mockMvc.perform(get("/hello/v1/helloPrint"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello!"));
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(getRspName(HelloRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(name)))
                .andExpect(jsonPath("$.data.email", is(email)))
                .andDo(this::printRspDto);
    }

    @Order(3)
    @Test
    public void 헬로추가() throws Exception {
        // given
        var helloInsertReqDto = HelloInsertReqDto.builder()
                .email("email")
                .name("name")
                .build();
        var reqJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(helloInsertReqDto);
        var hello = Hello.builder()
                .email(helloInsertReqDto.getEmail())
                .name(helloInsertReqDto.getName()).build();

        // when
        when(helloRepository.save(any())).thenReturn(hello);

        // then
        mockMvc.perform(post("/hello/v1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(reqJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name", is(getRspName(HelloInsertRspDto.class))))
                .andExpect(jsonPath("$.data.name", is(helloInsertReqDto.getName())))
                .andExpect(jsonPath("$.data.email", is(helloInsertReqDto.getEmail())))
                .andDo(this::printRspDto);
    }

    private void printRspDto(MvcResult handler) {
        try {
            log.debug("result: {}", handler.getResponse().getContentAsString());
        } catch (UnsupportedEncodingException e) {
            log.error("Fail contentAsString from response.", e);
        }
    }

    private String getRspName(Class rspDtoClass) {
        return rspDtoClass.getSimpleName();
    }

}
