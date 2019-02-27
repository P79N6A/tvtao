package com.yunos.tv.core.listener;


import android.text.TextUtils;

import com.alibaba.motu.crashreporter.IUTCrashCaughtListener;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.User;

import java.util.HashMap;
import java.util.Map;

public class MotuCrashCaughtListener implements IUTCrashCaughtListener {

    private String TAG = "MotuCrashCaughtListener";

    public MotuCrashCaughtListener() {
    }

    @Override
    public Map<String, Object> onCrashCaught(Thread arg0, Throwable arg1) {
        AppDebug.i(TAG, "onCrashCaught --> arg0 = " + arg0 + "; arg1 = " + arg1);
        Map<String, Object> lMap = new HashMap<String, Object>();
        try {
            String uuid = CloudUUIDWrapper.getCloudUUID();
            if (!TextUtils.isEmpty(uuid)) {
                lMap.put("uuid", uuid);
            }

            String nick = User.getNick();
            if (!TextUtils.isEmpty(nick)) {
                lMap.put("nick", nick);
            }

            String userId = User.getUserId();
            if (!TextUtils.isEmpty(userId)) {
                lMap.put("userId", userId);
            }

        } catch (Exception e) {
        }
        return lMap;
    }
}
