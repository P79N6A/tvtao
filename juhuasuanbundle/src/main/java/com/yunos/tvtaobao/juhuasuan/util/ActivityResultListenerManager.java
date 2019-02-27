package com.yunos.tvtaobao.juhuasuan.util;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;


public class ActivityResultListenerManager {
    public final static int ACTIVITY_CITYSET_REFRESH_CODE = 1001;//返回码
    public static ActivityResultListenerManager activityResultListenerManager = null;//主页回调
    public List<ActivityResultListener> listeners = new ArrayList<ActivityResultListener>();
    public static ActivityResultListenerManager getInstance(){
        if(activityResultListenerManager == null){
            activityResultListenerManager = new ActivityResultListenerManager();
        }
        return activityResultListenerManager;
    }
    /**
     * @param listener
     */
    public void addListener(ActivityResultListener listener){
        synchronized (this) {
            listeners.add(listener);
        }
    }
    /**
     * @param listener
     */
    public void removeListener(ActivityResultListener listener){
        synchronized (this) {
            listeners.remove(listener);
        }
    }
    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void notifyListeners(int requestCode, int resultCode, Intent data){
        if(listeners != null){
            for(ActivityResultListener listener : listeners){
                listener.onResult(requestCode, resultCode, data);
            }
        }
    }
    public interface ActivityResultListener{
        public void onResult(int requestCode, int resultCode, Intent data);
    }
    
}
