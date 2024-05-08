package com.dykim.base.consts.uris;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 *
 * <h3>Security front uris</h3>
 *
 * @author dongyoung.kim
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityFrontUris {

    public static final String LOGIN = "/login";
    public static final String LOGIN_SUCCESS = SampleFrontUris.OVERVIEW;
    public static final String LOGOUT = "/logout";
    public static final String LOGOUT_SUCCESS = LOGIN;
}
