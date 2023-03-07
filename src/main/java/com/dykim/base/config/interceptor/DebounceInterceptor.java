package com.dykim.base.config.interceptor;

import com.dykim.base.config.annotation.Debounce;
import com.dykim.base.advice.common.exception.HandlerDebounceException;
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
 *  2. 세션 획득 - 없는 경우 생성하지 않음.
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
 * <b>참고)빈 세션으로 응답 성공 시, 세션이 활성화된다.</b>
 * <b>따라서 최초 호출 이후 재호출 시에는 세션은 유효한 상태가 된다.</b>
 * </pre>
 *
 * @see <a href="https://github.com/idean3885/BaseProejct/issues/6">[Feature]DebounceInterceptor 구현 - API 다중호출 방어 Git Issue</a>
 * @see <a href="https://velog.io/@idean3885/API-%EB%8B%A4%EC%A4%91-%ED%98%B8%EC%B6%9C-%EC%9D%B4%EC%8A%88-%EC%B2%98%EB%A6%AC">API 다중 호출 이슈 처리 Velog</a>
 *
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
