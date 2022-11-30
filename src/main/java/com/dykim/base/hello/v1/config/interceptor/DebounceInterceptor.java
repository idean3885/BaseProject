package com.dykim.base.hello.v1.config.interceptor;

import com.dykim.base.hello.v1.config.Debounce;
import com.dykim.base.hello.v1.config.advice.exception.HandlerDebounceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * <h3>Debouce Interceptor</h3>
 *  API 다중호출 방지용 인터셉터<br/>
 *  JS 의 디바운스 방식으로 구현<br/>
 *  컨트롤러 메소드 별 어노테이션으로 관리하기 위해 핸들러매핑 이후 실행 전 체크한다.
 * <pre>
 *  1. 컨트롤러 메소드 별 @Debounce 어노테이션 확인
 *  2. 접근한 세션의 API 최종 호출시간 확인
 *  3. 최종호출 이후 일정시간동안 API 호출되는 경우 최종호출시간 갱신 후 예외처리
 * </pre>
 * <pre>
 * API 호출 데이터 양식
 * 요청한 세션: {
 *     debounceMap: {
 *         url1: last call time Millis,
 *         url2: last call time Millis,
 *         ...
 *     }
 * }
 * </pre>
 */
@Slf4j
@Component
public class DebounceInterceptor implements HandlerInterceptor {

    private static final String DEBOUNCE_MAP = "debounceMap";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            // 0. 실행여부
            if (!(handler instanceof HandlerMethod)) {
                return true;
            }

            // 1. Debounce 대상 여부
            var handlerMethod = (HandlerMethod) handler;
            Debounce debounce = handlerMethod.getMethodAnnotation(Debounce.class);
            if (debounce == null) {
                return true;
            }

            var requestURI = request.getRequestURI();
            if (debounce.value() == 0) {
                log.error("Debounce time is 0ms. Debounce pass. requestURI: {}", requestURI);
                return true;
            }

            // 2. 세션 획득 - 없는 경우 생성(순수 디바운스 기능만 확인, 로그인 구현 후 리팩토링 필요)
            var session = request.getSession();
            var currentTimeMillis = System.currentTimeMillis();

            // 3. debounceMap 체크
            var debounceMapObject = session.getAttribute(DEBOUNCE_MAP);
            if (debounceMapObject == null) {
                var debounceMap = new HashMap<String, Long>();
                debounceMap.put(requestURI, currentTimeMillis);
                session.setAttribute(DEBOUNCE_MAP, debounceMap);
                return true;
            }

            var debounceMap = (Map<String, Long>) debounceMapObject;

            var lastCallTimeMillis = debounceMap.get(requestURI);
            if (lastCallTimeMillis == null) {
                debounceMap.put(requestURI, lastCallTimeMillis);
                session.setAttribute(DEBOUNCE_MAP, debounceMap);
                return true;
            } else if (lastCallTimeMillis + debounce.value() <= currentTimeMillis) {
                debounceMap.put(requestURI, currentTimeMillis);
                session.setAttribute(DEBOUNCE_MAP, debounceMap);
                return true;
            }

            debounceMap.put(requestURI, currentTimeMillis);
            session.setAttribute(DEBOUNCE_MAP, debounceMap);
            throw new HandlerDebounceException(
                    String.format("Api call time has not elapsed. Remaining time %dms"
                            , lastCallTimeMillis + debounce.value() - currentTimeMillis));
        } catch (HandlerDebounceException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Debounce is bypass.");
        }

        return true;
    }

}
