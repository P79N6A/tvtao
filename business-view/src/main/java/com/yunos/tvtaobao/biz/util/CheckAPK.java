package com.yunos.tvtaobao.biz.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;

import com.yunos.tv.core.common.AppDebug;

import java.util.HashSet;

public class CheckAPK {

    private static final String TAG = "CheckAPK";

    /**
     * 检查APK包是否合法：1、包名是否一致；2、版本号是否与服务端一致；3、版本号是否比当前应用新；4、签名是否一致。
     * @param context
     * @param path
     *            apk路径
     * @param versionCode
     *            服务端versionCode
     * @return true if apk is valid
     */
    public static boolean checkAPKFile(Context context, String path, String versionCode) {
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            AppDebug.e(TAG, TAG + ".checkAPKFile.cannot get PackageManager object");
            return false;
        }
        // 获取APK包的签名
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_SIGNATURES);
        if (info == null) {
            AppDebug.e(TAG, TAG + ".checkAPKFile.cannot get PackageInfo object");
            return false;
        }
        AppDebug.d(TAG, TAG + ".checkAPKFile.apk packageName: " + info.packageName + " apk versionCode: "
                + info.versionCode + " apk versionName: " + info.versionName);
        // 判断APK包名是否与应用一致
        if (!context.getPackageName().equalsIgnoreCase(info.packageName)) {
            AppDebug.e(TAG, TAG + ".checkAPKFile.packageName mismatch, apk packageName: " + info.packageName
                    + " app packageName: " + context.getPackageName());
            return false;
        }
        // 判断APK版本号与服务端是否一致
        if (!versionCode.equalsIgnoreCase(String.valueOf(info.versionCode))) {
            AppDebug.e(TAG, TAG + ".checkAPKFile.versionCode mismatch between apk and server, apk versionCode: "
                    + info.versionCode + " server versionCode: " + versionCode);
            return false;
        }
        // 获取应用签名
        PackageInfo myInfo = null;
        try {
            myInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (NameNotFoundException e) {
            AppDebug.e(TAG, TAG + ".checkAPKFile.cannot get local package info: " + e.getLocalizedMessage());
            return false;
        }
        // 判断APK版本号是否比应用新
        if (myInfo.versionCode >= info.versionCode) {
            AppDebug.e(TAG, TAG + ".checkAPKFile.apk versionCode is downgraded, apk versionCode: " + info.versionCode
                    + " app versionCode: " + myInfo.versionCode);
            return false;
        }
        return isSignatureSame(info.signatures, myInfo.signatures);
    }

    /**
     * 校验两个签名是否一致
     * @param s1
     *            签名1
     * @param s2
     *            签名2
     * @return true if signatures are the same
     */
    private static boolean isSignatureSame(Signature[] s1, Signature[] s2) {
        if (s1 == null || s2 == null) {
            AppDebug.d(TAG, "at least one signature is null");
            return false;
        }

        HashSet<Signature> set1 = new HashSet<Signature>();
        for (Signature sig : s1) {
            set1.add(sig);
        }

        HashSet<Signature> set2 = new HashSet<Signature>();
        for (Signature sig : s2) {
            set2.add(sig);
        }

        if (set1.equals(set2)) {
            AppDebug.d(TAG, "signature is same");
            return true;
        }

        AppDebug.e(TAG, "signature is not consistent");
        return false;
    }
}
