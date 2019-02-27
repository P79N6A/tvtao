package com.yunos.tvtaobao.biz.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.base.utils.StringUtils;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.UpdateStatus;
import com.yunos.tvtaobao.biz.IAppUpdate;
import com.yunos.tvtaobao.biz.IAppUpdateCallback;
import com.yunos.tvtaobao.biz.controller.LogUtils;
import com.yunos.tvtaobao.biz.controller.Update;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.util.UserTrackUtil;

import java.lang.ref.WeakReference;

/**
 * @author quanquan.rqq
 *         上层调用入口，绑定service，启动更新
 */
public class UpdateService extends Service {

    private final static String TAG = "UpdateService";

    private Context mContext = null;

    private IAppUpdateCallback mAppUpdateCallback;

    public MyHandler getmServiceHandler() {
        return mServiceHandler;
    }

    private MyHandler mServiceHandler;

    private static class MyHandler extends Handler {

        private WeakReference<UpdateService> mOuter;

        public MyHandler(UpdateService updateService) {
            mOuter = new WeakReference<UpdateService>(updateService);
        }

        @Override
        public void handleMessage(Message msg) {
            UpdateService updateService = mOuter.get();
            if (updateService != null) {
                switch (msg.what) {
                    case UpdatePreference.TERMINATED:
                        break;
                    case UpdatePreference.LOG_RECEIVE:
                        LogUtils.getInstance(updateService).logReceive(updateService);
                        break;
                    case UpdatePreference.LOG_STOP:
                        LogUtils.getInstance(updateService).stop();
                        break;

                    case UpdatePreference.LOG_READ:
                        String log=msg.obj.toString();
                        SharedPreferences sp = updateService.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
                        String mUpdateParams=sp.getString(UpdatePreference.UPDATE_OBJECT,"");
                        boolean isMoHeOn= SharePreferences.getBoolean(UpdatePreference.IS_MOHE_LOG_ON,false);
                        boolean isLianMengLogOn=SharePreferences.getBoolean(UpdatePreference.IS_LIANMNEG_LOG_ON,false);
                        boolean isYiTiJiLogOn=SharePreferences.getBoolean(UpdatePreference.IS_YITIJI_LOG_ON,false);

                        if(StringUtils.isEmpty(mUpdateParams)){
                            return;
                        }

                        if(Config.MOHE.equals(Config.getChannel())&&!isMoHeOn){
                            return;
                        }else if(Config.LIANMENG.equals(Config.getChannel())&&!isLianMengLogOn){
                            return;
                        }else if(Config.YITIJI.equals(Config.getChannel())&&!isYiTiJiLogOn){
                            return;
                        }

                        JSONObject json = JSON.parseObject(mUpdateParams);
                        String code = json.getString("code");
                        String versionCode = json.getString("versionCode");
                        String versionName = json.getString("versionName");
                        String uuid = json.getString("uuid");
                        String channelId = json.getString("channelId");
                        String systemInfo = json.getString("systemInfo");
                        String version = json.getString("version");

                        BusinessRequest.getBusinessRequest().logReceive(version, uuid, channelId, code, versionCode, versionName, systemInfo,log, new UpdateService.LogReceiveListener());
                        break;
                }
            }
        }
    }

    private  static class LogReceiveListener implements RequestListener<String> {
        public LogReceiveListener() {
        }

