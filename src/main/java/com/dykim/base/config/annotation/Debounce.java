package com.dykim.base.config.annotation;

import com.dykim.base.config.interceptor.DebounceInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h3>Debounce Annotation</h3>
 * Api 다중호출 방지용 어노테이션
 *
 * @see DebounceInterceptor
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Debounce {

    /**
     * Debounce time millisecond.
     *
     * @return Debounce time millisecond; must be greater than zero
     */
    int value();

}
