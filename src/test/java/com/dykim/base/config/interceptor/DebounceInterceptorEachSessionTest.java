package com.dykim.base.config.interceptor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dykim.base.advice.common.CommonControllerAdvice;
import com.dykim.base.interceptor.DebounceInterceptor;
import com.dykim.base.sample.hello.controller.HelloController;
import com.dykim.base.sample.hello.entity.HelloRepository;
import com.dykim.base.sample.hello.service.HelloService;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 *
 *
 * <h3>DebounceInterceptor 다중 세션 테스트</h3>
 *
 * 각 세션 별 Api Debouncing 테스트
 *
 * <pre>
 *  - 반복을 위해 @RepeatedTest 사용
 *  - 매 반복 시 독립적인 Test 로 취급하기 때문에 전역 변수를 메소드 단위로 구분할 수 없다.
 *    ㄴ @BeforeEach, @AfterEach 매 회 수행됌
 *  - 따라서 반복시행에 대해 Thread-safe 변수 활용을 위해 클래스 당 1개의 반복 메소드만 작성한다.
 * </pre>
 *
 * @see DebounceInterceptor
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DebounceInterceptorEachSessionTest {

    @Mock private HelloRepository helloRepository;

    @InjectMocks private HelloService helloService;

    private MockMvc mockMvc;
    private final int REPEAT_COUNT = 100;

    @BeforeAll
    public void setupGlobalField() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new HelloController(helloService))
                        .addInterceptors(new DebounceInterceptor())
                        .setControllerAdvice(new CommonControllerAdvice())
                        .build();
    }

    @Execution(ExecutionMode.CONCURRENT)
    @RepeatedTest(REPEAT_COUNT)
    public void call_helloPrintDebounce_always_new_session() throws Exception {
        // given
        var newMockHttpSession = new MockHttpSession();

        // when
        mockMvc
                .perform(get("/sample/hello/helloPrintDebounce").session(newMockHttpSession))
                // then
                .andExpect(status().isOk())
                .andExpect(content().string(Json.pretty("hello!")));
    }
}
