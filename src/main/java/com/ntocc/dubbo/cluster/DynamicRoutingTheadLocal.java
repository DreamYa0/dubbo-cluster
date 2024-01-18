package com.ntocc.dubbo.cluster;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author dreamyao
 * @title
 * @date 2019-08-24 14:16
 * @since 1.0.0
 */
public final class DynamicRoutingTheadLocal {

    private static final TransmittableThreadLocal<String> OWNER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void set(String ground) {
        OWNER_THREAD_LOCAL.set(ground);
    }

    public static String get() {
        return OWNER_THREAD_LOCAL.get();
    }

    public static void remove() {
        OWNER_THREAD_LOCAL.remove();
    }
}
