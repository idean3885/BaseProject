package com.dykim.base.hello.v1.config;

import com.dykim.base.hello.v1.config.advice.InterceptorControllerAdvice;
import com.dykim.base.hello.v1.config.interceptor.DebounceInterceptor;
import com.dykim.base.hello.v1.controller.HelloController;
import com.dykim.base.hello.v1.entity.HelloRepository;
import com.dykim.base.hello.v1.service.HelloService;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <h3>DebounceInterceptor 테스트 클래스</h3>
 * Api 다중호출 방지를 위한 인터셉터 테스트
 * <pre>
 *  - 다중호출 방지를 위해 성능 검증이 필수
 *  - 이를 테스트하기 위해 실제 서버를 구동하는 방식으로 진행
 * </pre>
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DebounceInterceptorTest {

    @Mock
    private HelloRepository helloRepository;

    @InjectMocks
    private HelloService helloService;

    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new HelloController(helloService))
                .addInterceptors(new DebounceInterceptor())
                .setControllerAdvice(new InterceptorControllerAdvice())
                .build();
        mockHttpSession = new MockHttpSession();
    }

    @Order(1)
    @RepeatedTest(10)
    public void call_helloPrintDebounce_always_new_session() throws Exception {
        // given
        var newMockHttpSession = new MockHttpSession();

        // when
        mockMvc.perform(get("/hello/v1/helloPrintDebounce").session(newMockHttpSession))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(Json.pretty("hello!")));
    }

    @Order(2)
    @RepeatedTest(20)
    public void call_helloPrintDebounce_first_debounce(RepetitionInfo repetitionInfo) throws Exception {
        // when
        mockMvc.perform(get("/hello/v1/helloPrintDebounce").session(mockHttpSession))
                // then
                .andDo(handler -> {
                    var httpStatus = handler.getResponse().getStatus();
                    var currentCount = repetitionInfo.getCurrentRepetition();
                    assertThat(httpStatus).isEqualTo(currentCount == 1 ?
                            HttpStatus.OK.value() : HttpStatus.TOO_MANY_REQUESTS.value());
                    var rspString = handler.getResponse().getContentAsString();
                    if (httpStatus == HttpStatus.OK.value() && currentCount == 1) {
                        assertThat(rspString).isEqualTo(Json.pretty("hello!"));
                    }
                });
    }

}
