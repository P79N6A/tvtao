/**
 * $
 * PROJECT NAME: TvShopping
 * PACKAGE NAME: com.yunos.tvshopping.dialog
 * FILE NAME: BaseDialog.java
 * CREATED TIME: 2015年6月25日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tvtaobao.tvshoppingbundle.dialog;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.dialog.TvTaoBaoDialog;
import com.yunos.tvtaobao.tvshoppingbundle.bean.TbTvShoppingReceiverData;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2015年6月25日 下午3:02:36
 */
public class TvShopBaseDialog extends BaseDialog {

    // 对话框的window
    protected Window mWindow;
    // 网络错误对话框
    protected TvTaoBaoDialog mNetworkDialog;
    // 网络设置对话框按返回键是否关闭当前的界面
    protected boolean mCloseDialogOfNetworkDialog;
    // 是否立即消失
    protected boolean mDismissImmediately;
    // tvshop管理器
    protected TbTvShoppingManager mTbTvShoppingManager;
    private CloseSystemDialogReceiver mCloseSystemDialogReceiver;// dialog关闭监听

    public TvShopBaseDialog(Context context, int theme) {
        super(context, theme);

        mWindow = getWindow();
        mWindow.setType(WindowManager.LayoutParams.TYPE_PHONE);// 系统对话框
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);// 增加硬件加速
        mNetworkDialog = null;
        mCloseDialogOfNetworkDialog = false;
        mDismissImmediately = false;
        mTbTvShoppingManager = TbTvShoppingManager.getIntance();

        setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                if (mTbTvShoppingManager != null) {
                    AppDebug.i(TAG, TAG + ".onShow.dialog = " + getClass());
                    mTbTvShoppingManager.setActivityLaunchState(false);
                }
            }
        });
    }

    /**
     * 启动账号登录界面
     */
    public void startYunosAccountActivity(Context context, boolean ifChangeAccount) {
        //如果是更换账号,不设置回跳地址,停留在账号页面
        setCurrLoginInvalid();
        setLoginActivityStartShowing();
        CoreApplication.getLoginHelper(context).startYunosAccountActivity(context, ifChangeAccount);
    }

    /**
     * 打开网络设置的页面
     *
     * @param context         use activity context
     * @param openErrorString 打开失败后给出的提示
     */
    public void startNetWorkSettingActivity(Context context, String openErrorString) {
        try {
            Intent intent = null;
            // 判断手机系统的版本 即API大于10 就是3.0或以上版本
            if (android.os.Build.VERSION.SDK_INT > 10) {
                intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
            } else {
                intent = new Intent();
                ComponentName component = new ComponentName("com.android.settings", "com.android.settings.network");
                intent.setComponent(component);
                intent.setAction("android.intent.action.VIEW");
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            AppDebug.e("NetWork", "openg network setting activity error");
            String error = "open setting error";
            if (!TextUtils.isEmpty(openErrorString)) {
                error = openErrorString;
            }
            Toast.makeText(CoreApplication.getApplication(), error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置对话框是否立即消失
     *
     * @param dismissImmediately
     */
    public void setDismissImmediately(boolean dismissImmediately) {
        mDismissImmediately = dismissImmediately;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mWindow != null) {
            IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            mCloseSystemDialogReceiver = new CloseSystemDialogReceiver(this);
            mWindow.getContext().registerReceiver(mCloseSystemDialogReceiver, filter);
        }
    }

    @Override
    public void onDetachedFromWindow() {

        if (mWindow != null && mCloseSystemDialogReceiver != null) {
            mWindow.getContext().unregisterReceiver(mCloseSystemDialogReceiver);
            mCloseSystemDialogReceiver = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    String getVideoNameFromTvShopAllCollectDialog() {
        if (mTbTvShoppingManager != null && mTbTvShoppingManager.getmCurrTvShopReceiverData() != null) {
            return mTbTvShoppingManager.getmCurrTvShopReceiverData().getVideoName();
        } else {
            return null;
        }
    }

    @Override
    public void dismiss() {

        if (mNetworkDialog != null) {
            if (mNetworkDialog.isShowing()) {
                mNetworkDialog.dismiss();
            }
        }

        super.dismiss();
    }

    @Override
    protected String getAppTag() {
        return "Ts";
    }

    @Override
    protected String getAppName() {
        return "tvshopping";
    }

    @Override
    protected Uri getUri() {
        return null;
    }

    @Override
    protected void onStartActivityNetWorkError(DialogInterface.OnClickListener onClickListener, DialogInterface.OnKeyListener onKeyListener,
                                               boolean closeIfFinish) {

        mNetworkDialog = new TvTaoBaoDialog.Builder(mContext)
                .setMessage(mContext.getString(com.yunos.tvtaobao.businessview.R.string.ytbv_network_error_goto_set))
                .setPositiveButton(mContext.getString(com.yunos.tvtaobao.businessview.R.string.ytbv_setting), onClickListener).create();
        mNetworkDialog.setOnKeyListener(onKeyListener);

        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        final CloseSystemDialogReceiver closeSystemDialogReceiver = new CloseSystemDialogReceiver(mNetworkDialog);
        mNetworkDialog.getWindow().getContext().registerReceiver(closeSystemDialogReceiver, filter);
        mNetworkDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                AppDebug.v(TAG, TAG + ".onStartActivityNetWorkError.onDismiss dialog = " + dialog
                        + ", mNetworkDialog = " + mNetworkDialog);
                if (mNetworkDialog != null && mNetworkDialog.getWindow() != null && closeSystemDialogReceiver != null) {
                    mNetworkDialog.getWindow().getContext().unregisterReceiver(closeSystemDialogReceiver);
                }
            }
        });

        mNetworkDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);// 系统对话框
        mNetworkDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);// 增加硬件加速
        if (mNetworkDialog.isShowing()) {
            mNetworkDialog.dismiss();
        }
        mCloseDialogOfNetworkDialog = closeIfFinish;

        mNetworkDialog.show();
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        return initProperties(p);
    }

    public Map<String, String> initProperties(Map<String, String> p) {
        TbTvShoppingManager mTbTvShoppingManager = TbTvShoppingManager.getIntance();
        if (null == mTbTvShoppingManager) {
            return p;
        }
        TbTvShoppingReceiverData videoData = mTbTvShoppingManager.getmCurrTvShopReceiverData();
        if (null != videoData) {
            if (null != videoData.getVideoId()) {
                p.put("video_id", videoData.getVideoId());
            }
            if (null != videoData.getVideoName()) {
                p.put("video_name", videoData.getVideoName());
            }
            if (null != videoData.getType()) {
                p.put("video_type", videoData.getType().getName());
            }
        }
        return p;
    }

    /**
     * 注册关闭系统级对话框的监听
     * Class Descripton.
     *
     * @author mi.cao
     * @data 2015年6月26日 上午10:57:37
     */
    private static class CloseSystemDialogReceiver extends BroadcastReceiver {

        private String TAG = "CloseSystemDialogReceiver";
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private WeakReference<Dialog> mDialog;

        public CloseSystemDialogReceiver(Dialog dialog) {
            mDialog = new WeakReference<Dialog>(dialog);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (!TextUtils.isEmpty(reason) && SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {//如果是home键
                    Dialog dialog = mDialog.get();
                    AppDebug.v(TAG, TAG + ".onReceive.dialog = " + dialog);
                    if (dialog != null) {
                        if (dialog instanceof TvShopBaseDialog) {
                            TvShopBaseDialog baseDialog = (TvShopBaseDialog) dialog;
                            AppDebug.v(TAG, TAG + ".onReceive.baseDialog = " + baseDialog);
                            if (baseDialog != null) {
                                baseDialog.mDismissImmediately = true;
                            }
                        }

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
            }
        }
    }
}
