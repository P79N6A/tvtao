package com.yunos.tvtaobao.biz.dialog.util;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.dialog.TextProgressDialog;
import com.yunos.tvtaobao.biz.dialog.TvTaoBaoDialog;
import com.yunos.tvtaobao.biz.dialog.WaitProgressDialog;
import com.yunos.tvtaobao.businessview.R;

public class DialogUtil {

    private final String TAG = "DialogUtil";

    protected final Context mContext;

    // 网络提示对话框
    protected TvTaoBaoDialog mNetworkDialog;

    // 其他错误提示对话框
    protected TvTaoBaoDialog mNormalErrorDialog;

    // 进度条
    protected WaitProgressDialog mWaitProgressDialog;

    //文字进度条
    protected TextProgressDialog mTextProgressDialog;

    // 网络设置对话框按返回键是否关闭当前的Activity
    private boolean mCloseActivityOfNetworkDialog;

    private boolean mFinished;

    public DialogUtil(Context context) {
        mContext = context;
        mCloseActivityOfNetworkDialog = false;
    }

    public TvTaoBaoDialog getmNetworkDialog() {
        return mNetworkDialog;
    }

    public TvTaoBaoDialog getmNormalErrorDialog() {
        return mNormalErrorDialog;
    }

    public WaitProgressDialog getmWaitProgressDialog() {
        return mWaitProgressDialog;
    }

    public TextProgressDialog getTextProgressDialog() {
        return mTextProgressDialog;
    }

    /**
     * 关闭网络提示对话框
     */
    public void networkDialogDismiss() {
        if (mNetworkDialog != null) {
            mNetworkDialog.dismiss();
        }
    }

    /**
     * 关闭普通提示对话框
     */
    public void normalDialogDismiss() {
        if (mNormalErrorDialog != null) {
            mNormalErrorDialog.dismiss();
            mNormalErrorDialog = null;
        }
    }

    /**
     * 释放
     */
    public void onDestroy() {
        mFinished = true;
        // 将对话框消失并释放引用
        if (mNetworkDialog != null) {
            mNetworkDialog.dismiss();
            mNetworkDialog = null;
        }

        if (mNormalErrorDialog != null) {
            mNormalErrorDialog.dismiss();
            mNormalErrorDialog = null;
        }

        if (mWaitProgressDialog != null) {
            mWaitProgressDialog.dismiss();
            mWaitProgressDialog = null;
        }

        if (mTextProgressDialog != null) {
            mTextProgressDialog.dismiss();
            mTextProgressDialog = null;
        }
    }

    public boolean isFinished() {
        return mFinished;
    }


    /**
     * 创建进度条
     */
    private void creatWaitProgressDialog() {
        // 创建对话框
        mWaitProgressDialog = new WaitProgressDialog(mContext);
    }

    /**
     * 创建文字进度条
     */
    private void creatTextProgressDialog() {
        // 创建对话框
        mTextProgressDialog = new TextProgressDialog(mContext);
    }

