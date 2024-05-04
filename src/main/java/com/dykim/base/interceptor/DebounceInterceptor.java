package com.dykim.base.interceptor;

import com.dykim.base.advice.common.exception.HandlerDebounceException;
import com.dykim.base.config.annotation.Debounce;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 *
 *
 * <h3>Debouce Interceptor</h3>
 *
 * API 다중호출 방지용 인터셉터<br>
 * JS 의 디바운스 방식으로 구현<br>
 * 컨트롤러 메소드 별 어노테이션으로 관리하기 위해 핸들러매핑 이후 실행 전 체크한다.
 *
 * <pre>
 *  1. 핸들러메소드 검증
 *  2. Debounce 대상 여부
 *  3. 세션 획득 - 없는 경우 생성하지 않음.
 *  4. debounceMap header 조회
 *  4-2. 최초 호출 확인 - 동기화 처리
 *  5. debounceMap header 재조회
 *   1) 최종 호출시간 조회
 *   2) 최종 호출시간 갱신
 *  6. 디바운싱
 * </pre>
 *
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
 *
 * <pre>
 * <b>참고)빈 세션으로 응답 성공 시, 세션이 활성화된다.</b>
 * <b>따라서 최초 호출 이후 재호출 시에는 세션은 유효한 상태가 된다.</b>
 * </pre>
 *
 * @see <a href="https://github.com/idean3885/BaseProejct/issues/6">[Feature]DebounceInterceptor 구현
 *     - API 다중호출 방어 Git Issue</a>
 * @see <a
 *     href="https://velog.io/@idean3885/API-%EB%8B%A4%EC%A4%91-%ED%98%B8%EC%B6%9C-%EC%9D%B4%EC%8A%88-%EC%B2%98%EB%A6%AC">API
 *     다중 호출 이슈 처리 Velog</a>
 */
@Slf4j
@Component
public class DebounceInterceptor implements HandlerInterceptor {

    private static final String DEBOUNCE_MAP = "api-call-debounce-map";

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        var stopWatch = new StopWatch("debounceInterceptor");
        try {
            // 1. 핸들러메소드 검증
            stopWatch.start("1. 핸들러메소드 검증");
            if (!(handler instanceof HandlerMethod)) {
                return true;
            }
            stopWatch.stop();

            // 2. Debounce 대상 여부
            stopWatch.start("2. Debounce 대상 여부");
            var handlerMethod = (HandlerMethod) handler;
            Debounce debounce = handlerMethod.getMethodAnnotation(Debounce.class);
            if (debounce == null) {
                return true;
            }
            var requestURI = request.getRequestURI();
            if (debounce.value() <= 0) {
                log.error(
                        "Invalid debounce time: {}ms. request(URI: {})is debounce pass.",
                        debounce.value(),
                        requestURI);
                return true;
            }
            stopWatch.stop();

            // 3. 세션 검증
            stopWatch.start("3. 세션 검증");
            var session = request.getSession(false);
            var currentTimeMillis = System.currentTimeMillis();
            if (session == null) {
                log.error("Request session is invalid. Debounce pass.");
                return true;
            }
            stopWatch.stop();

            // 4. debounceMap header 조회
            stopWatch.start("4. debounceMap header 조회");
            var debounceMapObject = session.getAttribute(DEBOUNCE_MAP);
            var debounceMap = new HashMap<String, Long>();
            Long lastCallTimeMillis = null;
            stopWatch.stop();

            // 4-2. 최초 호출 확인 - 동기화 처리
            // 최초 호출 시 먼저 진입한 세션만 성공처리될 수 있도록 동기화 블록 설정함.
            if (debounceMapObject == null) {
                stopWatch.start("4-2. 최초 호출 확인 - 동기화 처리 진입");
                synchronized (DEBOUNCE_MAP) {
                    stopWatch.stop();
                    debounceMapObject = session.getAttribute(DEBOUNCE_MAP);
                    // 동기화 처리 중 다른 쓰레드가 대기하기 때문에 진입 시 한번 더 확인함.
                    if (debounceMapObject == null) {
                        stopWatch.start("4-2. 최초 호출 확인 - 동기화 처리 수행");
                        debounceMap.put(requestURI, currentTimeMillis);
                        session.setAttribute(DEBOUNCE_MAP, debounceMap);
                        stopWatch.stop();
                    }
                }
            }

            // 5. debounceMap header 재조회
            // 싱크로나이즈 블록에서 병렬처리로 인해 debounceMapObject 가 설정될 수 있기 때문에 다시 체크함.
            stopWatch.start("5. debounceMap header 재조회");
            if (debounceMapObject instanceof HashMap) {
                debounceMap = (HashMap<String, Long>) debounceMapObject; // 캐스팅 에러를 감수하고 최소한의 조건만 사용
                // 1) 최종 호출시간 조회
                lastCallTimeMillis = debounceMap.get(requestURI);

                // 2) 최종 호출시간 갱신
                debounceMap.put(requestURI, currentTimeMillis);
                session.setAttribute(DEBOUNCE_MAP, debounceMap);
            }
            stopWatch.stop();

            // 6. 디바운싱
            if (lastCallTimeMillis == null
                    || lastCallTimeMillis + debounce.value() <= currentTimeMillis) {
                return true;
            }
            var remainingTimeMillis = lastCallTimeMillis + debounce.value() - currentTimeMillis;
            log.error(
                    "Api called before debounce time(remaining: {}ms). Reset debounce {}ms",
                    remainingTimeMillis,
                    debounce.value());
            throw new HandlerDebounceException(
                    String.format(
                            "Api called before debounce time(remaining: %dms). Reset debounce %dms",
                            remainingTimeMillis, debounce.value()));
        } catch (HandlerDebounceException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Invalid Debounce process. Debounce is bypass.");
        } finally {
            log.debug(stopWatch.prettyPrint());
        }

        return true;
    }
}
