package com.yunos.tvtaobao.biz.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.UpdateStatus;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.util.MD5Util;
import com.yunos.tvtaobao.biz.controller.Update;
import com.yunos.tvtaobao.biz.manager.ActivityQueueManager;
import com.yunos.tvtaobao.biz.model.AppInfo;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.biz.util.UserTrackUtil;
import com.yunos.tvtaobao.businessview.R;
import java.io.File;
import java.util.Map;

/**
 * Created by pan on 2017/6/5.
 */

public class UpdateNowActivity extends CoreActivity implements View.OnClickListener {
    private final String TAG = "UpdateNowActivity";
    private TextView update_text;
    private LinearLayout update_btn_layout;
    private Button update_btn_leave, update_btn_now;
    private ProgressBar mDownloadProgressBar;
    private AppInfo appInfo;
    // 需要安装的apk文件
    private String mTargetFile;
    // 需安装文件的md5
    private String mTargetMd5;
    // 内部调用tvtaobao，外部调用tvtaobao_external
    private String mAppCode;
    // 安装文件的大小
    private long mTargetSize;
    // 是否是强制安装
    private boolean mIsForcedInstall;

    private Update up;

    private final int MSG_PROGRESS_UPDATE = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int progress = mDownloadProgressBar.getProgress();
            mDownloadProgressBar.setProgress(++progress);
            mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 20);
            AppDebug.w(TAG, TAG + ".mHandler progress : " + progress);
            if (progress >= 100) {
                install();
                mHandler.removeMessages(MSG_PROGRESS_UPDATE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bs_up_update_now_activity);
        initProperty();
        initView();

        up = Update.get(mAppCode);
        if (up == null)
            up = Update.get(UpdatePreference.TVTAOBAO_EXTERNAL);
    }

    /**
     * 初始化参数属性
     */
    private void initProperty() {
        Intent intent = getIntent();
        mAppCode = intent.getStringExtra(UpdatePreference.INTENT_KEY_APP_CODE);
        mTargetFile = intent.getStringExtra(UpdatePreference.INTENT_KEY_TARGET_FILE);
        mTargetMd5 = intent.getStringExtra(UpdatePreference.INTENT_KEY_TARGET_MD5);
        mTargetSize = intent.getLongExtra(UpdatePreference.INTENT_KEY_TARGET_SIZE, 0);
        mIsForcedInstall = intent.getBooleanExtra(UpdatePreference.INTENT_KEY_FORCE_INSTALL, false);
    }

    private void initView() {
        update_text = (TextView) findViewById(R.id.bs_up_update_text);
        update_btn_layout = (LinearLayout) findViewById(R.id.bs_up_update_btn_layout);
        update_btn_leave = (Button) findViewById(R.id.bs_up_update_btn_leave);
        update_btn_now = (Button) findViewById(R.id.bs_up_update_btn_now);
        mDownloadProgressBar = (ProgressBar) findViewById(R.id.bs_up_update_progress);

        update_btn_now.requestFocus();

        update_btn_leave.setOnClickListener(this);
        update_btn_now.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        if (up != null) {
            up.setIsStartActivity(true);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }

    @Override
    public void finish() {
        if (up != null) {
            up.setIsStartActivity(false);
        }
        super.finish();
    }

    @Override
    protected void onStartActivityNetWorkError() {

    }

    @Override
    protected String getAppTag() {
        return "UpdateNowActivity";
    }

    @Override
    protected String getAppName() {
        return "tvtaobao.update.now";
    }

    @Override
    public void onClick(View v) {
        Map<String, String> properties = Utils.getProperties();
        int i = v.getId();
        if (i == R.id.bs_up_update_btn_leave) {
            properties.put("controlName", "cancel");
            Utils.utControlHit(getFullPageName(), "cancel", properties);
            finish();
        } else if (i == R.id.bs_up_update_btn_now) {
            // 强制更新时，启动下载线程
            AppDebug.d(TAG, TAG + ".mInstallButton.onClick.onInstallClick, up: " + up + ", mAppCode = " + mAppCode);
            if (up != null) {
                showProgressLayout();
                up.setOnDownloadProgressListner(mDownloadProgressListner);
                up.start();
            } else {
                showErrorExitLayout();
                AppDebug.d(TAG, TAG + ".mInstallButton.onClick.showErrorExitLayout");
            }

            properties.put("controlName", "update");
            Utils.utControlHit(getFullPageName(), "update", properties);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishAndStop();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 下载更新过程监听事件
     */
    private Update.DownloadProgressListner mDownloadProgressListner = new Update.DownloadProgressListner() {

        @Override
        public void onUpdateProgress(final int progress) {
            AppDebug.d(TAG, TAG + ".onUpdateProgress progress = " + progress);
            runOnUiThread(new Runnable() {

                public void run() {
                    mDownloadProgressBar.setProgress(progress);
                    if (progress >= 100)
                        install();
                }
            });
        }

        @Override
        public void onFileExists() {
            AppDebug.d(TAG, TAG + ".onFileExists");
            mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
        }

        @Override
        public void onFildValid() {

        }

        @Override
        public void onInstall() {
            AppDebug.d(TAG, TAG + ".onInstall");
            install();
        }

        @Override
        public void onError(int errorType) {
            AppDebug.e(TAG, TAG + ".onError error type: " + errorType);
            onProcessError(errorType);
        }

        @Override
        public void onResumeDownload() {
            AppDebug.e(TAG, TAG + ".onResumeDownload");
//            hideNetworkErrorLayout();
        }

        @Override
        public void onRetryDownload(final int progress) {
            AppDebug.v(TAG, TAG + ".onRetryDownload, progress: " + progress);
            mDownloadProgressBar.setProgress(progress);
        }

        @Override
        public void onChangeDownloadType(int type) {
            AppDebug.e(TAG, TAG + ".onChangeDownloadType type : " + type);
//            onProcessTypeChanged(type);
        }
    };

    private void onProcessError(int errorType) {
        switch (errorType) {
            case UpdatePreference.ERROR_TYPE_NETWORK_DISCONNECT:
            case UpdatePreference.ERROR_TYPE_NETWORK_INAVAILABLE:
//                showNetworkErrorLayout();
                showErrorExitLayout();
                break;
            case UpdatePreference.ERROR_TYPE_FILE_EXCEPTION:
            case UpdatePreference.ERROR_TYPE_INVALID_UPDATE_FILE:
                showErrorExitLayout();
                break;
        }
    }

    private void showErrorExitLayout() {
        update_btn_layout.setVisibility(View.VISIBLE);
        update_btn_now.setText("再试一次");
        mDownloadProgressBar.setProgress(0);
        mDownloadProgressBar.setVisibility(View.GONE);
        update_text.setText("更新失败，可能因为您的网络状况不佳");
    }

    private void showNetworkErrorLayout() {

    }

    private void showProgressLayout() {
        update_btn_layout.setVisibility(View.GONE);
        mDownloadProgressBar.setVisibility(View.VISIBLE);
        update_text.setText("下载中，请勿返回或关闭电源");
    }

    /**
     * 调用PackageInstaller安装更新
     */
    private void install() {
        try {
            File newAPK = new File(mTargetFile);
            newAPK.setReadable(true, false);
            // 校验文件
            if (newAPK.length() != mTargetSize || !mTargetMd5.equalsIgnoreCase(MD5Util.getMD5(newAPK))) {
                UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_INVALID_UPDATE_FILE);
                AppDebug.e(TAG, TAG + ".install,invalid file, file size: " + newAPK.length() + " correct size: "
                        + mTargetSize + " file md5: " + MD5Util.getMD5(newAPK) + " correct MD5: " + mTargetMd5);
                AppDebug.d(TAG, TAG + ".install,delete invalid file: " + newAPK.delete());
                finishAndStop();
            }
        } catch (Exception e) {
            UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
            AppDebug.e(TAG, "get md5 exception: " + e.getLocalizedMessage());
            setResult(RESULT_CANCELED);
            finishAndStop(); // 发生异常则关闭升级提示框
        }
        AppDebug.d(TAG, TAG + ".install, MD5 check success, start to install new apk");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + mTargetFile), "application/vnd.android.package-archive");
        try {
            startActivity(intent);
        } catch (Exception e) {
            UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
            AppDebug.e(TAG, TAG + ".install,PackageInstaller exception: " + e.getLocalizedMessage());
            setResult(RESULT_CANCELED);
            finishAndStop(); // 发生异常则关闭升级提示框
        }
    }

    /**
     * 停止更新并关闭界面
     */
    private void finishAndStop() {
        UpdateStatus.setUpdateStatus(UpdateStatus.UNKNOWN, null);
        AppDebug.d(TAG, TAG + ".finishAndStop up : " + up);
        if (up != null) {
            up.stop();
        }
        finish();
    }

    /**
     * 保持activity只有一个
     */
    protected void onKeepActivityOnlyOne(String tag) {
        ActivityQueueManager manager = ActivityQueueManager.getInstance();
        manager.onDestroyActivityOfList(tag);
        manager.onAddDestroyActivityToList(tag, this);
    }

    /**
     * 移除保持的activity
     */
    protected void onRemoveKeepedActivity(String tag) {
        ActivityQueueManager manager = ActivityQueueManager.getInstance();
        manager.onRemoveDestroyActivityFromList(tag, this);
    }

    @Override
    public String getPageName() {
        return "Page_Voice_error_Lowversion";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> properties = Utils.getProperties();
        properties.put("appkey", Config.getAppKey());
        return properties;
    }
}
