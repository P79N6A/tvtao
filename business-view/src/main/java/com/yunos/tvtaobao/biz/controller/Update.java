package com.yunos.tvtaobao.biz.controller;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunos.tv.core.RtEnv;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;
import com.yunos.tv.core.config.UpdateStatus;
import com.yunos.tv.core.util.SystemUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tv.net.network.NetworkManager;
import com.yunos.tvtaobao.biz.dialog.UpdateDialog;
import com.yunos.tvtaobao.biz.model.AppInfo;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.service.UpdateService;
import com.yunos.tvtaobao.biz.util.CheckAPK;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.biz.util.UserTrackUtil;


import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author quanquan.rqq
 *         update interface
 */
public class Update {

    private static final String TAG = "Update";

    private static Map<String, Update> sUpdateMap = new HashMap<String, Update>();

    private static final long FORCE_INSTALL_DELAY_TIME = 0;

    private static final long UNFORCE_INSTALL_DELAY_TIME = 1;

    private int mMTopRetryTimes = 2;

    private int mDownloadRetryTimes = 2;

    private int mExceptionRetryTimes = 2;

    private int mDetectConnectionRetryTimes = 2;

    private Thread mDownloadThread;

    private AsyncTask<Void, Void, Boolean> mTopAppCheckTask;

    private AsyncTask<Void, Void, Boolean> mCheckApkTask;

    private AsyncTask<Void, Void, Boolean> mCheckNetworkTask;

    private UpdateService mContext;

    private String mMtopApi;

    private String mMtopApiInfo;

    private String mUpdateParams;

    private String mAppCode;

    private String mTargetFile;

    private String mTargetMd5;

    private String mReleaseNote;

    private String mVersionCode;

    private long mTargetSize;

    private AppInfo mAppInfo;

    private Bundle mBundle;

    private ABDownloader mDownloader;

    private boolean mIsStop = false;

    private boolean mIsForced = false;

    private boolean mIsStartActivity = false;

    private boolean mIsNeedRequest = false;

    private DownloadProgressListner mDownloadProgressListner;

    private static MyHandler mMyHandler;

    private static Handler mServiceHandler;

    private UpdateDialog updateDialog;


    private static class LogReceiveListener implements RequestListener<String> {

        public LogReceiveListener() {
        }

        @Override
        public void onRequestDone(String data, int resultCode, String handleMessagemsg) {
            Log.d(TAG, "onRequestDone " + data);
        }
    }


    private static class UpgradeAppListener implements RequestListener<String> {

        private WeakReference<MyHandler> mMyHandlerWeakReference;

        private Context mContext;

        public UpgradeAppListener(WeakReference<MyHandler> handler,Context context) {
            mMyHandlerWeakReference = handler;
            mContext=context;
        }

        @Override
        public void onRequestDone(String data, int resultCode, String handleMessagemsg) {
//            AppDebug.d(TAG, "onRequestDone " + data);
            Log.d(TAG, "onRequestDone " + data);
            if (mMyHandlerWeakReference == null || mMyHandlerWeakReference.get() == null) {
                return;
            }

            MyHandler myHandler = mMyHandlerWeakReference.get();
            if (resultCode == 200 && data != null) {  //有升级数据开始升级

                if(Utils.ExistSDCard()){

//                    SharedPreferences sp = mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
                    boolean isMoHeOn= SharePreferences.getBoolean(UpdatePreference.IS_MOHE_LOG_ON,false);
                    boolean isLianMengLogOn=SharePreferences.getBoolean(UpdatePreference.IS_LIANMNEG_LOG_ON,false);
                    boolean isYiTiJiLogOn=SharePreferences.getBoolean(UpdatePreference.IS_YITIJI_LOG_ON,false);
                    if(Config.MOHE.equals(Config.getChannel())&&isMoHeOn){
                        LogUtils.getInstance(mContext).start();
                    }else if(Config.LIANMENG.equals(Config.getChannel())&&isLianMengLogOn){
                        LogUtils.getInstance(mContext).start();
                    }else if(Config.YITIJI.equals(Config.getChannel())&&isYiTiJiLogOn){
                        LogUtils.getInstance(mContext).start();
                    }
                }
                AppInfo appInfo = new AppInfo(JSON.parseObject(data));
                AppDebug.d(TAG, "onRequestDone " + appInfo.toString());
                Message handleMessage = new Message();
                handleMessage.what = UpdatePreference.MTOP_DONE;
                handleMessage.obj = appInfo;
                myHandler.sendMessage(handleMessage);




                SharedPreferences p = mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = p.edit();
                e.putString(UpdatePreference.UPDATE_TIPS, StringUtil.isEmpty(appInfo.getReleaseNote())?"":appInfo.getReleaseNote());

                //image1.背景图；image2.圆点；image3.稍后再说聚焦；image4.稍后再说未聚焦；image5.马上升级聚焦；image6.马上升级未聚焦；
                e.putString(UpdatePreference.IMAGE1, StringUtil.isEmpty(appInfo.getImage1()) ? "" : appInfo.getImage1());
                e.putString(UpdatePreference.IMAGE2, StringUtil.isEmpty(appInfo.getImage2()) ? "" : appInfo.getImage2());
                e.putString(UpdatePreference.IMAGE3, StringUtil.isEmpty(appInfo.getImage3()) ? "" : appInfo.getImage3());
                e.putString(UpdatePreference.IMAGE4, StringUtil.isEmpty(appInfo.getImage4()) ? "" : appInfo.getImage4());
                e.putString(UpdatePreference.IMAGE5, StringUtil.isEmpty(appInfo.getImage5()) ? "" : appInfo.getImage5());
                e.putString(UpdatePreference.IMAGE6, StringUtil.isEmpty(appInfo.getImage6()) ? "" : appInfo.getImage6());
                e.putString(UpdatePreference.NEW_RELEASE_NOTE, StringUtil.isEmpty(appInfo.getNewReleaseNote()) ? "" : appInfo.getNewReleaseNote());
                e.putString(UpdatePreference.UPGRADE_MODE, StringUtil.isEmpty(appInfo.getUpgradeMode()) ? "" : appInfo.getUpgradeMode());
                e.putString(UpdatePreference.COLOR, StringUtil.isEmpty(appInfo.getColor()) ? "" : appInfo.getColor());
                e.putString(UpdatePreference.LATER_ON, StringUtil.isEmpty(appInfo.getLaterOn()) ? "" : appInfo.getLaterOn());
                e.putString(UpdatePreference.UPGRADE_NOW, StringUtil.isEmpty(appInfo.getUpgradeNow()) ? "" : appInfo.getUpgradeNow());

                e.commit();

            } else {
                myHandler.sendEmptyMessage(UpdatePreference.MTOP_FAIL);

            }
        }

    }

