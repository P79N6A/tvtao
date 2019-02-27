package com.yunos.tv.core.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.yunos.tv.core.common.AppDebug;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * 取得设备相关的信息接口
 *
 * @author tingmeng.ytm
 */
public class DeviceUtil {
    /**
     * 定义显示屏类型
     *
     * @author Administrator
     */
    public static class SCREENTYPE {

        // 屏幕为 720P
        public static final int ScreenType_720p = 1280;

        // 屏幕为 1080P
        public static final int ScreenType_1080P = 1920;
    }

    ;

    /**
     * 获得设备屏幕的缩放比
     */
    public static float getScreenScaleFromDevice(Context mContext) {

        float screenScale = 1.0f;

        Context context = mContext.getApplicationContext();

        DisplayMetrics dm = new DisplayMetrics();

        dm = context.getResources().getDisplayMetrics();

        int screenWidth = dm.widthPixels;
        //int screenHeight = dm.heightPixels;

        screenScale = 1.0f;

        float scale = (float) screenWidth / (float) SCREENTYPE.ScreenType_720p;

        if (scale > 1.2) {
            screenScale = 1.5f;
        }

        return screenScale;
    }

    /**
     * 获取屏幕的像素密度比
     *
     * @param mContext
     * @return
     */
    public static float getDensityFromDevice(Context mContext) {
        Context context = mContext.getApplicationContext();

        DisplayMetrics dm = new DisplayMetrics();

        dm = context.getResources().getDisplayMetrics();

        return dm.density;
    }

    /**
     * 取得Display的Metrics
     *
     * @param mContext
     * @return
     */
    public static DisplayMetrics getDisplayMetricsFromDevice(Context mContext) {
        DisplayMetrics dm = new DisplayMetrics();
        return dm;
    }

    /**
     * 取得设备的Brand
     *
     * @return
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 取得设备的model
     *
     * @return
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 取得设备的product
     *
     * @return
     */
    public static String getDeviceProduct() {
        return android.os.Build.PRODUCT;
    }


    private static String stb_result = null;  //机顶盒号

    /**
     * 获取机顶盒号
     *
     * @return
     */
    public static String getStbID() {
        return stb_result;
    }

    /**
     * 初始化机顶盒号
     *
     * @param context
     * @return
     */
    public static String initMacAddress(Context context) {

        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase("eth0"))
                    continue;
                byte[] mac = intf.getHardwareAddress();
                if (mac != null) {
                    StringBuilder buf = new StringBuilder();
                    for (int idx = 0; idx < mac.length; idx++)
                        buf.append(String.format("%02X:", mac[idx]));
                    if (buf.length() > 0)
                        buf.deleteCharAt(buf.length() - 1);
                    if (buf != null) {
                        stb_result = "MAC" + buf.toString().replaceAll(":", "");
                        // XXX 不保存 mac地址
                    }
                }
            }
            if (TextUtils.isEmpty(stb_result)) {
                WifiManager wifiMan = (WifiManager) context
                        .getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiMan.getConnectionInfo();
                String mac = info.getMacAddress();
                stb_result = "MAC" + mac.replace(":", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stb_result;
    }


    public static int  getYuyinPackageCode(Context context) {
        int versionCode = 0;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.yunos.tv.alitvasr", PackageManager.GET_CONFIGURATIONS);
            AppDebug.e("TAG", "packageInfo------" + packageInfo.versionCode);
            AppDebug.e("TAG", "packageInfo------" + packageInfo.versionName);
            versionCode = packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }
}
