package com.yunos.tvtaobao.biz.common;


import android.app.Activity;

import com.yunos.tv.core.common.AppDebug;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ActivityQueueManager {

    private String TAG = "ActivityQueueManager";

    private Map<String, ArrayList<WeakReference<Activity>>> mMapActivityList;

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

    public ArrayList<WeakReference<Activity>> getCacheActivityByTag(String tag){
        if(tag==null || mMapActivityList==null) {
            return null;
        }
        return mMapActivityList.get(tag);
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

    public void popCurrentActivity(String tag, Activity activity) {
        boolean result = false;
        if ((activity != null) && (mMapActivityList != null) && (mMapActivityList.containsKey(tag))) {
            ArrayList<WeakReference<Activity>> activityStack = mMapActivityList.get(tag);
            if (activityStack == null || (activityStack != null && activityStack.isEmpty())) {
                return;
            }
            for (WeakReference<Activity> refActivity : activityStack) {
                Activity saveActivity = refActivity.get();
                if (saveActivity != null && saveActivity == activity) {
                    result = activityStack.remove(refActivity);
                    break;
                }
            }
            mMapActivityList.put(tag, activityStack);
        }

        AppDebug.d(TAG, TAG + ".popCurrentActivity  activity:" + activity + ";  result = " + result);
    }

    /**
     * 将当前activity压入堆栈底
     * @param activity
     * @param maxCount
     */
    public void pushActivity(String tag, Activity activity, int maxCount) {
        boolean result = false;

        ArrayList<WeakReference<Activity>> activityStack = new ArrayList<WeakReference<Activity>>();
        if (mMapActivityList == null) {
            mMapActivityList = new HashMap<String, ArrayList<WeakReference<Activity>>>();
            onInitActivityManager();
        } else {
            activityStack = mMapActivityList.get(tag);
        }

        if (activityStack == null) {
            activityStack = new ArrayList<WeakReference<Activity>>();
        }

        WeakReference<Activity> refActivity = new WeakReference<Activity>(activity);

        result = activityStack.add(refActivity);
        AppDebug.d(TAG, TAG + ".pushActivity.activityStack.size:" + activityStack.size() + ", tag = " + tag
                + ", activity = " + activity + ", maxCount = " + maxCount);

        if (maxCount > 0 && activityStack.size() > maxCount) {
            WeakReference<Activity> refFirstActivity = activityStack.get(0);
            AppDebug.d(TAG, TAG + ".pushActivity: refFirstActivity = " + refFirstActivity);
            activityStack.remove(0);
            if (refFirstActivity != null) {
                Activity firstActivity = refFirstActivity.get();
                AppDebug.d(TAG, TAG + ".pushActivity: firstActivity = " + firstActivity);
                if (firstActivity != null) {
                    AppDebug.d(TAG, TAG + ".pushActivity: finish firstActivity.className" + firstActivity.getClass().getName());
                    firstActivity.finish();
                }
            }
        }

        mMapActivityList.put(tag, activityStack);

        AppDebug.d(TAG, TAG + ".pushActivity tag:" + tag + ";  activity:" + activity + ";  result = " + result);
    }

    /**
     * 清除堆栈,只保留keepCount个
     * @param tag
     * @param keepCount
     */
    public void clearhActivity(String tag, int keepCount) {
        ArrayList<WeakReference<Activity>> activityStack = new ArrayList<WeakReference<Activity>>();
        if (mMapActivityList == null) {
            mMapActivityList = new HashMap<String, ArrayList<WeakReference<Activity>>>();
            onInitActivityManager();
        } else {
            activityStack = mMapActivityList.get(tag);
        }

        if (activityStack == null) {
            activityStack = new ArrayList<WeakReference<Activity>>();
        }

        AppDebug.d(TAG, TAG + ".clearhActivity.activityStack.size:" + activityStack.size());

        if (activityStack.size() > keepCount) {
            int count = activityStack.size() - keepCount;
            for (int index = 0; index < count; index++) {
                WeakReference<Activity> refFirstActivity = activityStack.get(0);
                AppDebug.d(TAG, TAG + ".clearhActivity: refFirstActivity = " + refFirstActivity);
                activityStack.remove(0);
                if (refFirstActivity != null) {
                    Activity firstActivity = refFirstActivity.get();
                    AppDebug.d(TAG, TAG + ".clearhActivity: firstActivity = " + firstActivity);
                    if (firstActivity != null) {
                        AppDebug.d(TAG, TAG + ".clearhActivity:firstActivity.className"
                                + firstActivity.getClass().getName());
                        firstActivity.finish();
                    }
                }
            }
        }
    }
}
