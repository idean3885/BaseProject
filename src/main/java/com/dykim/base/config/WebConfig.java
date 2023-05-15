package com.dykim.base.config;

import com.dykim.base.config.interceptor.SessionValidationInterceptor;
import com.dykim.base.config.interceptor.DebounceInterceptor;
import com.dykim.base.config.interceptor.PerformanceInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final List<String> EXCLUDE_PATTERNS = List.of("/swagger-ui/**");

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionValidationInterceptor())
                .order(1)
                .excludePathPatterns(EXCLUDE_PATTERNS);
        registry.addInterceptor(new DebounceInterceptor())
                .order(2)
                .addPathPatterns("/sample/hello/**"); // api 에만 디바운스 처리하도록 분기하자.
        registry.addInterceptor(new PerformanceInterceptor())
                .order(Integer.MAX_VALUE)
                .excludePathPatterns(EXCLUDE_PATTERNS);
    }

}
