package com.yunos.tvtaobao.biz.request.ztc;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.yunos.tvtaobao.biz.request.BusinessRequest;


/**
 * Created by rca on 12/06/2017.
 */

public class ZTCUtils {
    public interface ZTCItemCallback {
        void onSuccess(String id);

        void onFailure(String msg);
    }

    public static void destroy() {
        if (redirectRequest != null) {
            redirectRequest.destroy();
        }
    }

    private static RedirectRequest redirectRequest;

    public static void getZtcItemId(final Activity activity, final String title, final String picUrl, final String eurl, final ZTCItemCallback callback) {
        if (activity == null || callback == null)
            return;
        final String userAgent = generateInfo(activity);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(eurl)) {
                    if (redirectRequest != null) {
                        redirectRequest.destroy();
                    }
                    redirectRequest = new RedirectRequest(activity, userAgent, true, new RedirectRequest.RedirectRequestListener() {
                        @Override
                        public void onItemIdRetrieveResult(boolean success, String id) {
                            if (success) {
                                callback.onSuccess(id);
                                KMRefluxRequest request = new KMRefluxRequest(id, title, picUrl);
                                BusinessRequest.getBusinessRequest().baseRequest(request, null, false);
                            } else {
                                callback.onFailure(null);
                            }
                        }
                    });
                    redirectRequest.requestParams(eurl);

                }
            }
        });

    }

    public static void getZtcItemId(final Activity activity, final String eurl, final ZTCItemCallback callback) {
        getZtcItemId(activity, null, null, eurl, callback);
    }

    private static String generateInfo(Context ctx) {

        String packageName = ctx.getPackageName();
        int versionCode = 0;
        String versionName = null;
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_META_DATA);
            versionCode = info.versionCode;
            versionName = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder phoneInfo = new StringBuilder(String.format(
                "AppVer:%s,VerCode:%d", versionName, versionCode));
        phoneInfo.append(String.format(", BOOTLOADER: %s",
                Build.BOOTLOADER));
        phoneInfo.append(String.format(",APP: %s", ctx.getPackageName()));
        // BRAND 运营商
        phoneInfo.append(String.format(", BRAND: %s", Build.BRAND));
        // 指纹
//        phoneInfo.append(String.format(", FINGERPRINT: %s",
//                Build.FINGERPRINT));
        // HARDWARE 硬件
        phoneInfo.append(String.format(", HARDWARE: %s",
                Build.HARDWARE));
        // phoneInfo += ", HOST: " + android.os.Build.HOST;
        // phoneInfo += ", ID: " + android.os.Build.ID;
        // MANUFACTURER 生产厂家
        phoneInfo.append(String.format(", MANUFACTURER: %s",
                Build.MANUFACTURER));
        // MODEL 机型
        phoneInfo.append(String.format(", MODEL: %s", Build.MODEL));
        phoneInfo.append(String.format(", PRODUCT: %s",
                Build.PRODUCT));
        phoneInfo.append(String.format(", VERSION.RELEASE: %s",
                Build.VERSION.RELEASE));
        phoneInfo.append(String.format(", VERSION.CODENAME: %s",
                Build.VERSION.CODENAME));
        // VERSION.INCREMENTAL 基带版本
        phoneInfo.append(String.format(", VERSION.INCREMENTAL: %s",
                Build.VERSION.INCREMENTAL));
        // VERSION.SDK SDK版本
        phoneInfo.append(String.format(", VERSION.SDK: %s",
                Build.VERSION.SDK));
        phoneInfo.append(String.format(", VERSION.SDK_INT: %d",
                Build.VERSION.SDK_INT));
        return phoneInfo.toString();
    }
}
