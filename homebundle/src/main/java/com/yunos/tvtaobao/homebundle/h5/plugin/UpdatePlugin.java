package com.yunos.tvtaobao.homebundle.h5.plugin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.taobao.atlas.startup.AtlasBridgeApplication;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.RtEnv;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.util.MD5Util;
import com.yunos.tvtaobao.homebundle.activity.HomeActivity;
import com.yunos.tvtaobao.biz.controller.Update;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.biz.util.UserTrackUtil;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by pan on 2017/7/10.
 */

public class UpdatePlugin {
    private final static String TAG = UpdatePlugin.class.getName();

    private WeakReference<HomeActivity> mReference;
    private static final String RESULT = "result";
    private UpdateCallback plugin;

    private JsHandler mHandler;

    private final static int GOTO_UPDATE = 100001;
    private final static int IS_UPDATE = 100002;

    public UpdatePlugin(WeakReference<HomeActivity> reference) {
        this.mReference = reference;
        mHandler = new JsHandler(new WeakReference<>(this));
        onInitPlugin();
    }

    private void onInitPlugin() {
        plugin = new UpdateCallback(new WeakReference<>(this));
        BlitzPlugin.bindingJs("tvtaobao_isUpdate", plugin);
        BlitzPlugin.bindingJs("tvtaobao_gotoUpdate", plugin);
        AppDebug.i(TAG, "mStbIDGetCallback");
        BlitzPlugin.bindingJs("tvtaobao_stbId_get", mStbIDGetCallback);

    }

    private long cbData_final;

    private boolean onHandleCall(final String param, long cbData) {
        cbData_final = cbData;

        JSONObject object = JSON.parseObject(param);
        final String methodName = object.getString("methodName");
        AppDebug.e(TAG, TAG + ".onHandleCallPay methodName : " + methodName);

        if (param != null && methodName != null) {
            AppDebug.e(TAG, "param : " + param + "     methodName : " + methodName);
            if (methodName.equalsIgnoreCase("tvtaobao_isUpdate")) {
                Message msg = new Message();
                msg.obj = param;
                msg.what = IS_UPDATE;
                mHandler.sendMessage(msg);
            } else if (methodName.equalsIgnoreCase("tvtaobao_gotoUpdate")) {
                Message msg = new Message();
                msg.obj = param;
                msg.what = GOTO_UPDATE;
                mHandler.sendMessage(msg);
            }
        }

        return true;
    }

    private static class UpdateCallback implements BlitzPlugin.JsCallback {

        private WeakReference<UpdatePlugin> mReference;

        private UpdateCallback(WeakReference<UpdatePlugin> reference) {
            mReference = reference;
        }

        @Override
        public void onCall(String param, long cbData) {
            AppDebug.i(TAG, "onCall --> param  =" + param + ";  cbData = " + cbData);
            if (mReference != null && mReference.get() != null) {
                UpdatePlugin plugin = mReference.get();
                plugin.onHandleCall(param, cbData);
            }
        }

    }

    private Update update;

    private static class JsHandler extends Handler {
        private WeakReference<UpdatePlugin> mReference;

        private JsHandler(WeakReference<UpdatePlugin> reference) {
            mReference = reference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mReference == null || mReference.get() == null)
                return;

