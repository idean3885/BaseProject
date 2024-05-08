package com.dykim.base.config.web;

import com.dykim.base.interceptor.DebounceInterceptor;
import com.dykim.base.interceptor.PerformanceInterceptor;
import com.dykim.base.interceptor.SessionValidationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        final String SAMPLE_PATH_PATTERN = "**/sample/**";
        registry
                .addInterceptor(new SessionValidationInterceptor())
                .order(1)
                .addPathPatterns(SAMPLE_PATH_PATTERN);
        registry
                .addInterceptor(new DebounceInterceptor())
                .order(2)
                .addPathPatterns(SAMPLE_PATH_PATTERN);
        registry
                .addInterceptor(new PerformanceInterceptor())
                .order(Integer.MAX_VALUE)
                .addPathPatterns(SAMPLE_PATH_PATTERN);
    }
}
