package com.yunos.tv;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.aliyun.ams.tyid.TYIDException;
import com.aliyun.ams.tyid.TYIDManager;
import com.aliyun.ams.tyid.TYIDManagerCallback;
import com.aliyun.ams.tyid.TYIDManagerFuture;

/**
 * Created by rca on 19/09/2017.
 * no use yet
 */

public class TYIDManagerWrapper {

    TYIDManager tyidManager;

    public interface IServiceConnectStatus {
        int STATUS_CONNECT = 1;
        int STATUS_FAIL = -1;

        void onServiceConnectStatus(int status);
    }

    private static int status = 0;

    private static TYIDManagerWrapper instance;

    public static TYIDManagerWrapper get(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null!");
        } else {
            synchronized (TYIDManagerWrapper.class) {
                if (instance == null) {
                    instance = new TYIDManagerWrapper(context);
                }
            }
            return instance;
        }
    }

    public static TYIDManagerWrapper get(Context context, IServiceConnectStatus callback) {
        if (context == null) {
            throw new IllegalArgumentException("context is null!");
        } else {
            synchronized (TYIDManagerWrapper.class) {
                if (instance == null) {
                    instance = new TYIDManagerWrapper(context, callback);
                } else {
                    callback.onServiceConnectStatus(status);
                }
            }
            return instance;
        }
    }

    public String peekToken(String param) {
        if (tyidManager != null) {
            return tyidManager.peekToken(param);
        }
        return null;
    }

    private TYIDManagerWrapper(Context context, IServiceConnectStatus callback) {
        try {
            tyidManager = TYIDManager.get(context);
            status = 1;
        } catch (TYIDException e) {
            e.printStackTrace();
            status = -1;
        }
        if (callback != null)
            callback.onServiceConnectStatus(status);
    }

    private TYIDManagerWrapper(Context context) {
        try {
            tyidManager = TYIDManager.get(context);
            status = 1;
        } catch (TYIDException e) {
            status = -1;
            e.printStackTrace();
        }
    }

    public int yunosGetLoginState() {
        return tyidManager.yunosGetLoginState();
    }

    public TYIDManagerFuture<Bundle> yunosApplyNewMtopToken(final String ttid, final String appkey, final String from, final String deviceId, final TYIDManagerCallback<Bundle> cb, final Handler handler) {
        try {
            if (tyidManager != null)
                return tyidManager.yunosApplyNewMtopToken(appkey, false, 500, true, cb, handler);
        } catch (NoSuchMethodError e) {
            // 兼容老版本，使用老的接口
            if (tyidManager != null) {
                String oldAppkey = "21590507";
                return tyidManager.yunosApplyMtopToken(ttid, oldAppkey, "yunostvtaobao",
                        deviceId, cb, handler);
            }
        }
        return null;
    }

}
