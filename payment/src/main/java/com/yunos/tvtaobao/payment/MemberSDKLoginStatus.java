package com.yunos.tvtaobao.payment;

import java.util.concurrent.atomic.AtomicBoolean;

public class MemberSDKLoginStatus {
    private static volatile boolean isLoggingOut = false;

    public static boolean isLoggingOut() {
        return isLoggingOut;
    }

    private static AtomicBoolean isLogin = new AtomicBoolean(false);

    public static boolean compareAndSetLogin(boolean expect, boolean update) {
        return isLogin.compareAndSet(expect, update);
    }

    public static void setLoggingOut(boolean loggingOut) {
        isLoggingOut = loggingOut;
    }
}