    private static class MyHandler extends Handler {

        private WeakReference<Update> mOuter;

        public MyHandler(Update up) {
            mOuter = new WeakReference<Update>(up);
        }

        @Override
        public void handleMessage(Message msg) {
            Update up = mOuter.get();
            if (up == null)
                return;
            switch (msg.what) {
                case UpdatePreference.MTOP_DONE:
                    Log.i(TAG, TAG + ".handleMessage.MTOP_DONE");
                    up.mAppInfo = (AppInfo) msg.obj;
                    if (up.mAppInfo.type == 0){
                        up.processUpdateInfo();
                    }else if (up.mAppInfo.type == 1){
                    up.processTpatchUpdateInfo();
                    }


                    if(!up.mAppInfo.isForced){

                        SharedPreferences sp = up.mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("releaseNote", up.mAppInfo.releaseNote);
                        editor.apply();
                    }

                    break;
                case UpdatePreference.MTOP_FAIL:
                    Log.e(TAG, TAG + ".handleMessage.fail to get info from mtop, return null, try "
                            + up.mMTopRetryTimes + " more times");
                    UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_MTOP_FAIL);
                    if (up.mMTopRetryTimes != 0) {
                        up.mMTopRetryTimes--;
                        up.onRetryDownload();//TODO??mtop请求失败，为什么调用重新下载呢，应该重新请求mtop吧
                        up.start();
                    } else {
                        UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_NETWORK_INAVAILABLE);
                        if (up.mIsForced) {
                            up.mIsNeedRequest = true;
                            up.onError(UpdatePreference.ERROR_TYPE_NETWORK_INAVAILABLE);
                        } else
                            up.sendTerminatedMessage();
                    }
                    break;
                case UpdatePreference.NEW_APK_EXIST:
                    Log.d(TAG, TAG + ".handleMessage.new apk exist, wait to be checked");
                    up.onFileExists();
                    break;
                case UpdatePreference.NEW_APK_VALID:
                    Log.d(TAG, TAG + ".handleMessage.integrated file NEW_APK_VALID");
                    up.onFileValid();
                    up.checkTopApp();
                    break;
                case UpdatePreference.DOWNLOAD_TPATCH_INTERRUPT:
                    Log.d(TAG, TAG + ".handleMessage.DOWNLOAD_TPATCH_INTERRUPT");
                    LogUtils.getInstance(up.mContext).logReceive(up.mContext);
                    break;
                case UpdatePreference.DOWNLOAD_TPATCH_TIMEOUT:
                    Log.d(TAG, TAG + ".handleMessage.DOWNLOAD_TPATCH_TIMEOUT");
                    LogUtils.getInstance(up.mContext).logReceive(up.mContext);
                    up.sendTerminatedMessage();
                    break;
                case UpdatePreference.NEW_TPATCH_VALID:
                case UpdatePreference.DOWNLOAD_TPATCH_DONE:
                    Updater.update(up.mServiceHandler,up.mContext, up.mAppInfo);
 //                   Toast.makeText(up.mContext, "下载完成", Toast.LENGTH_SHORT).show();
                    break;
                case UpdatePreference.DOWNLOAD_DONE:
                    Log.d(TAG, TAG + ".handleMessage.finish downloading, file size: " + msg.obj + ".mIsForced = "
                            + up.mIsForced);
                    // 埋点
                    UserTrackUtil.onCustomEvent(UpdatePreference.UT_DOWNLOAD_SUCCESS);
                    if (up.mIsForced) {
                        up.checkApk();
                    } else {

                        if(up.mAppInfo.type == 0&&!up.mAppInfo.isForced){
                           up.showNotFroceDialog();
                       }else {
                            up.sendTerminatedMessage();
                        }
                    }
                    break;
                case UpdatePreference.NEW_APK_INVALID:
                    Log.e(TAG, TAG + ".handleMessage.invalid new apk, need to download again, try "
                            + up.mDownloadRetryTimes + " more times" + ". mIsForced = " + up.mIsForced);
                    UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_INVALID_UPDATE_FILE);
                    if (up.mDownloadRetryTimes != 0) {
                        up.mDownloadRetryTimes--;
                        up.onRetryDownload();
                        up.start();
                    } else if (up.mIsForced)
                        up.onError(UpdatePreference.ERROR_TYPE_INVALID_UPDATE_FILE);
                    else
                        up.sendTerminatedMessage();
                    break;
                case UpdatePreference.NEW_TPATCH_INVALID:
                    Log.e(TAG, TAG + ".handleMessage.invalid new apk, need to download again, try "
                            + up.mDownloadRetryTimes + " more times" + ". mIsForced = " + up.mIsForced);
                    up.sendTerminatedMessage();
                    break;
                case UpdatePreference.EXCEPTION:
                    Log.e(TAG, TAG + ".handleMessage.fail to delete old file for some reason, try "
                            + up.mExceptionRetryTimes + " more times,EXCEPTION");
                    LogUtils.getInstance(up.mContext).logReceive(up.mContext);
                    UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
                    if (up.mExceptionRetryTimes != 0) {
                        up.mExceptionRetryTimes--;
                        up.onRetryDownload();
                        up.start();
                    } else if (up.mIsForced)
                        up.onError(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
                    else
                        up.sendTerminatedMessage();
                    break;
                case UpdatePreference.DOWNLOAD_TIMEOUT:
                    Log.e(TAG, TAG + ".handleMessage.download timeout, restart update progress, try "
                            + up.mDownloadRetryTimes + " more times, DOWNLOAD_TIMEOUT");
                    LogUtils.getInstance(up.mContext).logReceive(up.mContext);
                    UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_DOWNLOAD_TIMEOUT);
                    if (up.mDownloadRetryTimes != 0) {
                        up.mDownloadRetryTimes--;
                        up.onRetryDownload();
                        up.start();
                    } else {
                        UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_NETWORK_INAVAILABLE);
                        if (up.mIsForced) {
                            up.mIsNeedRequest = true;
                            up.onError(UpdatePreference.ERROR_TYPE_NETWORK_INAVAILABLE);
                        } else
                            up.sendTerminatedMessage();
                    }
                    break;
                case UpdatePreference.DOWNLOAD_INTERRUPT:
                    Log.d(TAG, TAG + ".handleMessage.download interrupt, DOWNLOAD_INTERRUPT");
                    LogUtils.getInstance(up.mContext).logReceive(up.mContext);
                    // up.sendTerminatedMessage();
                    break;

                case UpdatePreference.UPDATE_TERMINATED:
                    Log.d(TAG, TAG + ".handleMessage.update terminated, UPDATE_TERMINATED");
                    up.sendTerminatedMessage();
                    break;

                case UpdatePreference.DOWNLOAD_PROGRESS_UPDATE:
                    up.onUpdateProgress(msg.arg1);
