package com.yunos.tvtaobao.payment.utils;

import android.text.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CloudUUIDWrapper {
    private static String uuid;

    public static String getCloudUUID() {
        if (TextUtils.isEmpty(uuid)) {
            try {
                Class clz = Class.forName("com.yunos.CloudUUIDWrapper");
                Method isYunos = clz.getDeclaredMethod("getCloudUUID");
                uuid = (String) isYunos.invoke(clz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return uuid;
    }
}
