package com.yunos.tvtaobao.biz.updatesdk;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tvtaobao.biz.IAppUpdate;
import com.yunos.tvtaobao.biz.IAppUpdateCallback;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateClient implements IAppUpdateCallback {

    private String TAG = "UpdateClient";

    static private final String STATUS_KEY = "status";
    static private final String CODE_KEY = "code";
    static private final String INFO_KEY = "info";
    static private final String BUNDLE_KEY = "bundle";

    private IAppUpdate mAppUpdateService = null;
    private Context mContext;
    private String mUpdateParam;
    private IUpdateCallback mUpdateCallback;
    private static UpdateClient s_UpdateClient = null;

    public static UpdateClient getInstance(Context context) {
        if (s_UpdateClient == null) {
            s_UpdateClient = new UpdateClient(context);
        }
        return s_UpdateClient;
    }

    public UpdateClient(Context context) {
        mContext = context;
        bindService();
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAppUpdateService = IAppUpdate.Stub.asInterface(service);
            try {
                mAppUpdateService.startUpdate(mUpdateParam, UpdateClient.this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAppUpdateService = null;
        }
    };

    private void bindService() {
        if (mAppUpdateService != null)
            return;
        try {
            Intent intent = new Intent();
            intent.setPackage(mContext.getPackageName());
            intent.setAction("com.yunos.taobaotv.update.updateservice.IAppUpdate");
            mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unbindService() {
        if (mAppUpdateService != null) {
            mContext.unbindService(mConnection);
            mAppUpdateService = null;
        }
    }

    public void startDownload(String appCode, String versionName, int versionCode, String deviceId, String channelId,
                              IUpdateCallback callback) {
        AppDebug.i(TAG, TAG + ".startDownload.appCode = " + appCode + ".versionName = " + versionName
                + ", versionCode = " + versionCode + ".deviceId = " + deviceId + ".channelId = " + channelId
                + ", callback = " + callback);
        mUpdateCallback = callback;
        JSONObject paramJson = new JSONObject();
        try {
            paramJson.put("code", appCode != null ? appCode : "");
            paramJson.put("versionCode", "" + versionCode);
            paramJson.put("versionName", versionName != null ? versionName : "");
            paramJson.put("uuid", deviceId != null ? deviceId : "");
            paramJson.put("channelId", channelId != null ? channelId : "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUpdateParam = paramJson.toString();

        if (mAppUpdateService == null) {
            bindService();
        } else {
            try {
                mAppUpdateService.startUpdate(mUpdateParam, this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopDownload() {
        if (mAppUpdateService != null) {
            try {
                mAppUpdateService.stopUpdate(mUpdateParam);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService();
        }
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    @Override
    public void onUpdateStatusChanged(Bundle result) throws RemoteException {
        AppDebug.v(TAG, TAG + ".onUpdateStatusChanged.result = " + result + ", mUpdateCallback = " + mUpdateCallback);
        if (mUpdateCallback != null && result != null) {
            int status = result.getInt(STATUS_KEY);
            String code = result.getString(CODE_KEY);
            String info = result.getString(INFO_KEY);
            Bundle bundle = result.getBundle(BUNDLE_KEY);
            mUpdateCallback.onUpdateStatusChanged(status, info, bundle);
        }
    }

    public interface IUpdateCallback {

        public final int UPDATE_START = 100;
        public final int UPDATE_PROCESSING = 101;
        public final int UPDATE_END = 200;
        public final int UPDATE_ERROR = -1;

        void onUpdateStatusChanged(int status, String message, Bundle bundle);
    }


    public void addCurrentVersionToSP() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                int versionCode = AppInfo.getAppVersionNum();
                editor.putInt(UpdatePreference.UPDATE_CURRENT_VERSION_CODE, versionCode);
                editor.apply();
            }
        }).start();
    }
}
