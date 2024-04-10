/*
 * Copyright 2023 NHN (https://nhn.com) and others.
 * © NHN Corp. All rights reserved.
 */

package com.dykim.base.config;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Message source config
 *
 * @author dongyoung.kim
 * @since 1.0
 */
@Configuration
public class MessageSourceConfig {

    private static final String COOKIE_LANG = "lang";

    @Bean
    public LocaleResolver localeResolver() {
        var resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.KOREAN);
        resolver.setCookieName(COOKIE_LANG);
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(
                "classpath:messages/messages", "classpath:messages/ValidationMessages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setCacheSeconds(10 * 60);
        return messageSource;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        var interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(COOKIE_LANG);
        return interceptor;
    }
}