        @Override
        public void onRequestDone(String data, int resultCode, String handleMessagemsg) {
            Log.d(TAG, "onRequestDone " + data);
//            LogUtils.getInstance(updateService).stop();
        }
    }
    /**
     * The IRemoteInterface is defined through IDL
     */
    public final IAppUpdate.Stub mBinder = new IAppUpdate.Stub() {

        @Override
        public void startUpdate(String jsonParam, IAppUpdateCallback callback) throws RemoteException {
            AppDebug.d(TAG, TAG + ".startUpdate, jsonParam: " + jsonParam + ", callback = " + callback);

            UpdateStatus.setUpdateStatus(UpdateStatus.UNKNOWN, null);

            if (callback != null) {
                mAppUpdateCallback = callback;
            }

            if (jsonParam == null) {
                AppDebug.d(TAG, "jsonParam is null");
                doCallback(UpdatePreference.STATUS_ERROR, "", "jsonParam is null");
                return;
            }

            JSONObject json = null;
            try {
                json = JSON.parseObject(jsonParam);
            } catch (JSONException e) {
                AppDebug.d(TAG, "incorrect jsonParam format: " + e.getLocalizedMessage());
                doCallback(UpdatePreference.STATUS_ERROR, "", "json exception");
                return;
            }

            String code = json.getString("code");
            // 参数是否为空
            if (code == null) {
                AppDebug.d(TAG, "code is null");
                doCallback(UpdatePreference.STATUS_ERROR, "", "params error");
                return;
            }

            String curVersionCode = json.getString("versionCode");
            UserTrackUtil.setCurVersionCode(curVersionCode);

            if (code.equalsIgnoreCase(UpdatePreference.TVTAOBAO_EXTERNAL)) {
                Log.d(TAG, TAG + ".startUpdate.external call");

                // 停止并移除外部调用的自升级
                Update upExternal = Update.get(UpdatePreference.TVTAOBAO_EXTERNAL);
                if (upExternal != null) {
                    upExternal.stop();
                    Update.remove(UpdatePreference.TVTAOBAO_EXTERNAL);
                }

                upExternal = new Update(UpdateService.this, mServiceHandler, jsonParam);
                Update.add(UpdatePreference.TVTAOBAO_EXTERNAL, upExternal);
                upExternal.start();
            } else {
                Log.d(TAG, TAG + ".startUpdate.internal call");

                //停止并移除内部调用的自升级
                Update upInternal = Update.get(UpdatePreference.TVTAOBAO);
                if (upInternal != null) {
                    upInternal.stop();
                    Update.remove(UpdatePreference.TVTAOBAO);
                }
                // 获取内部调用自升级对象
                upInternal = new Update(UpdateService.this, mServiceHandler, jsonParam);
                Update.add(UpdatePreference.TVTAOBAO, upInternal);
                upInternal.start();
            }
            doCallback(UpdatePreference.STATUS_START_UPDATE, code, "start update");
        }

        @Override
        public void stopUpdate(String jsonParam) throws RemoteException {
            AppDebug.i(TAG, TAG + ".stopUpdate.jsonParam = " + jsonParam);
            JSONObject json = null;
            try {
                json = JSON.parseObject(jsonParam);
            } catch (JSONException e) {
                AppDebug.d(TAG, TAG + ".stopUpdate.json exception: " + e.getLocalizedMessage());
            }
            if (json == null) {// 参数格式错误
                doCallback(UpdatePreference.STATUS_ERROR, "", "json exception");
                return;
            }

            String code = json.getString("code");
            // 参数是否为空
            if (code == null) {
                doCallback(UpdatePreference.STATUS_ERROR, "", "params error");
                return;
            }
            AppDebug.d(TAG, TAG + ".stopUpdate.call stopUpdate, code: " + code);
            Update up = Update.get(code);
            if (up != null) {
                up.stop();
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        AppDebug.d(TAG, TAG + ".onBind.UpdateService.onBinde, return mBinder: " + mBinder);
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppDebug.d(TAG, TAG + ".service onCreate");
        mContext = getApplicationContext();
        mServiceHandler = new MyHandler(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppDebug.d(TAG, TAG + ".service onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        AppDebug.d(TAG, TAG + ".service onDestroy");
        super.onDestroy();
    }

    private void doCallback(int status, String code, String info) {
        if (mAppUpdateCallback != null) {
            Bundle result = new Bundle();
            result.putString(UpdatePreference.KEY_CALLBACK_CODE, code);
            result.putString(UpdatePreference.KEY_CALLBACK_INFO, info);
            result.putInt(UpdatePreference.KEY_CALLBACK_STATUS, status);
            try {
                mAppUpdateCallback.onUpdateStatusChanged(result);
            } catch (RemoteException e) {
                AppDebug.d(TAG, "remote exception: " + e.getLocalizedMessage());
            }
        }
    }
}
