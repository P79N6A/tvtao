package com.yunos.tvtaobao.biz.activity;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.UpdateStatus;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.controller.Update;
import com.yunos.tvtaobao.biz.manager.ActivityQueueManager;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.biz.util.MD5Util;
import com.yunos.tvtaobao.biz.util.UserTrackUtil;
import com.yunos.tvtaobao.businessview.R;

import java.io.File;
import java.util.Map;

/**
 * @author lijinyun
 * 提示更新界面
 */
public class NotForceUpdateActivity extends CoreActivity {

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
    private ImageView left_image,right_image,main_image,bg_img;
    //image1.背景图；image2.圆点；image3.稍后再说聚焦；image4.稍后再说未聚焦；image5.马上升级聚焦；image6.马上升级未聚焦；
    private String image1,image2,image3,image4,image5,image6,color,laterOn,upgradeNow;
    private RelativeLayout main_layout;
    private ImageView view1,view2,view3;
    private TextView bs_up_fature_text1,bs_up_fature_text2,bs_up_fature_text3;
    private boolean forceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNotNeedRegisterUpdate(true);
        SharedPreferences sp = getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
        updateInfoText = sp.getString(UpdatePreference.NEW_RELEASE_NOTE, getString(R.string.bs_before_up_default_memo));
        image1= sp.getString(UpdatePreference.IMAGE1, "");
        image2= sp.getString(UpdatePreference.IMAGE2, "");
        image3= sp.getString(UpdatePreference.IMAGE3, "");
        image4= sp.getString(UpdatePreference.IMAGE4, "");
        image5= sp.getString(UpdatePreference.IMAGE5, "");
        image6= sp.getString(UpdatePreference.IMAGE6, "");
        color= sp.getString(UpdatePreference.COLOR, "");
        laterOn= sp.getString(UpdatePreference.LATER_ON, "");
        upgradeNow= sp.getString(UpdatePreference.UPGRADE_NOW, "");
        forceType= sp.getBoolean(UpdatePreference.INTENT_KEY_FORCE_INSTALL, false);

        setContentView(R.layout.bs_up_not_force_update_mandatory_activity);

        layout1 = (LinearLayout) findViewById(R.id.bs_up_new_feature_content1);
        layout2 = (LinearLayout) findViewById(R.id.bs_up_new_feature_content2);
        layout3 = (LinearLayout) findViewById(R.id.bs_up_new_feature_content3);
        view1= (ImageView) findViewById(R.id.view1);
        view2= (ImageView) findViewById(R.id.view2);
        view3= (ImageView) findViewById(R.id.view3);
        bs_up_fature_text1= (TextView) findViewById(R.id.bs_up_fature_text1);
        bs_up_fature_text2= (TextView) findViewById(R.id.bs_up_fature_text2);
        bs_up_fature_text3= (TextView) findViewById(R.id.bs_up_fature_text3);
        feature1 = (TextView) findViewById(R.id.bs_up_fature_text1);
        feature2 = (TextView) findViewById(R.id.bs_up_fature_text2);
        feature3 = (TextView) findViewById(R.id.bs_up_fature_text3);
        left_image= (ImageView) findViewById(R.id.left_image);
        right_image= (ImageView) findViewById(R.id.right_image);
        main_layout= (RelativeLayout) findViewById(R.id.main_layout);
        main_image= (ImageView) findViewById(R.id.main_image);
        bg_img= (ImageView) findViewById(R.id.bg_img);

        if(upgradeNow.equals("image5")){
            right_image.requestFocus();
        }else {
            left_image.requestFocus();
        }

        ImageLoaderManager.getImageLoaderManager(NotForceUpdateActivity.this).displayImage(image1, main_image);
        ImageLoaderManager.getImageLoaderManager(NotForceUpdateActivity.this).displayImage(laterOn.equals("image4")?image4:image3, left_image);
        ImageLoaderManager.getImageLoaderManager(NotForceUpdateActivity.this).displayImage(upgradeNow.equals("image5")?image5:image6, right_image);
        ImageLoaderManager.getImageLoaderManager(NotForceUpdateActivity.this).displayImage(image2, view1);
        ImageLoaderManager.getImageLoaderManager(NotForceUpdateActivity.this).displayImage(image2, view2);
        ImageLoaderManager.getImageLoaderManager(NotForceUpdateActivity.this).displayImage(image2, view3);

        bs_up_fature_text1.setTextColor(color.charAt(0) == '#'?Color.parseColor(color):Color.parseColor('#'+color));
        bs_up_fature_text2.setTextColor(color.charAt(0) == '#'?Color.parseColor(color):Color.parseColor('#'+color));
        bs_up_fature_text3.setTextColor(color.charAt(0) == '#'?Color.parseColor(color):Color.parseColor('#'+color));


        left_image.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    ImageLoaderManager.getImageLoaderManager(NotForceUpdateActivity.this).displayImage(image3, left_image);
                }else {
                    ImageLoaderManager.getImageLoaderManager(NotForceUpdateActivity.this).displayImage(image4, left_image);
                }
            }
        });
        right_image.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    ImageLoaderManager.getImageLoaderManager(NotForceUpdateActivity.this).displayImage(image5, right_image);
                }else {
                    ImageLoaderManager.getImageLoaderManager(NotForceUpdateActivity.this).displayImage(image6, right_image);
                }
            }
        });

        left_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.utControlHit(getPageName(), "Expose_update_button_refuse", initTBSProperty());
                finish();
            }
        });

        right_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.utControlHit(getPageName(), "Expose_update_button_update", initTBSProperty());
                install();
            }
        });

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

        Utils.utCustomHit("Expose_update", initTBSProperty());
    }

    /**
     * 初始化埋点信息
     *
     * @return
     */
    public Map<String, String> initTBSProperty() {

        Map<String, String> p = Utils.getProperties();

        p.put("type", forceType?"force":"optional");
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
        if(bg_img!=null){
            bg_img.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        if(bg_img!=null){
            bg_img.setVisibility(View.VISIBLE);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        AppDebug.d(TAG, TAG + ".onDestroy");
        onRemoveKeepedActivity(NotForceUpdateActivity.class.getName());
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        AppDebug.v(TAG, TAG + ".onKeyDown.event = " + event);
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            // 埋点
//            UserTrackUtil.onCtrlClicked(CT.Button, UpdatePreference.UT_CANCEL);
//            setCheckNetWork(false);
//            //关闭更新提示框，并发送广播，该广播在MainBaseACtivity中
//            finishAndStop();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

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
        return "Update_NotForcedinstall_Expore";
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
