package com.yunos.tv;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;


public class TYIDManagerWrapper {


    public interface IServiceConnectStatus {
        void onServiceConnectStatus(int status);
    }

    private static TYIDManagerWrapper instance;

    private static int status = 0;

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

    private TYIDManagerWrapper(Context context) {

    }

    private TYIDManagerWrapper(Context context, final IServiceConnectStatus callback) {

    }

    public String peekToken(String param) {
        return null;
    }
}
