package com.yunos.tv.payment;

import android.content.Context;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/19
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class TVOSPayApplication {
    private static TVOSPayApplication application;
    public static TVOSPayApplication getInstance() {
        if (application == null) {
            synchronized (TVOSPayApplication.class) {
                if (application == null) {
                    application = new TVOSPayApplication();
                }
            }
        }
        return application;
    }

    public void init(Context context) {

    }
}
