package com.dykim.base.consts.uris;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 *
 * <h3>Sample api uris</h3>
 *
 * @author dongyoung.kim
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SampleApiUris {

    private static final String V1 = "/api/sample/v1";

    private static final String PREFIX = V1;

    public static final String DEBOUNCE = PREFIX + "/debounce";
    public static final String DTO = PREFIX + "/dto";
    public static final String EXCEPTION = PREFIX + "/exception";
    public static final String VALID_SESSION = PREFIX + "/valid-session";
    public static final String INSERT = PREFIX;
    public static final String SELECT = PREFIX;
    public static final String SELECT_LIST = PREFIX;
    public static final String UPDATE = PREFIX;
    public static final String DELETE = PREFIX;
}
