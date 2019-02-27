package com.yunos.tvtaobao.biz.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.taobao.atlas.update.AtlasUpdater;
import com.taobao.atlas.update.exception.MergeException;
import com.taobao.atlas.update.model.UpdateInfo;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;

import org.osgi.framework.BundleException;

import java.io.File;

/**
 * Created by huangdaju on 17/8/21.
 */

public class Updater {

    private final static String TAG = "Updater";
    private static boolean isUpdate;

    public static void update(final Handler serviceHandler, final Context context, final com.yunos.tvtaobao.biz.model.AppInfo updateInfo) {
        if (isUpdate) {
            Log.e(TAG, "当前进程下update already success");
            return;
        }
        String versionNum = String.valueOf(AppInfo.getAppVersionNum());
        if (versionNum.compareTo(updateInfo.version) < 0) {
            isUpdate = true;
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        File patchFile = new File("/sdcard/Android/data/com.yunos.tvtaobao/cache",
                                "patch-" + updateInfo.mUpdateInfo.updateVersion + "@" + updateInfo.mUpdateInfo.baseVersion + ".tpatch");
                        Log.d(TAG, "path:" + patchFile.getAbsolutePath());
                        try {
//                            toast("开始合并so",context);
                            AtlasUpdater.update(updateInfo.mUpdateInfo, patchFile);

                        } catch (MergeException e) {
                            Log.e(TAG, "e:" + e);
                            e.printStackTrace();
                            Message msg = serviceHandler.obtainMessage(UpdatePreference.LOG_RECEIVE);
                            serviceHandler.sendMessage(msg);
                        } catch (BundleException e) {
                            Log.e(TAG, "e:" + e);
                            e.printStackTrace();
                            Message msg = serviceHandler.obtainMessage(UpdatePreference.LOG_RECEIVE);
                            serviceHandler.sendMessage(msg);
                        } finally {
                            Message msg = serviceHandler.obtainMessage(UpdatePreference.LOG_STOP);
                            serviceHandler.sendMessage(msg);
                        }

//                        Toast.makeText(context, "合并成功", Toast.LENGTH_LONG).show();
//                        toast("合并成功",context);
                        Log.e(TAG, "update_finish");
//                        toast("更新成功，请重启app", context);
                    }
                }).start();

            } catch (Throwable e) {
                e.printStackTrace();
//                toast("更新失败, " + e.getMessage(), context);
                Message msg = serviceHandler.obtainMessage(UpdatePreference.LOG_RECEIVE);
                serviceHandler.sendMessage(msg);
            }
        }

    }

    private static void toast(final String msg, final Context context) {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//            }
//        });
    }
}
