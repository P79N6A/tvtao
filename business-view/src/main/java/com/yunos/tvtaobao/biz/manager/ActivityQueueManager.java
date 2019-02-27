package com.yunos.tvtaobao.biz.manager;


import android.app.Activity;

import com.yunos.tv.core.common.AppDebug;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public final class ActivityQueueManager {

    private String TAG = "ActivityQueueManager";

    private Map<String, ArrayList<WeakReference<Activity>>> mMapActivityList;
    private Stack<WeakReference<Activity>> activityStack;

    private static ActivityQueueManager mActivityQueueManager;

    public static ActivityQueueManager getInstance() {
        if (mActivityQueueManager == null) {
            mActivityQueueManager = new ActivityQueueManager();
        }

        return mActivityQueueManager;
    }

    private ActivityQueueManager() {
        onInitActivityManager();
    }

    /**
     * 初始化，由 AppHolder 调用
     */
    private void onInitActivityManager() {
        mMapActivityList = new HashMap<String, ArrayList<WeakReference<Activity>>>();
        mMapActivityList.clear();
    }

    /**
     * 清空， 由 AppHolder 调用
     */
    public void onClearManager() {

        onClearAllMapActivityList();

        if (activityStack != null) {
            for (int i = 0; i < activityStack.size(); i++) {
                popActivity();
            }
            activityStack.clear();
            activityStack = null;
        }
    }

    /**
     * 清楚所有map中的Activity
     */
    public void onClearAllMapActivityList() {
        if (mMapActivityList != null) {
            Iterator<Map.Entry<String, ArrayList<WeakReference<Activity>>>> iter = mMapActivityList.entrySet()
                    .iterator();
            while (iter.hasNext()) {
                Map.Entry<String, ArrayList<WeakReference<Activity>>> entry = (Map.Entry<String, ArrayList<WeakReference<Activity>>>) iter
                        .next();
                onDestroyActivityOfList(entry.getKey());
            }
            mMapActivityList.clear();
        }
        mMapActivityList = null;
    }

    /**
     * 把Activity 添加到释放队列中去，由 Activity 调用
     * @param Tag
     * @param activity
     * @return
     */
    public boolean onAddDestroyActivityToList(String Tag, Activity activity) {

        boolean result = false;
        if (mMapActivityList != null) {
            WeakReference<Activity> refActivity = new WeakReference<Activity>(activity);
            ArrayList<WeakReference<Activity>> arrayList = mMapActivityList.get(Tag);
            if (arrayList == null) {
                arrayList = new ArrayList<WeakReference<Activity>>();
                result = arrayList.add(refActivity);
                mMapActivityList.put(Tag, arrayList);
            } else {
                result = arrayList.add(refActivity);
            }
        }

        return result;
    }

    /**
     * 释放 TAG 中全部的Activity
     * @param Tag
     * @return
     */
    public boolean onDestroyActivityOfList(String Tag) {
        boolean result = false;
        if (mMapActivityList != null) {
            ArrayList<WeakReference<Activity>> arrayList = mMapActivityList.get(Tag);
            if (arrayList != null) {
                int count = arrayList.size();
                for (int index = 0; index < count; index++) {
                    WeakReference<Activity> refactivity = arrayList.get(index);
                    if (refactivity != null) {
                        Activity activity = refactivity.get();
                        if (activity != null) {
                            activity.finish();
                        }
                    }
                }
                arrayList.clear();
                result = true;
            }
        }
        return result;
    }

    /**
     * 从列表中移除销毁的activity
     * @param Tag
     * @param activity
     * @return
     */
    public boolean onRemoveDestroyActivityFromList(String Tag, Activity activity) {
        boolean result = false;
        if (mMapActivityList != null) {
            ArrayList<WeakReference<Activity>> arrayList = mMapActivityList.get(Tag);
            if (arrayList != null) {
                int count = arrayList.size();
                for (int index = 0; index < count; index++) {
                    WeakReference<Activity> refactivity = arrayList.get(index);
                    if (refactivity != null) {
                        if (activity == refactivity.get()) {
                            result = arrayList.remove(refactivity);
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * 弹出堆栈顶部的activity
     */
    public void popActivity() {
        if (activityStack != null) {
            WeakReference<Activity> refactivity = activityStack.firstElement();
            AppDebug.d(TAG, TAG + ".popActivity: refactivity = " + refactivity);
            activityStack.remove(refactivity);
            if (refactivity != null) {
                Activity activity = refactivity.get();
                AppDebug.d(TAG, TAG + ".popActivity: activity = " + activity);
                if (activity != null) {
                    AppDebug.d(TAG, TAG + ".popActivity:" + activity.getClass().getName());
                    activity.finish();
                }
            }
        }
    }

    public void popCurrentActivity(Activity activity) {
        boolean result = false;
        if ((activity != null) && (activityStack != null)) {
            for (WeakReference<Activity> refActivity : activityStack) {
                Activity saveActivity = refActivity.get();
                if (saveActivity != null && saveActivity == activity) {
                    result = activityStack.remove(refActivity);
                    break;
                }
            }
        }

        AppDebug.d(TAG, TAG + ".popCurrentActivity  activity:" + activity + ";  result = " + result);
    }

    /**
     * 将当前activity压入堆栈底
     * @param activity
     * @param maxCount
     */
    public void pushActivity(Activity activity, int maxCount) {

        boolean result = false;

        if (activityStack == null) {
            activityStack = new Stack<WeakReference<Activity>>();
            activityStack.clear();
        }
        WeakReference<Activity> refActivity = new WeakReference<Activity>(activity);

        result = activityStack.add(refActivity);
        AppDebug.d(TAG, TAG + ".activityStack.size:" + activityStack.size());

        if (maxCount > 0 && activityStack.size() > maxCount) {
            popActivity();
        }

        AppDebug.d(TAG, TAG + ".pushActivity  activity:" + activity + ";  result = " + result);
    }

}
