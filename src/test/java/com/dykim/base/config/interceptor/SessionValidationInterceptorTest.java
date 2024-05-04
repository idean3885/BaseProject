package com.dykim.base.config.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dykim.base.advice.common.CommonControllerAdvice;
import com.dykim.base.advice.common.exception.InvalidSessionException;
import com.dykim.base.interceptor.SessionValidationInterceptor;
import com.dykim.base.sample.hello.controller.HelloController;
import com.dykim.base.sample.hello.entity.HelloRepository;
import com.dykim.base.sample.hello.service.HelloService;
import com.dykim.base.util.TestAdviceUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 *
 * <h3>SessionValidationInterceptor 테스트 클래스</h3>
 *
 * Api 세션 검증을 위한 인터셉터 테스트
 *
 * <p>
 *
 * <pre>
 *  - API 요청이 필수이기 때문에 컨트롤러와 연관됨.
 *  - 하지만 세션 검증자체가 주된 목적이기 때문에 테스트코드를 따로 작성함.</b>
 * </pre>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SessionValidationInterceptorTest {

    @Mock private HelloRepository helloRepository;

    @InjectMocks private HelloService helloService;

    private MockMvc mockMvc;

    @BeforeAll
    public void setup() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new HelloController(helloService))
                        .addInterceptors(new SessionValidationInterceptor())
                        .setControllerAdvice(new CommonControllerAdvice())
                        .build();
    }

    @Order(1)
    @Test
    public void call_validSession_valid_session_return_true_string() throws Exception {
        // given
        var newMockHttpSession = new MockHttpSession();

        // when
        mockMvc
                .perform(get("/sample/hello/validSession").session(newMockHttpSession))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Order(2)
    @Test
    public void call_validSession_invalid_session_throw_InvalidSessionException() throws Exception {
        // when
        mockMvc
                .perform(get("/sample/hello/validSession"))
                // then
                .andExpect(status().isUnauthorized())
                .andExpect(
                        result ->
                                assertThat(TestAdviceUtil.getApiResultExceptionClass(result))
                                        .isEqualTo(InvalidSessionException.class))
                .andDo(TestAdviceUtil::printExceptionMessage);
    }
}
