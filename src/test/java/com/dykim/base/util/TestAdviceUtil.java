package com.dykim.base.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * <h3>Test Advice Util</h3>
 * 테스트 코드에서 사용되는 공통 로직 유틸<p/>
 *
 * <b>주의) 2개 이상의 클래스에서 사용될 가능성이 있는 로직만 추가할 것</b>
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestAdviceUtil {

    public static void printRspDto(MvcResult handler) {
        try {
            log.debug("printRspDto: {}", handler.getResponse().getContentAsString());
        } catch (UnsupportedEncodingException e) {
            log.error("Fail contentAsString from response.", e);
        }
    }

    public static <T> String getRspName(Class<T> rspDto) {
        return rspDto.getSimpleName();
    }

    public static Class<? extends Exception> getApiResultExceptionClass(MvcResult result) {
        return Objects.requireNonNull(result.getResolvedException()).getClass();
    }

    public static void printExceptionMessage(MvcResult result) {
        log.debug("printExceptionMessage: {}", Objects.requireNonNull(result.getResolvedException()).getLocalizedMessage());
    }

}
