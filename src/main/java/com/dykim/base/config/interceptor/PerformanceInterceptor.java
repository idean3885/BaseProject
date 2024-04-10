package com.dykim.base.config.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 *
 * <h3>Performance Interceptor</h3>
 *
 * API 성능 측정용 인터셉터
 *
 * <pre>
 *  - 온전히 성공한 로직에 대해서만 속도를 측정한다.
 *    => postHandle 에서 측정한 시간 출력으로 구현
 *
 *  <b>참고) 온전한 API 요청처리 시간을 측정하기 위해 해당 인터셉터는
 *          맨마지막에 등록시켜 실행순서를 보장해야 한다.</b>
 *
 *   예)
 *    A인터셉터 preHandle -> B인터셉터 preHandle
 *    -> PerformanceInterceptor.preHandle
 *    -> HandlerAdaptor 를 핸들러 메소드(컨트롤러 로직) 수행
 *    -> PerformanceInterceptor.postHandle(HandleAdaptor 예외없이 성공한 경우만 수행)
 *    -> B 인터셉터 postHandle
 *    ...
 * </pre>
 */
@Slf4j
public class PerformanceInterceptor implements HandlerInterceptor {

    private static final String PROCESS_TIME_MILLIS = "process-time-millis";

    @Override
    public boolean preHandle(
            HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        request.setAttribute(PROCESS_TIME_MILLIS, System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @Nullable Object handler,
            @Nullable ModelAndView modelAndView) {
        var completeHandlerAdaptorTimeMillis = System.currentTimeMillis();
        var requestTimeMillis = (Long) request.getAttribute(PROCESS_TIME_MILLIS);
        log.info(
                "Handler proceed success. process time: {}ms",
                completeHandlerAdaptorTimeMillis - requestTimeMillis);
    }
}
