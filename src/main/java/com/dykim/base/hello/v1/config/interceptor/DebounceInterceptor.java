package com.dykim.base.hello.v1.config.interceptor;

import com.dykim.base.hello.v1.config.Debounce;
import com.dykim.base.hello.v1.config.advice.exception.HandlerDebounceException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * <h3>Debouce Interceptor</h3>
 *  API 다중호출 방지용 인터셉터<br/>
 *  JS 의 디바운스 방식으로 구현<br/>
 *  컨트롤러 메소드 별 어노테이션으로 관리하기 위해 핸들러매핑 이후 실행 전 체크한다.
 * <pre>
 *  1. Debounce 대상 여부
 *  2. 세션 획득 - 없는 경우 생성(순수 디바운스 기능만 확인, 로그인 구현 후 리팩토링 필요)
 *  3. debounceMap 조회
 *  4. 최종 호출시간 조회
 *  5. 최종 호출시간 갱신
 *  6. 디바운싱
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
 * <pre>
 * <b>특이사항)온전한 기능확인을 위해 세션이 없는 경우 생성한다.</b>
 * <b>따라서 세션 구현 후 세션 획득 로직을 변경해야 한다.</b>
 * </pre>
 */
@Slf4j
@Component
public class DebounceInterceptor implements HandlerInterceptor {

    private static final String DEBOUNCE_MAP = "api-call-debounce-map";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        var stopWatch = new StopWatch("debounceInterceptor");
        try {
            // 0. 핸들러메소드 검증
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
            if (debounce.value() <= 0) {
                log.error("Invalid debounce time: {}ms. request(URI: {})is debounce pass.", debounce.value(), requestURI);
                return true;
            }

            // 2. 세션 검증
            stopWatch.start("getSession - isCreate:false");
            var session = request.getSession(false);
            var currentTimeMillis = System.currentTimeMillis();
            stopWatch.stop();
            if (session == null) {
                log.error("Request session is invalid. Debounce pass.");
                return true;
            }

            // 3. debounceMap 조회
            stopWatch.start("getAttribute - debounceMap");
            var debounceMapObject = session.getAttribute(DEBOUNCE_MAP);
            stopWatch.stop();

            stopWatch.start("get debounceMap");
            var debounceMap = new HashMap<String, Long>();
            if (debounceMapObject != null) {
                debounceMap = (HashMap<String, Long>) debounceMapObject; // 캐스팅 에러를 감수하고 최소한의 조건만 사용
            }
            stopWatch.stop();

            // 4. 최종 호출시간 조회
            stopWatch.start("get lastCallTimeMillis - map.get()");
            var lastCallTimeMillis = debounceMap.get(requestURI);
            stopWatch.stop();

            // 5. 최종 호출시간 갱신
            stopWatch.start("set lastCallTimeMillis - map.set()");
            debounceMap.put(requestURI, currentTimeMillis);
            stopWatch.stop();

            stopWatch.start("setAttribute - debounceMap");
            session.setAttribute(DEBOUNCE_MAP, debounceMap);
            stopWatch.stop();

            log.info(stopWatch.prettyPrint());

            // 6. 디바운싱
            if (lastCallTimeMillis == null || lastCallTimeMillis + debounce.value() <= currentTimeMillis) {
                return true;
            }
            var remainingTimeMillis = lastCallTimeMillis + debounce.value() - currentTimeMillis;
            log.error("Api called before debounce time(remaining: {}ms). Reset debounce {}ms", remainingTimeMillis, debounce.value());
            throw new HandlerDebounceException(String.format("Api called before debounce time(remaining: %dms). Reset debounce %dms"
                            , remainingTimeMillis, debounce.value()));
        } catch (HandlerDebounceException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Invalid Debounce process. Debounce is bypass.");
        }

        return true;
    }

}
