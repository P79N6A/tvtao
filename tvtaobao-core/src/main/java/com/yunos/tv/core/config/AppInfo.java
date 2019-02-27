package com.yunos.tv.core.config;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;

public class AppInfo {

    public static final String HTTP_PARAMS_ENCODING = "UTF-8";
    private static Integer appVersionNum;
    private static String appVersionName;
    private static String packageName;

    public static String getPackageName() {
        if (packageName == null) {
            try {
                packageName = CoreApplication.getApplication().getPackageName();
            } catch (Exception e) {
                return null;
            }
        }
        return packageName;
    }

    public static int getAppVersionNum() {
        if (null == appVersionNum) {
            // 读取版本号
            try {
                PackageManager pm = CoreApplication.getApplication().getPackageManager();
                PackageInfo info = pm.getPackageInfo(CoreApplication.getApplication().getPackageName(), 0);
                appVersionNum = info.versionCode;
                appVersionName = info.versionName;
            } catch (Exception e) {
                AppDebug.e("SystemConfig-getAppVersion", "读取版本号异常: " + e.toString());
            }
        }
        return appVersionNum;
    }

    public static String getAppVersionName() {
        if (null == appVersionNum) {
            // 读取版本号
            try {
                PackageManager pm = CoreApplication.getApplication().getPackageManager();
                PackageInfo info = pm.getPackageInfo(CoreApplication.getApplication().getPackageName(), 0);
                appVersionNum = info.versionCode;
                appVersionName = info.versionName;
            } catch (Exception e) {
                AppDebug.e("SystemConfig-getAppVersion", "读取版本号异常: " + e.toString());
            }
        }
        return appVersionName;
    }
}
