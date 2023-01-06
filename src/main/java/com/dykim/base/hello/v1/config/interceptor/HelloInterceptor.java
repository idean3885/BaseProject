package com.dykim.base.hello.v1.config.interceptor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Component
public class HelloInterceptor implements HandlerInterceptor {

    private final List<String> byPassUrls = List.of("/hello/v1/helloDto");

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 1. Request Url By pass 확인
        for (String byPassUrl : byPassUrls) {
            // 1) startsWith
            if (request.getRequestURI().startsWith(byPassUrl)) {
                log.info("Session Validation By pass.");
                return true;
            }
        }

        // 2. Debounce 테스트용 세션생성 로그 request.getSession() -> 세션없으면 만들기 때문에 메소드 처리완료 후에는 항상 세션이 활성화된다.
        log.info("sessionId: [{}]requestURI: {}", request.getSession().getId(), request.getRequestURI());
        return true;
    }

}
