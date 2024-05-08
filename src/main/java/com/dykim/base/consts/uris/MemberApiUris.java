package com.dykim.base.consts.uris;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 *
 * <h3>Member api uris</h3>
 *
 * @author dongyoung.kim
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberApiUris {

    private static final String V1 = "/api/member/v1";
    public static final String SELECT = V1;
    public static final String SELECT_LIST = V1;
    public static final String INSERT = V1;
    public static final String UPDATE = V1;
    public static final String DELETE = V1;
}