            UpdatePlugin plugin = mReference.get();
            if (plugin != null) {
                plugin.update = Update.get("tvtaobao");
            }
            final BzResult result;
            switch (msg.what) {
                case IS_UPDATE:
                    AppDebug.e(TAG, TAG + ".Handler IS_UPDATE");
                    result = new BzResult();
                    if (plugin.update != null) {
                        plugin.update.setOnDownloadProgressListner(new Update.DownloadProgressListner() {
                            @Override
                            public void onRetryDownload(int progress) {
                            }

                            @Override
                            public void onUpdateProgress(int progress) {
                            }

                            @Override
                            public void onFileExists() {
                            }

                            @Override
                            public void onFildValid() {
                                AppDebug.e(TAG, TAG + ".onHandleCall onFileExists");
                                result.addData("isUpdate", true);
                                result.addData("version", AppInfo.getAppVersionNum());
                                result.setSuccess();
                                String res = result.toJsonString();
                                BlitzPlugin.responseJs(true, res, mReference.get().cbData_final);
                            }

                            @Override
                            public void onInstall() {

                            }

                            @Override
                            public void onError(int errorType) {

                            }

                            @Override
                            public void onResumeDownload() {

                            }

                            @Override
                            public void onChangeDownloadType(int type) {

                            }
                        });
                    }
                    break;
                case GOTO_UPDATE:
                    AppDebug.e(TAG, TAG + ".Handler GOTO_UPDATE");
                    result = new BzResult();
                    result.addData("version", AppInfo.getAppVersionNum());
                    if (mReference.get().install()) {
                        result.setSuccess();
                        String res = result.toJsonString();
                        BlitzPlugin.responseJs(true, res, mReference.get().cbData_final);
                    } else {
                        result.addData(RESULT, "false");
                        String res = result.toJsonString();
                        BlitzPlugin.responseJs(false, res, mReference.get().cbData_final);
                    }
                    break;
            }

        }
    }

    /**
     * 调用PackageInstaller安装更新
     */
    private boolean install() {
        if (update == null)
            return false;

        Bundle bundle = update.getBundle();
        if (bundle == null){
            bundle= (Bundle) RtEnv.get(RtEnv.KEY_UPDATE_BUNDLE);
            if(bundle == null){
                return false;
            }
//            return false;
        }else {
            RtEnv.set(RtEnv.KEY_UPDATE_BUNDLE,bundle);
        }

        String mTargetFile = bundle.getString(UpdatePreference.INTENT_KEY_TARGET_FILE);
        long mTargetSize = bundle.getLong(UpdatePreference.INTENT_KEY_TARGET_SIZE);
        String mTargetMd5 = bundle.getString(UpdatePreference.INTENT_KEY_TARGET_MD5);
        try {
            File newAPK = new File(mTargetFile);
            newAPK.setReadable(true, false);
            // 校验文件
            if (newAPK.length() != mTargetSize || !mTargetMd5.equalsIgnoreCase(MD5Util.getMD5(newAPK))) {
                UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_INVALID_UPDATE_FILE);
                AppDebug.e(TAG, TAG + ".install,invalid file, file size: " + newAPK.length() + " correct size: "
                        + mTargetSize + " file md5: " + MD5Util.getMD5(newAPK) + " correct MD5: " + mTargetMd5);
                AppDebug.d(TAG, TAG + ".install,delete invalid file: " + newAPK.delete());
            }
        } catch (Exception e) {
            UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
            AppDebug.e(TAG, "get md5 exception: " + e.getLocalizedMessage());
            return false;
        }
        AppDebug.d(TAG, TAG + ".install, MD5 check success, start to install new apk");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + mTargetFile), "application/vnd.android.package-archive");
        try {
            if (mReference != null) {
                HomeActivity homeActivity = mReference.get();
                homeActivity.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
            AppDebug.e(TAG, TAG + ".install,PackageInstaller exception: " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    private static void responseFalse(long cdData) {
        BzResult result = new BzResult();
        result.addData(RESULT, "false");
        String res = result.toJsonString();
        BlitzPlugin.responseJs(false, res, cdData);
    }


    private  BlitzPlugin.JsCallback mStbIDGetCallback = new BlitzPlugin.JsCallback() {
        @Override
        public void onCall(String param, long cdData) {
            if (mReference != null) {
                HomeActivity homeActivity = mReference.get();
                String stbId = DeviceUtil.initMacAddress(homeActivity);
                AppDebug.i(TAG, "mStbIDGetCallback  stb == " + stbId);
                if (stbId != null) {
                    BzResult result = new BzResult();
                    result.addData(RESULT, "true");
                    result.addData("stbID", stbId);
                    result.setSuccess();
                    String res = result.toJsonString();
                    BlitzPlugin.responseJs(true, res, cdData);
                } else {
                    responseFalse(cdData);
                }
            }
        }
    };
}
