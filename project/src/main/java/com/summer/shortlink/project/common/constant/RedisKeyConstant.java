package com.summer.shortlink.project.common.constant;

import java.util.concurrent.TimeUnit;

public class RedisKeyConstant {

    public static final String GOTO_SHORT_LINK_KEY_FORMAT = "short-link_goto_%s";

    public static final String GOTO_SHORT_LINK_LOCK_KEY_FORMAT = "short-link_goto_lock_%s";

    public static final String GOTO_SHORT_LINK_NULL_VALUE_KEY_FORMAT = "short-link_goto_null_%s";

    public static final String GOTO_SHORT_LINK_NULL_VALUE = "null";

    public static final Integer NULL_VALUE_EXPIRE = 30;

    public static final TimeUnit NULL_VALUE_EXPIRE_UNIT = TimeUnit.MINUTES;
}
