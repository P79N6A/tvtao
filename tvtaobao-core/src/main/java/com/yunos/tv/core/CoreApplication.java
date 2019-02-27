/**
 * $
 * PROJECT NAME: TvTaoBaoBase
 * PACKAGE NAME: com.yunos.tv.tvtaobaobase
 * FILE NAME: AppHolder.java
 * CREATED TIME: 2014-10-23
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
package com.yunos.tv.core;


import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.blitz.global.BzApplication;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.pay.LoginHelperImpl;

/**
 * Application
 */
public class CoreApplication extends BzApplication {

    private static final String TAG = "CoreApplication";

    protected static CoreApplication mApplication = null;

    protected static long onCreateTime = 0l;

    public MyLifecycleHandler getMyLifecycleHandler() {
        return myLifecycleHandler;
    }

    private MyLifecycleHandler myLifecycleHandler = new MyLifecycleHandler();

    @Override
    public void onCreate() {
        Config.getInstance();
        if (Config.isDebug()) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
                    .detectNetwork().detectAll().penaltyLog().build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects().penaltyLog().penaltyDropBox()
                    //                .penaltyDeath()
                    .build());
        }
        registerActivityLifecycleCallbacks(myLifecycleHandler);
        super.onCreate();

    }

    @Override
    public void onTrimMemory(final int level) {
        super.onTrimMemory(level);

        Log.w(TAG, "onTrimMemory: critical level: " + level);
        switch (level) {
            case TRIM_MEMORY_UI_HIDDEN:
                break;
            case TRIM_MEMORY_BACKGROUND:
                break;
            case TRIM_MEMORY_MODERATE:
                break;
            case TRIM_MEMORY_COMPLETE:
                //clearImageLoaderCache();
                // seems clear cacache cause problem.
                break;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, " onLowMemory called");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        clear();
    }

    public void clear() {
        User.clearUser();
        SharePreferences.destroy();
        RtEnv.clear();
    }

    public static void toast(String s) {
        Toast toast = Toast.makeText(getApplication(), "", Toast.LENGTH_SHORT);
        toast.setText(s);
        toast.show();
    }

    public static LoginHelper getLoginHelper(Context context) {
        LoginHelper loginHelper = BzApplication.getLoginHelper(context);
        if (loginHelper == null) {
            loginHelper = LoginHelperImpl.getJuLoginHelper();
            setLoginHelper(loginHelper);
        }
        return loginHelper;
    }

    public static CoreApplication getApplication() {
        return mApplication;
    }

    public static long getOnCreateTime() {
        return onCreateTime;
    }
}
