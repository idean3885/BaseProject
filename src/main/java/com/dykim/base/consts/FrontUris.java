/*
 * Copyright 2024 NHN (https://nhn.com) and others.
 * © NHN Corp. All rights reserved.
 */

package com.dykim.base.consts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 *
 * <h3>Front uris</h3>
 *
 * @author dongyoung.kim
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FrontUris {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Security {

        public static final String LOGIN = "/login";
        public static final String LOGIN_SUCCESS = Front.OVERVIEW;
        public static final String LOGOUT = "/logout";
        public static final String LOGOUT_SUCCESS = LOGIN;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Front {

        public static final String OVERVIEW = "/main";
    }
}
