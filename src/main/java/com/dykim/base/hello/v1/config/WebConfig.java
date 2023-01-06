package com.dykim.base.hello.v1.config;

import com.dykim.base.hello.v1.config.interceptor.DebounceInterceptor;
import com.dykim.base.hello.v1.config.interceptor.HelloInterceptor;
import com.dykim.base.hello.v1.config.interceptor.PerformanceInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HelloInterceptor())
                .order(1)
                .addPathPatterns("/hello/v1/**");

        registry.addInterceptor(new DebounceInterceptor())
                .order(2)
                .addPathPatterns("/hello/v1/**");

        registry.addInterceptor(new PerformanceInterceptor())
                .order(Integer.MAX_VALUE)
                .addPathPatterns("/hello/v1/**");
    }

}
