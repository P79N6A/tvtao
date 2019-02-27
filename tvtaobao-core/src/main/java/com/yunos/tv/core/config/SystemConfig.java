package com.yunos.tv.core.config;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.yunos.tv.core.common.AppDebug;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 系统配置
 * @author tianxiang
 * @date 2012-9-21 15:09:53
 */
public class SystemConfig {

    private static String TAG = "SystemConfig";

    public static final String HTTP_PARAMS_ENCODING = "UTF-8";

    public static Float DENSITY;// 屏幕密度比例 如0.75 1.0

    public static Integer DENSITY_DPI;// 屏幕密度 如120 160 240

    public static Integer SCREEN_WIDTH;// 屏幕宽度 单位为像素

    public static Integer SCREEN_HEIGHT;// 屏幕高度 单位为像素

    private static Boolean hasInited = false;


    // 是否yunos4.0系统
    public static boolean SYSTEM_YUNOS_4_0;
    // android系统版本号
    public static String SYSTEM_VERSION;

    // 版本数字
    public static Integer APP_VERSION_NUMBER;

    // 版本字符
    public static String APP_VERSION;

    public static void init(Context context) {
        if (hasInited) {
            return;
        }
        if (context == null) {
            throw new IllegalArgumentException("The context was null");
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        DENSITY = dm.density;
        DENSITY_DPI = dm.densityDpi;
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            APP_VERSION_NUMBER = info.versionCode;
            APP_VERSION = info.versionName;
        } catch (Exception e) {
            AppDebug.e("SystemConfig-getAppVersion", "读取版本号异常: " + e.toString());
        }
        SYSTEM_YUNOS_4_0 = isYunOS40System();
        SYSTEM_VERSION = getSystemVersion();

        AppDebug.v(TAG, TAG + ".init.versionCode = " + APP_VERSION_NUMBER + ", versionName = " + APP_VERSION
                + ", DisplayMetrics = " + dm + ", SYSTEM_YUNOS_4_0 = " + SYSTEM_YUNOS_4_0 + ",SYSTEM_VERSION = "
                + SYSTEM_VERSION);
        hasInited = true;
    }

    /**
     * 是否是Yunos4.0系统
     * @return
     */
    public static boolean isYunOS40System() {
        String res_version = getSystemProp("android.os.SystemProperties", "ro.yunos.version");
        AppDebug.i(TAG, TAG + ".isYunOS40System.res_version = " + res_version);

        if (!TextUtils.isEmpty(res_version) && res_version.compareToIgnoreCase("4.0.0") >= 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取系统版本号
     * @return
     */
    public static String getSystemVersion() {
        // 先获取yunos版本号
        String res_version = getSystemProp("android.os.SystemProperties", "ro.yunos.build.version.release");
        // 如果没有，则获取android系统版本号
        if (TextUtils.isEmpty(res_version)) {
            res_version = Build.VERSION.RELEASE;
        }
        //假定仍然没有，则再去获取其他属性（应该不会出现这种情况）
        if (TextUtils.isEmpty(res_version)) {
            res_version = getSystemProp("android.os.SystemProperties", "ro.build.version.release");
        }
        if (TextUtils.isEmpty(res_version)) {
            res_version = getSystemProp("android.os.SystemProperties", "ro.yunos.version");
        }

        // 如果没有获取到系统版本号，打印错误日志
        if (TextUtils.isEmpty(res_version)) {
            res_version = "";
            Log.e(TAG, TAG + "getSystemVersion.res_version = " + res_version);
        }

        return res_version;
    }

    /**
     * 通过类名和属性名，获取系统属性
     * @param className
     * @param propName
     * @return
     */
    public static String getSystemProp(String className, String propName) {
        Class<?> props;
        String res_version = null;
        try {
            props = Class.forName(className);
            if (props != null) {
                Method method = props.getMethod("get", String.class);
                if (method != null) {
                    res_version = (String) method.invoke(props.newInstance(), propName);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return res_version;
    }

}
