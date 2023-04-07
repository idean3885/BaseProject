package com.dykim.base.enums;

import lombok.RequiredArgsConstructor;

/**
 * <h3>권한 직책</h3>
 * Spring Security 에 사용되는 권한
 */
@RequiredArgsConstructor
public enum AuthorityRole {

    ADMIN("관리자, 권한설정 TBD")
    , DEVELOPER("개발자, 권한설정 TBD")
    , GENERAL("일반 사용자, 권한설정 TBD")
    ;

    private final String description;

}
