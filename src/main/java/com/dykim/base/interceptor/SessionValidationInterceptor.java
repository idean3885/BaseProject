package com.dykim.base.interceptor;

import com.dykim.base.advice.common.exception.InvalidSessionException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class SessionValidationInterceptor implements HandlerInterceptor {

    private final List<String> byPassUrls = List.of("/sample/hello");

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) {
        var requestUri = request.getRequestURI();

        // 1. Request Url By pass 확인
        // TODO: 로그인 기능 구현 후 아래 코드 사용 및 byPass 범위 테스트케이스 수정 필요.
        //        for (String byPassUrl : byPassUrls) {
        //            if (request.getRequestURI().startsWith(byPassUrl)) {
        //                log.info("Session Validation By pass startsWith {}", byPassUrl);
        //                return true;
        //            }
        //        }

        // tmp1) Debounce 테스트용 세션생성 TODO: 로그인 기능 구현 후 삭제 필요.
        // request.getSession() -> 세션없으면 만들기 때문에 메소드 처리완료 후에는 항상 세션이 활성화된다.
        final var TMP_DEBOUNCE_URI = "/sample/hello/helloPrintDebounce";
        if (requestUri.startsWith(TMP_DEBOUNCE_URI)) {
            log.info(
                    "[byPass For debounce]sessionId: [{}]requestURI: {}",
                    request.getSession().getId(),
                    requestUri);
        }

        // 1) 세션검증
        // tmp) 특정 uri 만 검증 TODO: 로그인 기능 구현 후 특정 URL 조건 삭제 필요.
        final var TMP_VALIDATION_URI = "/sample/hello/validSession";
        if (TMP_VALIDATION_URI.equals(requestUri)) {
            var requestSession = request.getSession(false);
            if (requestSession == null) {
                // 유효하지 않은 세션 거부는 온전한 로직이므로 에러를 쌓을 필요가 없음.
                // 리턴되는 에러코드로 클라이언트 측이 가이드받을 수 있기 때문에 디버그로만 기록한다. TODO: 예외 및 에러코드 확인.
                log.debug("Invalid session. requestDenied.");
                // 예외 발생 -> 컨트롤러어드바이스 처리 -> 상태코드 세팅하여 전송
                // return false 인 경우, 핸들러로 요청이 전달되지만 않을뿐 클라이언트는 200 응답을 받게 된다.
                throw new InvalidSessionException("Invalid Session! valid session plz.");
            }
            // TODO: 로그인 기능 구현 후 세션의 사용자 데이터 검증(스프링 시큐리티 예정)
        }

        return true;
    }
}
