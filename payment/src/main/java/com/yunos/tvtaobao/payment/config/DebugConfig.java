package com.yunos.tvtaobao.payment.config;

import android.app.ActivityManager;

import com.yunos.tvtaobao.payment.BuildConfig;

/**
 * Created by LJY on 18/5/10.
 */

public class DebugConfig {

    /**
     *检测是否在跑monkey
     */
    public static boolean whetherIsMonkey() {
        if(BuildConfig.IsMonkey){
            return ActivityManager.isUserAMonkey();
        }else {
            return BuildConfig.IsMonkey;
        }
    }
}
