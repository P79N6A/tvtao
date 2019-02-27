package com.yunos;

import android.content.Context;

import com.yunos.baseservice.clouduuid.CloudUUID;


/**`
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

    /**
     * for D-mode only
     *
     * @param context
     * @param log
     */
    public static void init(Context context, boolean log) {
    }

    /**
     * for D-mode only
     *
     * @param listener
     * @param productName
     * @param ttid
     */
    public static void generateUUIDAsyn(IUUIDListener listener, String productName, String ttid) {
    }

    public static void setAndroidOnly(boolean flag) {
    }
}
