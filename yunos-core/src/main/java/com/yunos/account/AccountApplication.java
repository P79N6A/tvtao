package com.yunos.account;

import android.content.Context;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/19
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class AccountApplication {

    private static AccountApplication application;
    public static AccountApplication getInstance() {
        if (application == null) {
            synchronized (AccountApplication.class) {
                if (application == null) {
                    application = new AccountApplication();
                }
            }
        }
        return application;
    }

    public void init(Context context) {

    }
}
