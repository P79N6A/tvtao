package com.yunos.tvtaobao.biz.updatesdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.ihome.android.market2.aidl.AppOperateAidl;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/7/6
 *     desc : 电信天翼高清和电信聚精彩自升级
 *     version : 1.0
 * </pre>
 */

public class UpdateFromDX {
    private static final String TAG = "UpdateFromDX";
    private static UpdateFromDX updateFromDX;
    private Context mContext;
    // 电信悦me市场，电视淘宝appId
    private final String yuemeAppId = "customAPPIDspS225440000000000000001327";
    // 电信聚精彩，电视淘宝appId
    private final String jujingcaiAppId = "custom00012018070411223000000000101685";
    private AppOperateAidl appOperateService	= null;
    public static UpdateFromDX getInstance(Context context) {
        if (updateFromDX == null) {
            synchronized (UpdateFromDX.class) {
                if (updateFromDX == null) {
                    updateFromDX = new UpdateFromDX(context);
                }
            }
        }
        return updateFromDX;
    }

    private UpdateFromDX(Context context) {
        if (context == null) {
            throw new NullPointerException();
        } else {
            this.mContext = context;
        }
    }

    private void bindService() {
        Intent intent = new Intent("com.ihome.android.market2.aidl.AppOperateAidl");
        mContext.bindService(intent, appOperateConn, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        Intent intent = new Intent("com.ihome.android.market2.aidl.AppOperateAidl");
        mContext.unbindService(appOperateConn);
    }

    private ServiceConnection appOperateConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            appOperateService = AppOperateAidl.Stub.asInterface(service);
            appCheckUpdate();
        }

        public void onServiceDisconnected(ComponentName name) {
            appOperateService = null;
        }
    };

    public void appCheckUpdate() {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (appOperateService == null) {
                        AppDebug.e(TAG, "Service was not  bind");
                        bindService();
                        sleep(2000);
                    }

                    if (appOperateService != null) {
                        boolean isNeedUpdate = appOperateService.appUpdateCheck();
                        if (isNeedUpdate) {
                            AppDebug.e(TAG, "appCheckUpdate > 发现新版本，可以升级！");
                            appUpdate();
                        }else{
                            AppDebug.e(TAG, "appCheckUpdate > 无需升级！");
                            unbindService();
                        }
                    }
                    else {
                        AppDebug.e(TAG, "appCheckUpdate > 检测异常");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Thread.currentThread().interrupt();
                }
            }
        }.start();
    }

    private void appUpdate() {
        String appId = null;
        if ("2017050920".equals(Config.getChannel())) {
             appId = jujingcaiAppId;
        } else {
            appId = yuemeAppId;
        }
        final AppOperateReqInfo info = new AppOperateReqInfo(appId, "电视淘宝", "rca.rc.tvtaobao", "UPDATE");
        new Thread() {
            @Override
            public void run() {
                try {

                    if (appOperateService == null) {
                        bindService();
                        AppDebug.e(TAG, "Service : not  bind");
                        sleep(2000);
                    }

                    if (appOperateService != null) {
                        String result = appOperateService.appOperate(new Gson().toJson(info));
                        AppDebug.e(TAG, "appUpdate > result = " + result);
                        if (!TextUtils.isEmpty(result)) {

                        }else{

                        }
                    }
                    else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Thread.currentThread().interrupt();
                }
            }
        }.start();
    }
}
