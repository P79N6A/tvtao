package com.yunos.tv.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by LJY on 18/8/28.
 */

public class MyLifecycleHandler  implements Application.ActivityLifecycleCallbacks {
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;
    private static int created;
    private static int destoryed;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ++created;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        android.util.Log.w("test", "application is in foreground: " + (resumed > paused));
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        android.util.Log.w("test", "application is visible: " + (started > stopped));
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ++destoryed;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public boolean isApplicationVisible() {
        return started > stopped;
    }

    public boolean isApplicationInForeground() {
        // 当所有 Activity 的状态中处于 resumed 的大于 paused 状态的，即可认为有Activity处于前台状态中
        return resumed > paused;
    }

    public boolean isLastActivityInForeground(){
        Log.e("ljy","created=="+created+"---------destoryed"+destoryed);
        return created-destoryed==1;
    }

}

