package com.dykim.base.hello.v1.config;

import com.dykim.base.hello.v1.config.advice.InterceptorControllerAdvice;
import com.dykim.base.hello.v1.config.advice.exception.InvalidSessionException;
import com.dykim.base.hello.v1.config.interceptor.SessionValidationInterceptor;
import com.dykim.base.hello.v1.controller.HelloController;
import com.dykim.base.hello.v1.entity.HelloRepository;
import com.dykim.base.hello.v1.service.HelloService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <h3>SessionValidationInterceptor 테스트 클래스</h3>
 * Api 세션 검증을 위한 인터셉터 테스트<p/>
 *
 * <pre>
 *  - API 요청이 필수이기 때문에 컨트롤러와 연관됨.
 *  - 하지만 세션 검증자체가 주된 목적이기 때문에 테스트코드를 따로 작성함.</b>
 * </pre>
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SessionValidationInterceptorTest {

    @Mock
    private HelloRepository helloRepository;

    @InjectMocks
    private HelloService helloService;

    private MockMvc mockMvc;

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new HelloController(helloService))
                .addInterceptors(new SessionValidationInterceptor())
                .setControllerAdvice(new InterceptorControllerAdvice())
                .build();
    }

    @Order(1)
    @Test
    public void call_validSession_valid_session_return_true_string() throws Exception {
        // given
        var newMockHttpSession = new MockHttpSession();

        // when
        mockMvc.perform(get("/hello/v1/validSession").session(newMockHttpSession))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Order(2)
    @Test
    public void call_validSession_invalid_session_throw_InvalidSessionException() throws Exception {
        // when
        mockMvc.perform(get("/hello/v1/validSession"))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertThat(getApiResultExceptionClass(result))
                        .isEqualTo(InvalidSessionException.class)
                )
                .andDo(this::printExceptionMessage);
    }

    private Class<? extends Exception> getApiResultExceptionClass(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getClass();
    }

    private void printExceptionMessage(MvcResult result) {
        log.debug("result: {}", Objects.requireNonNull(result.getResolvedException()).getLocalizedMessage());
    }

}