//                    Toast.makeText(up.mContext, "下载进度"+msg.arg1, Toast.LENGTH_SHORT).show();
                    break;

                case UpdatePreference.ERROR_TYPE_NETWORK_DISCONNECT:
                    Log.d(TAG, TAG + ".handleMessage.ERROR_TYPE_NETWORK_DISCONNECT");
                    UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
                    up.onError(UpdatePreference.ERROR_TYPE_NETWORK_DISCONNECT);
                    break;

                case UpdatePreference.SHOW_UPDATE_DIALOG:


                    if(!up.mAppInfo.isForced){

                        SharedPreferences sp = up.mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
                        String releaseNote = sp.getString("releaseNote", "");
//                        long time = sp.getLong("update_dialog_show_time", 0);
//                        if (DateUtils.isToday(time)){
//                            break;
//                        }

                        up.showUpdateDialog(msg.getData(),releaseNote);

                    }
                default:
                    break;
            }
        }
    }

    private void showNotFroceDialog(){
        if(mBundle==null){
            putBundle();
        }

        if (mBundle != null) {
            try {
                Intent startIntent = new Intent();
                startIntent.setData(Uri.parse("not_force_update://yunos_tvtaobao_not_force_update"));
                startIntent.putExtras(mBundle);
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                mContext.startActivity(startIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 传入请求参数
     *
     * @param context
     * @param paramsJson 请求更新参数
     */
    public Update(UpdateService context, Handler serviceHandler, String paramsJson) {
        AppDebug.d(TAG, TAG + ".Update paramsJson = " + paramsJson);
        this.mContext = context;
        this.mServiceHandler = serviceHandler;
        this.mUpdateParams = paramsJson;
        mMyHandler = new MyHandler(this);
        init();
    }

    private void init() {
        // code
        JSONObject jsonParams = JSON.parseObject(mUpdateParams);
        mAppCode = jsonParams.getString("code");
        jsonParams.put("code", UpdatePreference.TVTAOBAO);
        jsonParams.put("version", Build.VERSION.RELEASE);
        String systemInfo = Build.MODEL + "/" + Build.VERSION.SDK;
        jsonParams.put("systemInfo", systemInfo);

        JSONObject json = new JSONObject();
        if (Config.getRunMode() == RunMode.DAILY) {
            json.put("uuid", "762A775537A9C517964028B557C52D64");
            json.put("server", "test");
        } else if (Config.getRunMode() == RunMode.PREDEPLOY) {
            json.put("server", "prerelease");
        } else {
            json.put("server", "online");
        }
        json.put("v", UpdatePreference.API_VERSION);
//        try {
//            jsonParams = new JSONObject(mUpdateParams);
//            mAppCode = jsonParams.getString(jsonParams, "code");
//            // 替换资源名
//            JSONUtil.put(jsonParams, "code", UpdatePreference.TVTAOBAO);
//            // 系统版本
//            JSONUtil.put(jsonParams, "version", Build.VERSION.RELEASE);
//            // 系统信息
//            String systemInfo = Build.MODEL + "/" + Build.VERSION.SDK;
//            JSONUtil.put(jsonParams, "systemInfo", systemInfo);
//        } catch (JSONException e) {
//            Log.e(TAG, TAG + ".init, json exception: " + e);
//        }

        // MTOP server
//        JSONObject json = new JSONObject();
//        if (com.yunos.tv.core.config.Config.getRunMode() == RunMode.DAILY) {
//            // test 服务器，uuid写死
//            AppDebug.d(TAG, TAG + ".MTOP test");
//            JSONUtil.put(jsonParams, "uuid", "762A775537A9C517964028B557C52D64");
//            APPLog.d(TAG, TAG + ".test jasonParam: " + mUpdateParams);
//            JSONUtil.put(json, "server", "test");
//        } else if (com.yunos.tv.core.config.Config.getRunMode() == RunMode.PREDEPLOY) {
//            AppDebug.d(TAG, TAG + ".MTOP predeploy");
//            JSONUtil.put(json, "server", "prerelease");
//        } else {
//            AppDebug.d(TAG, TAG + ".MTOP online");
//            JSONUtil.put(json, "server", "online");
//        }
//        JSONUtil.put(json, "v", UpdatePreference.API_VERSION);
        mMtopApiInfo = json.toString();
        if (jsonParams != null) {
            mUpdateParams = jsonParams.toString();
        }

        // MTOP api
        mMtopApi = UpdatePreference.API;

        AppDebug.d(TAG, TAG + ".full request paramsJson: " + mUpdateParams + ", mMtopApiInfo = " + mMtopApiInfo
                + ", mMtopApi = " + mMtopApi);
    }

    /**
     * 维护资源名和控制升级对象的map
     *
     * @param code 资源名
     * @param up   控制升级的对象
     */
    public static void add(String code, Update up) {
        if (sUpdateMap == null) {
            sUpdateMap = new HashMap<String, Update>();
        }
        AppDebug.i(TAG, TAG + ".add.code = " + code);
        sUpdateMap.put(code, up);
    }

    /**
     * 通过资源名获得其对应的控制升级对象
     *
     * @param code 资源名
     * @return 控制升级对象
     */
    public static Update get(String code) {
        if (sUpdateMap == null)
            return null;
        AppDebug.i(TAG, TAG + ".get.code = " + code);
        return sUpdateMap.get(code);
    }

    /**
     * 删除匹配对
     *
     * @param code 资源名
     */
    public static void remove(String code) {
        if (sUpdateMap == null) {
            return;
        }
        AppDebug.i(TAG, TAG + ".remove.code = " + code);
        sUpdateMap.remove(code);
    }

    /**
     * 启动更新（需先传参数）
     */
    public void start() {
        AppDebug.d(TAG, TAG + ".start.update start");
        mIsNeedRequest = false;
        registerNetworkListner();
        // 检查网络状态
        checkNetwork();
    }

    /**
     * 停止更新线程
     */
    public void stop() {
        AppDebug.d(TAG, TAG + ".stop.update stop");
        mIsStop = true;
        releaseNetworkListner();
        if (mCheckApkTask != null)
            mCheckNetworkTask.cancel(true); // 检查网络状态线程
        if (mTopAppCheckTask != null)
            mTopAppCheckTask.cancel(true); // 检查当前应用线程
        if (mDownloadThread != null)
            mDownloadThread.interrupt(); // 下载线程
    }

    /**
     * 重置重试次数，然后启动更新
     */
    public void retry() {
        AppDebug.d(TAG, TAG + ".retry, init retry times");
        mIsNeedRequest = false;
        mMTopRetryTimes = 2;
        mDownloadRetryTimes = 2;
        mExceptionRetryTimes = 2;
        mDetectConnectionRetryTimes = 2;
        start();
    }

    /**
     * 是否已经处于停止状态
     *
     * @return 如果处于停止状态则返回true
     */
    public boolean isStop() {
        return mIsStop;
    }

    /**
     * 获取启动更新界面的bundle
     *
     * @return
     */
    public Bundle getBundle() {
        return mBundle;
    }

    public boolean getIsStartActivity() {
        return mIsStartActivity;
    }

    public void setIsStartActivity(boolean isStart) {
        AppDebug.d(TAG, TAG + ".setIsStartActivity: " + isStart);
        mIsStartActivity = isStart;
    }

    public void setOnDownloadProgressListner(DownloadProgressListner l) {
        mDownloadProgressListner = l;
    }

    private void onRetryDownload() {
        if (mDownloadProgressListner == null)
            return;
        if (TextUtils.isEmpty(mTargetFile))
            return;
        int progress = 0;
        File file = new File(mTargetFile);
        if (file == null || !file.exists() || !file.isFile() || file.length() == 0)
            progress = 0;
        else
            progress = (int) (file.length() * 100 / mTargetSize);
        AppDebug.d(TAG, TAG + ".onRetryDownload, process = " + progress);
        mDownloadProgressListner.onRetryDownload(progress);
    }

    private void onUpdateProgress(int progress) {
        if (mDownloadProgressListner == null)
            return;
        mDownloadProgressListner.onUpdateProgress(progress);
    }

    private void onFileExists() {
        if (mDownloadProgressListner == null)
            return;
        mDownloadProgressListner.onFileExists();
    }

    private void onFileValid() {
        if (mDownloadProgressListner == null)
            return;
        mDownloadProgressListner.onFildValid();
    }

    private void onInstall() {
        if (mDownloadProgressListner == null)
            return;
        mDownloadProgressListner.onInstall();
    }

    private void onError(int errorType) {
        if (mDownloadProgressListner == null)
            return;
        mDownloadProgressListner.onError(errorType);
    }

    private void onResumeDownload() {
        if (mDownloadProgressListner == null)
            return;
        mDownloadProgressListner.onResumeDownload();
    }

    private void onChangeDownloadType(int type) {
        if (mDownloadProgressListner == null)
            return;
        mDownloadProgressListner.onChangeDownloadType(type);
    }

    // 网络连接正常下，开始处理更新
    private void getUpdateInfo() {
        if (mMyHandler == null || mContext == null || mMtopApi == null || mMtopApiInfo == null || mUpdateParams == null) {
            AppDebug.e(TAG, TAG + ".getUpdateInfoparams error, at least one param is null");
            if (mMyHandler == null)
                return;
            Message msg = mMyHandler.obtainMessage(UpdatePreference.UPDATE_TERMINATED);
            mMyHandler.sendMessage(msg);
            return;
        }

        // 访问 MTOP
        AppDebug.v(TAG, TAG + ".getUpdateInfo start AccessMTop.get");
        JSONObject json = JSON.parseObject(mUpdateParams);
        String code = json.getString("code");
        String versionCode = json.getString("versionCode");
        String versionName = json.getString("versionName");
        String uuid = json.getString("uuid");
        String channelId = json.getString("channelId");
        String systemInfo = json.getString("systemInfo");
        String version = json.getString("version");

        SharedPreferences p = mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        e.putString(UpdatePreference.UPDATE_OBJECT, StringUtil.isEmpty(mUpdateParams)?"":mUpdateParams);
        e.commit();
        try {
            String umtoken= Config.getUmtoken(mContext);
            org.json.JSONObject object = new org.json.JSONObject();
            object.put("umToken", Config.getUmtoken(mContext));
            object.put("wua", Config.getWua(mContext));
            object.put("isSimulator", Config.isSimulator(mContext));
            object.put("userAgent", Config.getAndroidSystem(mContext));
            String extParams = object.toString();

        BusinessRequest.getBusinessRequest().requestUpGrade(version, uuid, channelId, code, versionCode, versionName, systemInfo,umtoken,Config.getModelInfo(mContext),extParams,
                new UpgradeAppListener(new WeakReference<MyHandler>(mMyHandler),mContext));

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void processTpatchUpdateInfo() {
        AppDebug.v(TAG, TAG + ".processTpatchUpdateInfo.mAppInfo = " + mAppInfo);
        if (mAppInfo == null) {
            AppDebug.e(TAG, TAG + ".processUpdateInfo appInfo is null");
            if (mMyHandler == null)
                return;
            Message msg = mMyHandler.obtainMessage(UpdatePreference.UPDATE_TERMINATED);
            mMyHandler.sendMessage(msg);
            onError(UpdatePreference.ERROR_TYPE_INVALID_TPATCH_UPDATE_FILE);
            UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_MTOP_TPATCH_FAIL);
            return;
        }
        // MTOP是否调用成功
        if (!mAppInfo.isSuccess) {
            Log.e(TAG, TAG + ".processUpdateInfo.fail reason from server: " + mAppInfo.returnText);
            if (mMyHandler == null)
                return;
            Message msg = mMyHandler.obtainMessage(UpdatePreference.UPDATE_TERMINATED);
            mMyHandler.sendMessage(msg);
            onError(UpdatePreference.ERROR_TYPE_INVALID_TPATCH_UPDATE_FILE);
            UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_MTOP_TPATCH_FAIL);
            return;
        }
        // 是否已是最新版本，若是，则删除旧升级文件
        if (mAppInfo.isLatest) {
            //TODO 是否删除旧的文件
            return;
        }
        // 检查返回的新版本参数
        if (checkEmpty(mAppInfo.downloadUrl, mAppInfo.downloadMd5, mAppInfo.version, mAppInfo.size)
                || !mAppInfo.apkName.contains(".tpatch")) {
            AppDebug.e(TAG, TAG + ".processUpdateInfo.appInfo params error(from MTOP)");
            if (mMyHandler == null)
                return;
            Message msg = mMyHandler.obtainMessage(UpdatePreference.UPDATE_TERMINATED);
            mMyHandler.sendMessage(msg);
            onError(UpdatePreference.ERROR_TYPE_INVALID_UPDATE_FILE);
            return;
        }
        if (Utils.ExistSDCard()) {
            mTargetFile = mContext.getExternalCacheDir() + File.separator + "patch-" + mAppInfo.mUpdateInfo.updateVersion + "@" + mAppInfo.mUpdateInfo.baseVersion + ".tpatch";
        } else {
            File filesFolder = mContext.getFilesDir();
            if (filesFolder == null) {
                Log.e(TAG, TAG + ".processUpdateInfo.files folder does not exist");
                sendTerminatedMessage();
                releaseNetworkListner();
                onError(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
                UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
                return;
            }
            mTargetFile = filesFolder.getPath() + File.separator + mAppInfo.apkName;
        }
        mTargetMd5 = mAppInfo.downloadMd5;
        mIsForced = mAppInfo.isForced;
        mReleaseNote = mAppInfo.releaseNote;
        mVersionCode = mAppInfo.version;
        try {
            mTargetSize = Long.parseLong(mAppInfo.size);
        } catch (NumberFormatException e) {
            Log.e(TAG, TAG + ".processUpdateInfo.size parse error: " + e.getLocalizedMessage());
        }
        putBundle(); // 将启动activity的数据放进Bundle
        UserTrackUtil.setNewVersionCode(mVersionCode);
        UserTrackUtil.setIsForcedInstall(mIsForced);
        AppDebug.v(TAG, TAG + ".processUpdateInfo.mIsStartActivity = " + mIsStartActivity + ". mIsForced = "
                + mIsForced);
        startDownload(UNFORCE_INSTALL_DELAY_TIME);
    }

    private void processUpdateInfo() {
        AppDebug.v(TAG, TAG + ".processUpdateInfo.mAppInfo = " + mAppInfo);
        if (mAppInfo == null) {
            AppDebug.e(TAG, TAG + ".processUpdateInfo appInfo is null");
            if (mMyHandler == null)
                return;
            Message msg = mMyHandler.obtainMessage(UpdatePreference.UPDATE_TERMINATED);
            mMyHandler.sendMessage(msg);
            onError(UpdatePreference.ERROR_TYPE_INVALID_UPDATE_FILE);
            UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_MTOP_FAIL);
            return;
        }

        // MTOP是否调用成功
        if (!mAppInfo.isSuccess) {
            Log.e(TAG, TAG + ".processUpdateInfo.fail reason from server: " + mAppInfo.returnText);
            if (mMyHandler == null)
                return;
            Message msg = mMyHandler.obtainMessage(UpdatePreference.UPDATE_TERMINATED);
            mMyHandler.sendMessage(msg);
            onError(UpdatePreference.ERROR_TYPE_INVALID_UPDATE_FILE);
            UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_MTOP_FAIL);
            return;
        }
        // 是否已是最新版本，若是，则删除旧升级文件
        if (mAppInfo.isLatest) {
            AppDebug.d(TAG, TAG + ".processUpdateInfo.current version is latest, no new version to update");
            SharedPreferences sp = mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
            String oldFilePath = sp.getString(UpdatePreference.SP_KEY_PATH, "");
            File oldFile = new File(oldFilePath);
            if (oldFile != null && oldFile.exists()) {
                UserTrackUtil.onCustomEvent(UpdatePreference.UT_INSTALL_SUCCESS);
                boolean isDeleted = oldFile.delete();
                AppDebug.d(TAG, TAG + ".processUpdateInfo.delete old update file: " + isDeleted);
            }
            sendTerminatedMessage();
            releaseNetworkListner();
            onChangeDownloadType(UpdatePreference.DOWNLOAD_TYPE_LATEST);
            return;
        }
        // 检查返回的新版本参数
        if (checkEmpty(mAppInfo.downloadUrl, mAppInfo.downloadMd5, mAppInfo.version, mAppInfo.size, mAppInfo.apkName)
                || !mAppInfo.apkName.contains(".apk")) {
            AppDebug.e(TAG, TAG + ".processUpdateInfo.appInfo params error(from MTOP)");
            if (mMyHandler == null)
                return;
            Message msg = mMyHandler.obtainMessage(UpdatePreference.UPDATE_TERMINATED);
            mMyHandler.sendMessage(msg);
            onError(UpdatePreference.ERROR_TYPE_INVALID_UPDATE_FILE);
            return;
        }
        //TODO 修改下载路径
        if (Utils.ExistSDCard()) {
            mTargetFile = mContext.getExternalCacheDir() + File.separator + mAppInfo.apkName;
        } else {
            File filesFolder = mContext.getFilesDir();
            if (filesFolder == null) {
                Log.e(TAG, TAG + ".processUpdateInfo.files folder does not exist");
                sendTerminatedMessage();
                releaseNetworkListner();
                onError(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
                UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
                return;
            }
            mTargetFile = filesFolder.getPath() + File.separator + mAppInfo.apkName;
        }

        mTargetMd5 = mAppInfo.downloadMd5;
        mIsForced = mAppInfo.isForced;
        mReleaseNote = mAppInfo.releaseNote;
        mVersionCode = mAppInfo.version;
        try {
            mTargetSize = Long.parseLong(mAppInfo.size);
        } catch (NumberFormatException e) {
            Log.e(TAG, TAG + ".processUpdateInfo.size parse error: " + e.getLocalizedMessage());
        }

        putBundle(); // 将启动activity的数据放进Bundle

        UserTrackUtil.setNewVersionCode(mVersionCode);
        UserTrackUtil.setIsForcedInstall(mIsForced);

        AppDebug.v(TAG, TAG + ".processUpdateInfo.mIsStartActivity = " + mIsStartActivity + ". mIsForced = "
                + mIsForced);

        onChangeDownloadType(UpdatePreference.DOWNLOAD_TYPE_UNFORCED);
        startDownload(UNFORCE_INSTALL_DELAY_TIME);

//        if (!mIsForced) {
//            // 非强制更新
//            onChangeDownloadType(UpdatePreference.DOWNLOAD_TYPE_UNFORCED);
//            UpdateStatus.setUpdateStatus(UpdateStatus.UNKNOWN, null);
//            startDownload(UNFORCE_INSTALL_DELAY_TIME);
//        } else if (!mIsStartActivity) {//还没有打开activity时，发送广播打开
//            // 强制更新
//            sendBroadcastToStartActivity(mBundle);
//        } else {
//            // 强制更新重试
//            startDownload(FORCE_INSTALL_DELAY_TIME);
//        }
    }

    /**
     * 开启下载线程
     *
     * @param sleepTime 是否限速，每次从网络读取数据时后都会挂起 sleepTime 毫秒
     */
    private void startDownload(final long sleepTime) {
        if (mAppInfo == null || mContext == null) {
            Log.e(TAG, TAG + ".startDownload.appinfo or context is null, cannot call this method directly");
            return;
        }

        if (mDownloadThread != null && mDownloadThread.isAlive()) {
            Log.w(TAG, TAG + ".startDownload.alive download thread exists, will not create a new thread");
            return;
        }

        mDownloadThread = new Thread() {

            @Override
            public void run() {
                AppDebug.d(TAG, TAG + ".startDownload.mDownloadRetryTimes: " + mDownloadRetryTimes);
                String url = mAppInfo.downloadUrl;
                if (mDownloadRetryTimes <= 0) {
                    AppDebug.d(TAG, TAG + ".startDownload.change cdn url to oss url");
                    url = mAppInfo.ossDownloadUrl;
                }

                AppDebug.d(TAG, TAG + ".startDownload.apk name: " + mAppInfo.apkName + ".url = " + url
                        + ", mTargetFile = " + mTargetFile + ",mTargetMd5 = " + mTargetMd5 + ", mVersionCode = "
                        + mVersionCode + ", mTargetSize = " + mTargetSize + ", sleepTime = " + sleepTime);
                String version = null;
                if (mAppInfo.type == 0) { //apk 下载
                    version = mVersionCode;
                    mDownloader = new ApkDownloader(url, mTargetFile, mTargetMd5, version, mReleaseNote, mTargetSize, sleepTime,
                            mContext, mMyHandler);
                } else if (mAppInfo.type == 1) { //tpatch 下载
                    version = mAppInfo.versionName;
                    mDownloader = new TpatchDownLoader(url, mTargetFile, mTargetMd5, version, mTargetSize, sleepTime
                            , mContext, mMyHandler);
                }
                try {
                    mDownloader.download();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mMyHandler != null) {
                        Message msg = mMyHandler.obtainMessage(UpdatePreference.EXCEPTION);
                        mMyHandler.sendMessage(msg);
                    }
                }
            }
        };
        mDownloadThread.setPriority(3); // 调低线程优先级
        if (!mIsStop)
            mDownloadThread.start();
    }

    // 检查网络状态
    private void checkNetwork() {
        mCheckNetworkTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                while (!NetworkManager.instance().isNetworkConnected()) {
                    if (isCancelled()) {
                        AppDebug.d(TAG, TAG + ".checkNetwork.stop check network");
                        return false;
                    }
                    if (mDetectConnectionRetryTimes != 0) {
                        Log.e(TAG, TAG + ".checkNetwork.network disconnected, check " + mDetectConnectionRetryTimes
                                + " more times 1s later");
                        mDetectConnectionRetryTimes--;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            if (isCancelled()) {
                                AppDebug.d(TAG, TAG + ".checkNetwork.stop check network(interrupt)");
                                return false;
                            }
                        }
                    } else {
                        mIsNeedRequest = true;
                        return false;
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                AppDebug.v(TAG, TAG + ".checkNetwork.onPostExecute.result = " + result + ".mIsStop = " + mIsStop);
                if (mIsStop) {
                    return;
                }

                if (result) {
                    if(Boolean.TRUE.equals(RtEnv.get(RtEnv.KEY_SHOULD_CHECK_UPDATE,true))){
                        getUpdateInfo();
                        RtEnv.set(RtEnv.KEY_SHOULD_CHECK_UPDATE,false);
                    }

                } else {
                    if (mMyHandler != null) {
                        Message msg = mMyHandler.obtainMessage(UpdatePreference.ERROR_TYPE_NETWORK_DISCONNECT);
                        mMyHandler.sendMessage(msg);
                    }
                }
            }
        };
        AppDebug.v(TAG, TAG + ".checkNetwork.mIsStop = " + mIsStop);
        if (!mIsStop) {
            mCheckNetworkTask.execute();
        }
    }

    // 周期检查当前运行的应用是否为本应用，是则打开更新界面
    private void checkTopApp() {
        mTopAppCheckTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                while (!mContext.getPackageName().equalsIgnoreCase(SystemUtil.getTopPackageName(mContext))) {
                    if (isCancelled()) {
                        AppDebug.d(TAG, TAG + ".checkTopApp.stop check the current app, termiante update process");
                        return false;
                    }
                    AppDebug.d(TAG, TAG + ".checkTopApp.current app is not " + mContext.getPackageName()
                            + " retry checking in 5s");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        if (isCancelled()) {
                            AppDebug.d(TAG, TAG
                                    + ".checkTopApp.stop check the current app(interrupt), termiante update process");
                            return false;
                        }
                    }
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                AppDebug.d(TAG, TAG + ".checkTopApp. onPostExecute result = " + result);
                if (result) {
                    AppDebug.d(TAG, TAG + ".checkTopApp.current app is: " + mContext.getPackageName()
                            + ", start update activity");
                    startUpdateActivity();
                } else {
                    if (mMyHandler == null)
                        return;
                    Message msg = mMyHandler.obtainMessage(UpdatePreference.UPDATE_TERMINATED);
                    mMyHandler.sendMessage(msg);
                }
            }

            @Override
            protected void onCancelled(Boolean result) {
                AppDebug.d(TAG, TAG + ".checkTopApp.onCancelled");
                onPostExecute(result);
            }
        };

        AppDebug.v(TAG, TAG + ".checkTopApp.mIsStop = " + mIsStop);
        if (!mIsStop) {
            mTopAppCheckTask.execute();
        }
    }

    private void checkApk() {
        AppDebug.d(TAG, TAG + ".checkApk check apk when forced update download finish");
        mCheckApkTask = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return CheckAPK.checkAPKFile(mContext, mTargetFile, mVersionCode);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                AppDebug.v(TAG, TAG + ".checkApk.onPostExecute.result = " + result);
                if (result) {
                    Message msg = mMyHandler.obtainMessage(UpdatePreference.NEW_APK_VALID);
                    mMyHandler.sendMessage(msg);
                } else {
                    Log.e(TAG, TAG + ".onPostExecute.apk checked fail");
                    File file = new File(mTargetFile);
                    if (file != null && file.exists())
                        file.delete();
                    Message msg = mMyHandler.obtainMessage(UpdatePreference.NEW_APK_INVALID);
                    mMyHandler.sendMessage(msg);
                }
            }
        };
        mCheckApkTask.execute();
    }

    // 非强制更新时启动更新界面，强制更新时通知界面下载完成
    private void startUpdateActivity() {
        AppDebug.v(TAG, TAG + ".startUpdateActivity.mIsStop = " + mIsStop + ", mIsForced = " + mIsForced);
        if (mIsStop)
            return;
//        if (!mIsForced) {
            // 发送广播
//            sendBroadcastToStartActivity(mBundle);
//            startisForcedUpdateDialog(mBundle);
//            sendTerminatedMessage();
//        } else {
            // 通知activity准备安装更新
//            onInstall();
            sendBroadcastToStartActivity(mBundle);
//        }
        releaseNetworkListner();
    }

    private void putBundle() {
        mBundle = new Bundle();
        mBundle.putString(UpdatePreference.INTENT_KEY_APP_CODE, mAppCode);
        mBundle.putBoolean(UpdatePreference.INTENT_KEY_FORCE_INSTALL, mIsForced);
        mBundle.putString(UpdatePreference.INTENT_KEY_UPDATE_INFO, mReleaseNote);
        mBundle.putString(UpdatePreference.INTENT_KEY_TARGET_FILE, mTargetFile);
        mBundle.putString(UpdatePreference.INTENT_KEY_TARGET_MD5, mTargetMd5);
        mBundle.putLong(UpdatePreference.INTENT_KEY_TARGET_SIZE, mTargetSize);
    }

    private void sendBroadcastToStartActivity(Bundle bundle) {
        AppDebug.d(TAG, TAG + ".sendBroadcastToStartActivity.send broadcast to start update activity, bundle: " + bundle
                + ".mIsStop = " + mIsStop);
        if (mIsStop) {
            return;
        }
        // 设置状态供注册广播时查询
        UpdateStatus.setUpdateStatus(UpdateStatus.START_ACTIVITY, bundle);
        // 发送广播
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.yunos.taobaotv.update.action.BROADCAST");
        broadcastIntent.putExtras(bundle);
        mContext.sendBroadcast(broadcastIntent);

        // 埋点
        UserTrackUtil.onCustomEvent(UpdatePreference.UT_SHOW_UPDATE_ACTIVITY);
    }

    /**
     * 非强制更新页面
     *
     *
     * @param bundle
     */
    private void startisForcedUpdateDialog(Bundle bundle) {
        SharedPreferences sp = mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
        long time = sp.getLong("update_dialog_show_time", 0);
        AppDebug.d(TAG, "time: " + DateUtils.isToday(time));
        if (DateUtils.isToday(time))
            return;

        Message msg = new Message();
        msg.setData(bundle);
        msg.what = UpdatePreference.SHOW_UPDATE_DIALOG;
        mMyHandler.sendMessageDelayed(msg, 10 * 1000);
    }

    /**
     * 显示顶部更新弹框
     *
     */
    private void showUpdateDialog(Bundle bundle,String str) {
        if (updateDialog == null)
            updateDialog = new UpdateDialog(mContext,str);
        if (!updateDialog.isShowing()) {
            updateDialog.setBundle(bundle);
            RtEnv.set(RtEnv.KEY_UPDATE_BUNDLE,bundle);
            updateDialog.show();
        }
    }

    private void sendTerminatedMessage() {
//         APPAppDebug.d(TAG, "send terminated message to update service");
//         if (mServiceHandler == null) return;
//         Message msg =
//         mServiceHandler.obtainMessage(UpdatePreference.TERMINATED, map);
//         mServiceHandler.sendMessage(msg);

        stop();
    }

    private void registerNetworkListner() {
        AppDebug.d(TAG, TAG + ".registerNetworkListner");
        NetworkManager.instance().init(mContext);
        NetworkManager.instance().registerStateChangedListener(mNetworkListner);
    }

    private void releaseNetworkListner() {
        AppDebug.d(TAG, TAG + ".releaseNetworkListner");
        NetworkManager.instance().unregisterStateChangedListener(mNetworkListner);
        try {
            NetworkManager.instance().release();
        } catch (Exception e) {
        }
    }

    /**
     * @param strings strs needed to be check if they were empty
     * @return true if one of strs is empty
     */
    private boolean checkEmpty(String... strings) {
        for (String str : strings) {
            if (TextUtils.isEmpty(str))
                return true;
        }
        return false;
    }



    /**
     * 网络状态监听
     */
    private NetworkManager.INetworkListener mNetworkListner = new NetworkManager.INetworkListener() {

        @Override
        public void onNetworkChanged(boolean isConnected, boolean lastIsConnected) {
            AppDebug.d(TAG, "onNetworkChanged, isConnected: " + isConnected + " isNeedRequest: " + mIsNeedRequest);
            if (isConnected && mIsNeedRequest) {
                mIsNeedRequest = false;
                onResumeDownload();
                retry();
            }
        }
    };

    /**
     * 更新过程监听
     * Class Descripton.
     *
     * @author mi.cao
     * @data 2015年6月19日 上午11:35:14
     */
    public interface DownloadProgressListner {

        // 重新下载
        void onRetryDownload(int progress);

        // 更新进度
        void onUpdateProgress(int progress);

        // 安装文件已存在
        void onFileExists();

        // 安装文件有效
        void onFildValid();

        // 开始安装
        void onInstall();

        // 安装错误
        void onError(int errorType);

        // 重新开始下载
        void onResumeDownload();

        // 安装类型
        void onChangeDownloadType(int type);
    }

}
