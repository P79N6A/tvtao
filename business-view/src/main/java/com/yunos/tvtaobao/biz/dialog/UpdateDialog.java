package com.yunos.tvtaobao.biz.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.UpdateStatus;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.util.MD5Util;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.businessview.R;
import com.yunos.tvtaobao.biz.controller.Update;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.biz.util.UserTrackUtil;

import java.io.File;
import java.util.Calendar;

/**
 * Created by pan on 2017/6/6.
 */

public class UpdateDialog extends Dialog {

    private final String TAG = "UpdateDialog";
    private final static String PAGENAME = "update_dialog";

    private Context mContext;
    private Button now_update_btn, leave_update_btn;
    private TextView update_title;
    private RelativeLayout update_btn_layout;
    private Animation animation;
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

    private String title;

    public UpdateDialog(Context context) {
        this(context, R.style.update_top_Dialog);
    }

    public UpdateDialog(Context context,String str) {
        this(context, R.style.update_top_Dialog);
        title=str;
    }

    public UpdateDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        WindowManager.LayoutParams l = getWindow().getAttributes();
        //0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
        l.dimAmount = 0.8f;
        l.gravity = Gravity.TOP;
        getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        getWindow().setWindowAnimations(R.style.bs_up_dialog_animation);
        getWindow().setAttributes(l);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bs_up_update_top_dialog);
        GlobalConfig globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
//        String title=globalConfig==null?"":(globalConfig.getUpgrade()==null?"":(StringUtil.isEmpty(globalConfig.getUpgrade().getText())?"":globalConfig.getUpgrade().getText()));
//        String title=globalConfig==null?"":(globalConfig.getUpgrade()==null?"":(StringUtil.isEmpty(globalConfig.getUpgrade().getContent())?"":globalConfig.getUpgrade().getContent()));

        update_title  =(TextView) findViewById(R.id.update_title);
        update_title.setText(StringUtil.isEmpty(title)?mContext.getString(R.string.bs_up_dilog_title):title);
        update_btn_layout = (RelativeLayout) findViewById(R.id.dialog_update_btn_layout);
        now_update_btn = (Button) findViewById(R.id.dialog_update_top_now);
        leave_update_btn = (Button) findViewById(R.id.dialog_update_top_later);

        animation = AnimationUtils.loadAnimation(mContext,
                R.anim.bs_up_dialog_btnlayout_enter);
        animation.setInterpolator(new DecelerateInterpolator());

        now_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.utControlHit(PAGENAME, "Update_passive_update", Utils.getProperties());
                install();
            }
        });
        leave_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.utControlHit(PAGENAME, "Update_passive_cancel", Utils.getProperties());
                finishAndStop();
            }
        });

        mHandler.sendEmptyMessageDelayed(0, 6 * 1000); //6秒后自动收回
    }

    @Override
    public void show() {
        super.show();
        now_update_btn.requestFocus();
        update_btn_layout.setAnimation(animation);
        Utils.utPageAppear(PAGENAME, PAGENAME);
        SharedPreferences sp = mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("update_dialog_show_time", Calendar.getInstance().getTime().getTime());
        editor.apply();
    }

    public void setBundle(Bundle bundle) {
        mAppCode = bundle.getString(UpdatePreference.INTENT_KEY_APP_CODE);
        mTargetFile = bundle.getString(UpdatePreference.INTENT_KEY_TARGET_FILE);
        mTargetMd5 = bundle.getString(UpdatePreference.INTENT_KEY_TARGET_MD5);
        mTargetSize = bundle.getLong(UpdatePreference.INTENT_KEY_TARGET_SIZE, 0);
        mIsForcedInstall = bundle.getBoolean(UpdatePreference.INTENT_KEY_FORCE_INSTALL, false);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mHandler != null && mHandler.hasMessages(0)) {
                mHandler.removeMessages(0);
                mHandler.sendEmptyMessageDelayed(0, 6000);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismiss();
        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
        Utils.utPageDisAppear(PAGENAME);
        mHandler.removeCallbacksAndMessages(null);
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
            finishAndStop(); // 发生异常则关闭升级提示框
        }
        AppDebug.d(TAG, TAG + ".install, MD5 check success, start to install new apk");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + mTargetFile), "application/vnd.android.package-archive");
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            UserTrackUtil.onErrorEvent(UpdatePreference.ERROR_TYPE_FILE_EXCEPTION);
            AppDebug.e(TAG, TAG + ".install,PackageInstaller exception: " + e.getLocalizedMessage());
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
        dismiss();
    }
}
