package com.yunos.tvtaobao.tvshoppingbundle.dialog;


import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.listener.NetworkOkDoListener;
import com.yunos.tvtaobao.blitz.account.AccountActivityHelper;
import com.yunos.tvtaobao.blitz.account.AccountActivityHelper.OnAccountStateChangedListener;

import java.util.Map;
import java.util.regex.Pattern;

abstract public class BaseDialog extends Dialog {

    protected String TAG = "BaseDialog";
    protected final String Page = getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    private final String TAGS = "BaseDialog[" + Page + "]";

    private final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    protected Context mContext;

    // 账号处理
    protected AccountActivityHelper mAccountActivityHelper;

    // 外部来源
    private String mFrom;

    // 内部来源
    private String mHuoDong;

    // 来源应用
    private String mApp;

    //页面名称
    protected String mPageName;

    // 网络广播接收器
    private NetBroadcastReceiver mNetworkChangeBroadcastReceiver;

    // 重连后的监听
    private NetworkOkDoListener mNetworkOkDoListener;

    // 账号变化监听
    private AccountActivityHelper.OnAccountStateChangedListener mOnAccountStateChangedListener;

    // 是否网络检查
    private boolean mNetWorkCheck = false;// 在边看边买中不做网络判断

    public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initBaseDialog(context);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        initBaseDialog(context);
    }

    public BaseDialog(Context context) {
        super(context);
        initBaseDialog(context);
    }

    private void initBaseDialog(Context context) {
        mContext = context;
        initFromActApp();
        // 创建账号帮助类
        mAccountActivityHelper = new AccountActivityHelper();
        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()) != null)
            CoreApplication.getLoginHelper(CoreApplication.getApplication()).registerLoginListener(CoreApplication.getApplication());
    }

    @Override
    public void show() {
        // 注册网络广播接收器
        if (mNetworkChangeBroadcastReceiver == null) {
            mNetworkChangeBroadcastReceiver = new NetBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(CONNECTIVITY_CHANGE_ACTION);
            filter.setPriority(1000);
            mContext.registerReceiver(mNetworkChangeBroadcastReceiver, filter);
        }

        if (isTbs()) {
            mPageName = getFullPageName();
            //如果这应页面需要做代码统计，就不能不设置名称
            if (TextUtils.isEmpty(mPageName)) {
                throw new IllegalArgumentException("The PageName was null and TBS is open");
            }
            //TODO
            //20180503 ,页面曝光点去掉videoName
//            String videoName=getVideoNameFromTvShopAllCollectDialog();
//            mPageName+=("_video_name_"+videoName);
            Utils.utPageAppear(mPageName, mPageName);
        }

        super.show();
    }


    abstract String getVideoNameFromTvShopAllCollectDialog();


    @Override
    public void dismiss() {

        unRegisterLoginListener();

        // 取消注册的广播接收器，并释放引用
        if (mNetworkChangeBroadcastReceiver != null) {
            mContext.unregisterReceiver(mNetworkChangeBroadcastReceiver);
            mNetworkChangeBroadcastReceiver = null;
        }

        if (!TextUtils.isEmpty(mPageName) && isTbs()) {
            Map<String, String> p = getPageProperties();
            AppDebug.i(TAGS, TAGS + ".dismiss TBS=updatePageProperties(" + p + ")");
            Utils.utUpdatePageProperties(mPageName, p);
            Utils.utPageDisAppear(mPageName);
        }

        super.dismiss();
    }

    /**
     * 是否需要TBS（usertrack）统计
     *
     * @return
     */
    protected boolean isTbs() {
        return true;
    }

    /**
     * 启动Activity
     *
     * @param intent
     */
    public void startActivity(Intent intent) {
        AppDebug.d(TAGS, "TAG  ---- >  startActivity;  this =  " + this);
        if (intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (!TextUtils.isEmpty(mFrom)) {
            bundle.putString(CoreIntentKey.URI_FROM_BUNDLE, mFrom);
        }
        if (!TextUtils.isEmpty(mHuoDong)) {
            bundle.putString(CoreIntentKey.URI_HUODONG_BUNDLE, mHuoDong);
        }
        if (TextUtils.isEmpty(getAppName())) {
            throw new IllegalArgumentException("The activity.getAppName() was empty");
        }
        if (!TextUtils.isEmpty(mApp)) {
            bundle.putString(CoreIntentKey.URI_FROM_APP_BUNDLE, mApp);
        } else {
            bundle.putString(CoreIntentKey.URI_FROM_APP_BUNDLE, getAppName());
        }

        intent.putExtras(bundle);

        AppDebug.v(TAGS,
                "TAG  ---- >  startActivityForResult;  intent =  " + intent + ", getExtras=" + intent.getExtras());

        if (mNetWorkCheck) {
            //是否是跳转到网络设置界面
            boolean isGotoNetworkSetting = false;
            if (android.provider.Settings.ACTION_WIFI_SETTINGS.equals(intent.getAction())) {
                isGotoNetworkSetting = true;
            }
            ComponentName component = intent.getComponent();
            if (component != null && "com.android.settings".equals(component.getPackageName())
                    && "com.android.settings.network".equals(component.getClassName())) {
                isGotoNetworkSetting = true;
            }
            //网络未通弹出提示框并返回
            if (!NetWorkUtil.isNetWorkAvailable() && !isGotoNetworkSetting) {
                //                onStartActivityNetWorkError(false);
                //                return;
            }
        }
        setInnerIntent(intent);
        // 锁住启动页面的错误
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置内部的启动页面的关键参数
     * 内部之间的页面调用添加内部参数，这个会在调用完成后删除掉
     *
     * @param intent
     */
    public void setInnerIntent(Intent intent) {
        if (intent != null) {
            intent.putExtra(CoreActivity.INTENT_KEY_INNER, true);
        }
    }

    /**
     * 初始化统计需要的3个参数，from，from_act, from_app
     */
    protected void initFromActApp() {
        mFrom = getStringFromUri(CoreIntentKey.URI_FROM, "");
        if (!TextUtils.isEmpty(mFrom)) {
            mFrom = mFrom.replaceAll("[\\r\\n\\t\\s\\|\\\\\\/]+", "");
        }

        mHuoDong = getStringFromUri(CoreIntentKey.URI_HUODONG, null);
        if (!TextUtils.isEmpty(mHuoDong)) {
            mHuoDong = mHuoDong.replaceAll("[\\r\\n\\t\\s\\|\\\\\\/]+", "");
        }

        mApp = getStringFromUri(CoreIntentKey.URI_FROM_APP, null);
        if (!TextUtils.isEmpty(mApp)) {
            mApp = mApp.replaceAll("[\\r\\n\\t\\s\\|\\\\\\/]+", "");
        }

        if (null != mApp && mApp.equals(getAppName())) {
            mApp = null;
        }
        AppDebug.i(TAGS, TAGS + ".initFromActApp mFrom=" + mFrom + ", mHuoDong=" + mHuoDong + ", mApp=" + mApp);
    }

    /**
     * 登录界面是否是被取消
     *
     * @return
     */
    protected boolean loginIsCanceled() {
        if (mAccountActivityHelper != null) {
            return mAccountActivityHelper.loginIsCanceled();
        }
        return false;
    }

    /**
     * 设置登录界面正在显示
     */
    public void setLoginActivityStartShowing() {
        if (mAccountActivityHelper != null) {
            mAccountActivityHelper.setAccountActivityStartShowing();
        }
    }

    /**
     * 设置当前的登录状态是否为非法
     */
    public void setCurrLoginInvalid() {
        if (mAccountActivityHelper != null) {
            mAccountActivityHelper.setAccountInvalid();
        }
    }

    /**
     * 注册登录的状态监听
     */
    protected void registerLoginListener() {
        if (mOnAccountStateChangedListener == null) {
            mOnAccountStateChangedListener = new OnAccountStateChangedListener() {

                @Override
                public void onAccountStateChanged(AccountActivityHelper.AccountLoginState state) {
                    switch (state) {
                        case LOGIN:
                            onLogin();
                            break;
                        case LOGOUT:
                            onLogout();
                            break;
                        case CANCEL:
                            onLoginCancel();
                            break;
                        default:
                            break;
                    }
                }
            };
            if (mAccountActivityHelper != null) {
                mAccountActivityHelper.registerAccountActivity(mOnAccountStateChangedListener);
            }
        }
    }

    /**
     * 删除注册登录的状态监听
     */
    protected void unRegisterLoginListener() {
        if (mOnAccountStateChangedListener != null) {
            mAccountActivityHelper.unRegisterAccountActivity(mOnAccountStateChangedListener);
        }
    }

    /**
     * 登录
     */
    protected void onLogin() {
        refreshData();
    }

    /**
     * 登出
     */
    protected void onLogout() {
    }

    /**
     * 删除网络重连监听
     */
    public void removeNetworkOkDoListener() {
        mNetworkOkDoListener = null;
    }

    /**
     * 设置网络重连监听
     *
     * @param mNetworkOkDoListener
     */
    public void setNetworkOkDoListener(NetworkOkDoListener mNetworkOkDoListener) {
        this.mNetworkOkDoListener = mNetworkOkDoListener;
    }

    /**
     * 登录取消
     */
    protected void onLoginCancel() {
        AppDebug.i(TAG, "onLoginCancel");
    }

    /**
     * 刷新界面,用于子页面重写,登录成功时可能会调用
     */
    protected void refreshData() {
    }

    /**
     * 设置外部来源
     *
     * @param from
     */
    public void setFrom(String from) {
        mFrom = from;
    }

    /**
     * 设置内部来源
     *
     * @param huodong
     */
    public void setHuodong(String huodong) {
        mHuoDong = huodong;
    }

    /**
     * 设置来源应用
     *
     * @param app
     */
    public void setFromApp(String app) {
        mApp = app;
    }

    /**
     * 页面名称
     *
     * @return
     */
    public String getFullPageName() {
        if (TextUtils.isEmpty(mPageName)) {
            mPageName = getAppTag() + getPageName();
        }
        return mPageName;
    }

    /**
     * 返回null表示不统计该Activity
     *
     * @return
     */
    protected String getPageName() {
        return Pattern.compile("(activity|view|null|page|layout|dialog)$", Pattern.CASE_INSENSITIVE)
                .matcher(getClass().getSimpleName()).replaceAll("");
    }

    /**
     * 定义应用关键字，和pageName合并拼成一个页面名称
     *
     * @return
     */
    abstract protected String getAppTag();

    /**
     * 定义应用名称，自动实现from_app的传值
     *
     * @return
     */
    abstract protected String getAppName();

    /**
     * 获取界面的URI
     *
     * @return
     */
    abstract protected Uri getUri();

    /**
     * 启动activity失败时调用
     */
    abstract protected void onStartActivityNetWorkError(OnClickListener onClickListener, OnKeyListener onKeyListener,
                                                        boolean closeIfFinish);

    /**
     * 网络状态变化
     *
     * @param available true 连接 or 断开
     */
    protected void changedNetworkStatus(boolean available) {
        AppDebug.i(TAG, "changedNetworkStatus available=" + available);
    }

    /**
     * 定义页面参数
     *
     * @return
     */
    public Map<String, String> getPageProperties() {
        return Utils.getProperties(mFrom, mHuoDong, mApp);
    }

    /**
     * @return the mFrom
     */
    public String getmFrom() {
        return mFrom;
    }

    /**
     * @return the mHuoDong
     */
    public String getmHuoDong() {
        return mHuoDong;
    }

    /**
     * @return the mApp
     */
    public String getmApp() {
        return mApp;
    }

    /**
     * 从URI中提取数据值
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public String getStringFromUri(String name, String defaultValue) {
        String value = defaultValue;
        Uri uri = getUri();
        if (null != uri) {
            try {
                value = uri.getQueryParameter(name);
                if (TextUtils.isEmpty(value)) {
                    value = defaultValue;
                }
            } catch (Exception e) {
                value = defaultValue;
            }
        }
        return value;
    }

    // 网络接收器
    public class NetBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {
                // 网络可用时关闭提示框
                if (NetWorkUtil.isNetWorkAvailable()) {
                    if (mNetworkOkDoListener != null) {
                        mNetworkOkDoListener.todo();
                    }
                }
                changedNetworkStatus(NetWorkUtil.isNetWorkAvailable());
            }
        }
    }

    ;
}
