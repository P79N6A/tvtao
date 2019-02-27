package com.yunos.tvtaobao.biz.activity;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taobao.statistic.CT;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.UpdateStatus;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.util.MD5Util;
import com.yunos.tvtaobao.biz.util.UserTrackUtil;
import com.yunos.tvtaobao.biz.controller.Update;
import com.yunos.tvtaobao.biz.manager.ActivityQueueManager;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.businessview.R;

import java.io.File;
import java.util.Map;

/**
 * @author quanquan.rqq
 * 强制更新界面
 */
public class UpdateActivity extends CoreActivity {

    private static String TAG = "UpdateActivity";
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
    private String updateInfoText;
    private TextView update_title;
    private LinearLayout layout1, layout2, layout3;
    private TextView feature1, feature2, feature3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNotNeedRegisterUpdate(true);
        SharedPreferences sp = getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);

        updateInfoText = sp.getString(UpdatePreference.UPDATE_TIPS, getString(R.string.bs_before_up_default_memo));
        setContentView(R.layout.bs_up_update_mandatory_activity);
        layout1 = (LinearLayout) findViewById(R.id.bs_up_new_feature_content1);
        layout2 = (LinearLayout) findViewById(R.id.bs_up_new_feature_content2);
        layout3 = (LinearLayout) findViewById(R.id.bs_up_new_feature_content3);
        feature1 = (TextView) findViewById(R.id.bs_up_fature_text1);
        feature2 = (TextView) findViewById(R.id.bs_up_fature_text2);
        feature3 = (TextView) findViewById(R.id.bs_up_fature_text3);
        LinearLayout[] layouts = {layout1, layout2, layout3};
        TextView[] textViews = {feature1, feature2, feature3};
        if (!TextUtils.isEmpty(updateInfoText)) {
            String[] tmp = updateInfoText.split("\n");
            AppDebug.e("NewFeature", "NewFeature.updateInfoText : " + updateInfoText + " ,tmp.length : " + tmp.length);

            for (int i = 0; i < tmp.length; i++) {
                AppDebug.e("NewFeature", "NewFeature.tmp : " + tmp[i] + " ,textview : " + textViews[i]);
                layouts[i].setVisibility(View.VISIBLE);
                textViews[i].setText(tmp[i]);
            }
        }
        setCheckNetWork(true);
        initProperty();
        Utils.utCustomHit("Update_Forcedinstall_Expore", Utils.getProperties());
        Utils.utCustomHit("Expose_update", getPropertiesForceUpdate());

    }

    public Map<String, String> getPropertiesForceUpdate() {

        Map<String, String> p = Utils.getProperties();

        p.put("type", "force");

        if (!TextUtils.isEmpty(CloudUUIDWrapper.getCloudUUID())) {
            p.put("uuid", CloudUUIDWrapper.getCloudUUID());
        }

        try {
            if (!TextUtils.isEmpty(AppInfo.getPackageName()) && !TextUtils.isEmpty(AppInfo.getAppVersionName())) {
                p.put("from_app", AppInfo.getPackageName() + AppInfo.getAppVersionName());
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }

        if (CoreApplication.getLoginHelper(getApplicationContext()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }

        return p;

    }


    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        AppDebug.d(TAG, TAG + ".onDestroy");
        onRemoveKeepedActivity(UpdateActivity.class.getName());
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 强制升级时，按BACK键等同于按HOME键
        AppDebug.v(TAG, TAG + ".onKeyDown.event = " + event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 埋点
            UserTrackUtil.onCtrlClicked(CT.Button, UpdatePreference.UT_CANCEL);

            setCheckNetWork(false);

//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            startActivity(intent);


            //关闭更新提示框，并发送广播，该广播在MainBaseACtivity中
            finishAndStop();

            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            // 埋点
//            UserTrackUtil.onCtrlClicked(CT.Button, UpdatePreference.UT_CLICK_UPDATE);

            Utils.utControlHit(getFullPageName(), "Update_Forcedinstall_update", Utils.getProperties());
            install();
        }

        return super.onKeyDown(keyCode, event);
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
        Update up = Update.get(mAppCode);
        if (up != null) {
            up.stop();
        }
        exitChildProcess();
        clearAllOpenedActivity(this);
        finish();
        CoreApplication.getApplication().clear();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected String getAppTag() {
        return TAG;
    }

    @Override
    protected String getAppName() {
        return "tvtaobao.update";
    }

    @Override
    protected void onStartActivityNetWorkError() {
    }

    @Override
    public String getPageName() {
        return "Update_Forcedinstall_Expore";
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

    private void exitChildProcess() {
        ActivityManager activityMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityMgr.getRunningAppProcesses()) {
            if (appProcess.processName.compareTo("com.yunos.tvtaobao:bs_webbroser") == 0) {
                AppDebug.i(TAG, "kill processName=" + appProcess.processName);
                android.os.Process.killProcess(appProcess.pid);
            }
        }
    }
}