    /**
     * 创建网络提示对话框, 并设置监听
     */
    private void onCreatSetNetworkDialog() {

        if (mNetworkDialog != null) {
            return;
        }

        // 创建网络对话框
        mNetworkDialog = new TvTaoBaoDialog.Builder(mContext)
                .setMessage(mContext.getString(R.string.ytbv_network_error_goto_set))
                .setPositiveButton(mContext.getString(R.string.ytbv_setting), new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppDebug.v(TAG, TAG + ".onCreatSetNetworkDialog.onClick.SYSTEM_YUNOS_4_0 = "
                                + SystemConfig.SYSTEM_YUNOS_4_0);
                        if (SystemConfig.SYSTEM_YUNOS_4_0) {// 打开yunos4.0的设置界面
                            BaseActivity baseActivity = (BaseActivity) mContext;
                            if (baseActivity != null) {
                                try {
                                    baseActivity.showYunosHostPage(null,
                                            "page://settingrelease.tv.yunos.com/settingrelease");
                                } catch (Exception e) {
                                    AppDebug.i(TAG, "onCreatSetNetworkDialog --> Exception --> e = " + e.toString());
                                    Utils.startNetWorkSettingActivity(mContext,
                                            mContext.getString(R.string.ytbv_open_setting_activity_error));
                                }
                            } else {
                                Log.e(TAG, TAG + ".onCreatSetNetworkDialog.onClick.baseActivity == null");
                            }
                        } else {
                            Utils.startNetWorkSettingActivity(mContext,
                                    mContext.getString(R.string.ytbv_open_setting_activity_error));
                        }
                    }
                }).create();

        // 设置网络对话框的按键监听
        mNetworkDialog.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    dialog.dismiss();
                    if (mCloseActivityOfNetworkDialog && mContext != null && mContext instanceof Activity) {
                        Activity activity = (Activity) mContext;
                        activity.finish();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 显示网络异常对话框
     */
    public void showNetworkErrorDialog(boolean isFinishActivity) {

        AppDebug.d(TAG, " showNetworkErrorDialog;   ; mNetworkDialog = " + mNetworkDialog);

        Activity mActivity = (Activity) mContext;
        if (mActivity.isFinishing()) {
            // 如果当前 mActivity已经finish，那么直接返回
            mActivity = null;
            return;
        }

        if (mNormalErrorDialog != null) {
            // 在弹出设置对话框之前，隐藏普通对话框
            mNormalErrorDialog.dismiss();
        }

        if (mNetworkDialog == null) {
            onCreatSetNetworkDialog();
        }

        mCloseActivityOfNetworkDialog = isFinishActivity;
        if (mNetworkDialog.isShowing()) {
            mNetworkDialog.dismiss();
        }

        mNetworkDialog.show();
    }

    /**
     * 一般的错误提示
     *
     * @param msg
     */
    public void showErrorDialog(String msg) {

        AppDebug.d(TAG, " showErrorDialog;  msg =  " + msg);

        showErrorDialog(msg, mContext.getString(R.string.ytbv_confirm), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNormalErrorDialog.dismiss();
            }
        }, null);
    }

    /**
     * 一般的错误提示
     *
     * @param msg
     */
    public void showErrorDialog(String msg, OnClickListener listener, final OnKeyListener onKeyListener) {
        showErrorDialog(msg, mContext.getString(R.string.ytbv_confirm), listener, onKeyListener);
    }

    /**
     * 一般的错误提示,基础方法
     *
     * @param msg
     */
    public void showErrorDialog(String msg, String firstButtonTitle, final OnClickListener firstButtonOnClickListener,
                                OnKeyListener onKeyListener) {

        AppDebug.d(TAG, "showErrorDialog;  msg =  " + msg + "; firstButtonTitle = " + firstButtonTitle);

        if (TextUtils.isEmpty(msg)) {
            return;
        }

        if (mNetworkDialog != null) {
            // 在普通对话框弹出之前，隐藏网络设置对话框
            mNetworkDialog.dismiss();
        }

        if (mNormalErrorDialog != null && mNormalErrorDialog.isShowing()) {
            mNormalErrorDialog.dismiss();
            mNormalErrorDialog = null;
        }

        Activity mActivity = (Activity) mContext;
        if (mActivity.isFinishing()) {
            mActivity = null;
            return;
        }

        mNormalErrorDialog = new TvTaoBaoDialog.Builder(mContext).setMessage(msg)
                .setPositiveButton(firstButtonTitle, firstButtonOnClickListener).create();

        if (onKeyListener == null) {
            onKeyListener = new OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                }
            };
        }

        mNormalErrorDialog.setOnKeyListener(onKeyListener);
        if (!mActivity.isFinishing()) {
            mNormalErrorDialog.show();
        }
    }

    /**
     * 控制loading显示隐藏
     *
     * @param show
     */
    public void OnWaitProgressDialog(boolean show) {

        try {
            AppDebug.d(TAG, "OnWaitProgressDialog;  show =  " + show + "; this = " + this);

            Activity mActivity = (Activity) mContext;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (mActivity.isFinishing() || mFinished || mActivity.isDestroyed()) {
                    // 如果当前 mActivity已经finish，那么直接返回
                    mActivity = null;
                    mFinished = false;
                    return;
                }
            } else {
                if (mActivity.isFinishing() || mFinished) {
                    // 如果当前 mActivity已经finish，那么直接返回
                    mActivity = null;
                    mFinished = false;
                    return;
                }
            }

            if (mWaitProgressDialog == null) {
                creatWaitProgressDialog();
            }

            if (show && mWaitProgressDialog.isShowing()) {
                return;
            }

            if (!show && !mWaitProgressDialog.isShowing()) {
                return;
            }

            if (show) {
                mWaitProgressDialog.show();
            } else {
                mWaitProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制Text loading显示隐藏
     *
     * @param show
     */
    public void onTextProgressDialog(CharSequence text, boolean show) {

        AppDebug.d(TAG, "onTextProgressDialog;  show =  " + show + "; this = " + this);

        Activity mActivity = (Activity) mContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (mActivity.isFinishing() || mFinished || mActivity.isDestroyed()) {
                // 如果当前 mActivity已经finish，那么直接返回
                mActivity = null;
                mFinished = false;
                return;
            }
        } else {
            if (mActivity.isFinishing() || mFinished) {
                // 如果当前 mActivity已经finish，那么直接返回
                mActivity = null;
                mFinished = false;
                return;
            }
        }

        if (mTextProgressDialog == null) {
            creatTextProgressDialog();
        }

        if (show && mTextProgressDialog.isShowing()) {
            mTextProgressDialog.setText(text);
            return;
        }

        if (!show && !mTextProgressDialog.isShowing()) {
            return;
        }

        if (show) {
            mTextProgressDialog.setText(text);
            mTextProgressDialog.show();
        } else {
            mTextProgressDialog.dismiss();
        }
    }


    /**
     * 设置进度条 按返回键 取消是否有效
     *
     * @param flag
     */
    public void setProgressCancelable(boolean flag) {

        AppDebug.d(TAG, "setProgressCancelable;  flag =  " + flag + "; this = " + this);

        if (mWaitProgressDialog != null) {
            mWaitProgressDialog.setCancelable(flag);
        }

        if (mTextProgressDialog != null) {
            mTextProgressDialog.setCancelable(flag);
        }
    }

}
