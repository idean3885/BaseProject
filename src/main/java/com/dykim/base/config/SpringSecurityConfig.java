package com.dykim.base.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    private final String[] SWAGGER_PATTERNS = {"/api-docs/**", "/swagger-ui/**"};
    private final String[] SAMPLE_PATTERNS = {"/sample/**"};
    private final String[] ANONYMOUS_PATTERNS = {"/member/signUp", "/member/signIn"};

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests(request -> request
//                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll() // Spring Boot 3.0 부터 적용된 시큐리티 6.0 이상 필수 설정으로 우선 제외
                                .antMatchers(SWAGGER_PATTERNS).permitAll()
                                .antMatchers(SAMPLE_PATTERNS).permitAll()
                                .antMatchers(ANONYMOUS_PATTERNS).permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .defaultSuccessUrl("/sample/front/sampleWithDefaultLayout", true)
                        .permitAll()
                )
                .logout(Customizer.withDefaults());
        return httpSecurity.build();
    }

}
