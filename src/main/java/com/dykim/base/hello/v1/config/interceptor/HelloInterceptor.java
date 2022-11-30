package com.dykim.base.hello.v1.config.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Component
public class HelloInterceptor implements HandlerInterceptor {

    private final List<String> byPassUrls = List.of("/hello/v1/helloDto", "/hello/v1", "/hello/v1/*");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. Request Url By pass 확인
        if (byPassUrls.contains(request.getRequestURI())) {
            log.info("Session Validation By pass.");
            return true;
        }

        log.info("sessionId: {}", request.getSession().getId());
        log.info("requestURI: {}", request.getRequestURI());

        return true;
    }

}
