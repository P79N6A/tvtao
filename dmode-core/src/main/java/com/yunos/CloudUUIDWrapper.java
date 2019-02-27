package com.yunos;


import android.content.Context;

import com.yunos.tvtaobao.uuid.CloudUUID;

/**
 * Created by rca on 18/09/2017.
 */

public class CloudUUIDWrapper {
    public static String getCloudUUID() {
        return CloudUUID.getCloudUUID();
    }

    /**
     * for D-mode only
     */
    public interface IUUIDListener {
        void onCompleted(int error, float time);
    }

    public static void init(Context context, boolean log) {
        CloudUUID.init(context, log);
    }

    /**
     * for D-mode only
     *
     * @param listener
     * @param productName
     * @param ttid
     */
    public static void generateUUIDAsyn(final IUUIDListener listener, String productName, String ttid) {
        CloudUUID.generateUUIDAsyn(new com.yunos.tvtaobao.uuid.IUUIDListener() {
            @Override
            public void onCompleted(int i, float v) {
                listener.onCompleted(i, v);
            }
        }, productName, ttid);
    }

    public static void setAndroidOnly(boolean flag){
        CloudUUID.setAndroidOnly(flag);
    }


}
