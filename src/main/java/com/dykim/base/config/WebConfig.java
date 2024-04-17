package com.dykim.base.config;

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
        registry
                .addInterceptor(new SessionValidationInterceptor())
                .order(1)
                .addPathPatterns("/sample/hello/**");
        registry.addInterceptor(new DebounceInterceptor()).order(2).addPathPatterns("/sample/hello/**");
        registry
                .addInterceptor(new PerformanceInterceptor())
                .order(Integer.MAX_VALUE)
                .addPathPatterns("/sample/hello/**");
    }
}
