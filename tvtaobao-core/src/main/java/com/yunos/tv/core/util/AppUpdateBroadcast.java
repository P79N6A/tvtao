package com.yunos.tv.core.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.UpdateStatus;

import java.util.Calendar;


public class AppUpdateBroadcast {
    private final String TAG = "AppUpdateBroadcast";
    private static final String APP_UPDATE_ACTION = "com.yunos.taobaotv.update.action.BROADCAST";
    private AppUpdateBroadcastReceiver mAppUpdateBroadcastReceiver;
    private OnShowAppUpdateListener mOnShowAppUpdateListener;

    /**
     * 注册
     * @param listener
     */
    public void registerUpdateBroadcast(OnShowAppUpdateListener listener){
        AppDebug.i(TAG, "registerUpdateBroadcast");
        if (mAppUpdateBroadcastReceiver == null) {
            mAppUpdateBroadcastReceiver = new AppUpdateBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(APP_UPDATE_ACTION);
            filter.setPriority(1000);
            CoreApplication.getApplication().registerReceiver(mAppUpdateBroadcastReceiver, filter);
        }
        mOnShowAppUpdateListener = listener;
        
        AppDebug.i(TAG, "update status: " + UpdateStatus.getUpdateStatus());
        if (UpdateStatus.getUpdateStatus() == UpdateStatus.START_ACTIVITY) {
        	Bundle bundle = UpdateStatus.getBundle();
        	if (mOnShowAppUpdateListener != null && bundle != null) {
        		mOnShowAppUpdateListener.onShowAppUpdate(bundle);
        	}
        }

//        UpdateStatus.setUpdateStatus(UpdateStatus.UNKNOWN, null);
    }
    
    /**
     * 反注册
     */
    public void unregisterUpdateBroadcast(){
        AppDebug.i(TAG, "unregisterUpdateBroadcast");
        if (mAppUpdateBroadcastReceiver != null) {
            CoreApplication.getApplication().unregisterReceiver(mAppUpdateBroadcastReceiver);
            mAppUpdateBroadcastReceiver = null;
        }
        mOnShowAppUpdateListener = null;
    }
    
    
    /**
     * 显示强制更新页面
     * @param context
     * @return true show success or false
     */
    public boolean startUpdateActivity(Context context, Bundle bundle){
        AppDebug.i(TAG, "startUpdateActivity bundle="+bundle);
        if (bundle != null) {
            try {

                Boolean mIsForced=bundle.getBoolean("isForced");
                if(!mIsForced){
                    SharedPreferences sp = context.getSharedPreferences("updateInfo", Context.MODE_PRIVATE);
                    String upgradeMode= sp.getString( "upgradeMode", "");
                    if("everyDay".equals(upgradeMode)){  //everyDay  一天弹一次；everyTime每次启动应用弹一次
                        long time = sp.getLong("update_dialog_show_time", 0);
                        if (DateUtils.isToday(time)){
                            return false;
                        }
                    }
                    startNotForceUpdateActivity(context,bundle);


                    SharedPreferences.Editor editor = sp.edit();
                    editor.putLong("update_dialog_show_time", Calendar.getInstance().getTime().getTime());
                    editor.apply();

                }else {
                Intent startIntent = new Intent();
                startIntent.setData(Uri.parse("update://yunos_tvtaobao_update"));
                startIntent.putExtras(bundle);
                context.startActivity(startIntent);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }            
        return false;
    }


    /**
     * 显示提示更新页面
     * @param context
     * @return true show success or false
     */
    public boolean startNotForceUpdateActivity(Context context, Bundle bundle){
        AppDebug.i(TAG, "startUpdateActivity bundle="+bundle);
        if (bundle != null) {
            try {
                Intent startIntent = new Intent();
                startIntent.setData(Uri.parse("not_force_update://yunos_tvtaobao_not_force_update"));
                startIntent.putExtras(bundle);
                context.startActivity(startIntent);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // 网络接收器
    public class AppUpdateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, APP_UPDATE_ACTION)) {
                Bundle bundle = intent.getExtras();
                AppDebug.i(TAG, "onReceive bundle="+bundle);
                if (bundle != null && mOnShowAppUpdateListener != null) {
                    mOnShowAppUpdateListener.onShowAppUpdate(bundle);
                }
                UpdateStatus.setUpdateStatus(UpdateStatus.UNKNOWN, null);
            }
        }
    };
    
    /**
     * 显示更新页面的监听器
     * @author tingmeng.ytm
     *
     */
    public interface OnShowAppUpdateListener{
        public void onShowAppUpdate(Bundle bundle);

        public void onShowAppUpdateDialog(Bundle bundle);
    }
}
