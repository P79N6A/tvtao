package com.tvtaobao.voicesdk.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.config.AppInfo;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by pan on 2017/10/8.
 */

public class ActivityUtil {

    /**
     * 判断Activity是否在最前台
     * @param context
     * @param activity
     * @return
     */
    public static boolean isTopActivity(Context context, Class<?> activity) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String className = "";
        if (Build.VERSION.SDK_INT >= 21) {
            List<ActivityManager.RunningAppProcessInfo> pis = am.getRunningAppProcesses();
            ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);

            if (topAppProcess != null && topAppProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                String processName = topAppProcess.processName;
                if (AppInfo.getPackageName().equals(processName)) {
                    if (isRunningForeground(context)) {
                        if (mActivity != null && mActivity.get() != null) {
                            className = mActivity.get().getClass().getName();
                        }
                    }
                }
            }
        } else {
            //getRunningTasks() is deprecated since API Level 21 (Android 5.0)
            List localList = am.getRunningTasks(1);
            ActivityManager.RunningTaskInfo localRunningTaskInfo = (ActivityManager.RunningTaskInfo) localList.get(0);
            className = localRunningTaskInfo.topActivity.getClassName();
        }

        LogPrint.d("ActivityUtil", "TopActivity.ClassName : " + className);
        LogPrint.d("ActivityUtil", "Activity.ClassName : " + activity.getName());
        return className.contains(activity.getName());
    }

    public static boolean isRunningForeground(Context context){
        return CoreApplication.getApplication().getMyLifecycleHandler().isApplicationInForeground();
    }

    private static WeakReference<Activity> mActivity;
    public static void addTopActivity(Activity activity) {
        mActivity = new WeakReference<Activity>(activity);
    }

    public static Context getTopActivity() {
        if (mActivity != null && mActivity.get() != null) {
            return mActivity.get();
        }
        return null;
    }

    private static WeakReference<Dialog> mDialog;
    public static void addVoiceDialog(Dialog dialog) {
        mDialog = new WeakReference<Dialog>(dialog);
    }

    public static Dialog getVoiceDialog() {
        if (mDialog != null && mDialog.get() != null) {
            return mDialog.get();
        }
        return null;
    }
}
