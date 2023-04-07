package com.dykim.base.util;

import com.dykim.base.member.dto.MemberSessionVo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SessionUtil {

    public static final String MEMBER_SESSION_DATA = "member-session-data";

    public static final String SIGN_IN_REDIRECT_URI = "/user/signIn";

    public static MemberSessionVo getMemberSessionVo() {
        return Optional.ofNullable((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes::getRequest)
                .map(request -> request.getSession(false))
                .map(session -> (MemberSessionVo) session.getAttribute(MEMBER_SESSION_DATA))
                .orElseGet(MemberSessionVo::new);
    }

}
