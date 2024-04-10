package com.dykim.base.util;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.test.web.servlet.MvcResult;

/**
 *
 *
 * <h3>Test Advice Util</h3>
 *
 * 테스트 코드에서 사용되는 공통 로직 유틸
 *
 * <p>아래 조건에 맞는 경우만 등록한다.<br>
 * <b>등록 조건</b>
 *
 * <pre>
 *  1. 2개 이상의 테스트 클래스에서 사용되는 경우
 * </pre>
 *
 * <b>참고) 조건에 맞는 로직만 등록하는 이유</b>
 *
 * <pre>
 *  - 가능성을 보고 유틸로 미리 이관한다면 다른 클래스에서 사용되기 전까진
 *    관리범위만 늘어나게 된다. (테스트 클래스 + 공통 유틸 클래스)
 *  - 또한 공통 유틸에 등록하는 것보다 공통 유틸에서 제거하는 것이 더 어렵기 때문에
 *    조건에 맞는 확실한 경우만 등록시킨다.(기존 로직에 영향을 주지 않도록 제거해야하기 때문임.)
 * </pre>
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
        log.debug(
                "printExceptionMessage: {}",
                Objects.requireNonNull(result.getResolvedException()).getLocalizedMessage());
    }
}
