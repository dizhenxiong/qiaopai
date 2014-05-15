package com.lenovo.vctl.redis.client.route;

import org.apache.log4j.Logger;

public final class ContextHolder {
    private static Logger logger = Logger.getLogger(ContextHolder.class);
    private static final ThreadLocal<Object> contextHolder = new ThreadLocal<Object>();

    public static void setRedisName(Object redisName) {
        contextHolder.set(redisName);
    }

    public static Object getRedisName() {
        return contextHolder.get();
    }

    public static void clearDataSource() {
        contextHolder.remove();
    }
}
