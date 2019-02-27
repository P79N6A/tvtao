/**
 * $
 * PROJECT NAME: core
 * PACKAGE NAME: com.yunos.tv.core.activity
 * FILE NAME: CoreActivity.java
 * CREATED TIME: 2014-10-28
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tvtaobao.biz.activity;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;

import com.ut.mini.internal.UTTeamWork;

import com.alibaba.fastjson.JSON;
import com.alibaba.mtl.appmonitor.AppMonitor;
import com.alibaba.mtl.appmonitor.model.DimensionValueSet;
import com.alibaba.mtl.appmonitor.model.MeasureValueSet;
import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.ut.mini.UTAnalytics;
import com.yunos.CloudUUIDWrapper;
import com.yunos.RunMode;
import com.yunos.ott.sdk.core.Environment;
import com.yunos.tv.blitz.activity.BzBaseActivity;
import com.yunos.tv.blitz.view.BlitzBridgeSurfaceView;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.RtEnv;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.ActivityDataUtil;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.AppUpdateBroadcast;
import com.yunos.tv.core.util.AppUpdateBroadcast.OnShowAppUpdateListener;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.MonitorUtil;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.dialog.NewFeatureDialog;
import com.yunos.tvtaobao.biz.focus_impl.FocusContext;
import com.yunos.tvtaobao.biz.focus_impl.FocusManager;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.updatesdk.UpdateClient;
import com.yunos.tvtaobao.biz.updatesdk.UpdateFromDX;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.businessview.BuildConfig;
import com.yunos.tvtaobao.payment.config.DebugConfig;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Activity基础类
 *
 * @author hanqi
 * @data 2014-10-28 下午5:22:53
 */

abstract public class CoreActivity extends BzBaseActivity implements ActivityPathRecorder.PathNode, FocusContext {
    private static long resumeCount = 0l;
    /* 内部 是否是内部启动activity */
    public static final String INTENT_KEY_INNER = "frominner";
    /* 判断内部URI调用是否支持Intent flags的往下继承 */
    public static final String INTENT_KEY_INHERIT_FLAGS = "inheritflags";
    public static final String INTENT_KEY_IS_FROM_OUTSIDE = "isFromOutside";

    protected static String TAG = "Core";
    protected final String Page = getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());
    private final String TAGS = "CoreActivity[" + Page + "]";
    // 外部来源
    private String mFrom;
    // 内部来源
    private String mHuoDong;
    // 来源应用
    private String mApp;
    //外部广告承接页
//    private String mJoin;
    //页面名称
    protected String mPageName;
    private boolean mNetWorkCheck = true;

    private static SparseArray<Activity> mOpenedActivity = new SparseArray<Activity>();

    private AppUpdateBroadcast mAppUpdateBroadcast; // 应用更新的广播
    private boolean mNotNeedRegisterUpdate; // 是否不需要注册广播，默认是需要注册广播

    protected static Handler exitHandler;

    /* 应用自更新 */
    private UpdateClient mUpdateClient;
    private NewFeatureDialog mNewFeatureDialog;


    private long createTime = 0l;
    private long resumeTime = 0l;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        createTime = System.currentTimeMillis();
        super.onCreate(savedInstanceState);
        MonitorUtil.init();
        if (RunMode.isYunos() == Environment.getInstance().isYunos()) {
            if (exitHandler != null)
                exitHandler.removeCallbacksAndMessages(null);
            else
                exitHandler = new Handler();
            AppDebug.i(TAGS, TAGS + ".onCreate ");


            initFromActApp();
            mOpenedActivity.put(this.hashCode(), this);
            mNewIntent = null;
            ActivityPathRecorder.getInstance().recordPathNode(this);
            mAppUpdateBroadcast = new AppUpdateBroadcast();
            if (Boolean.TRUE.equals(RtEnv.get(RtEnv.KEY_SHOULD_CHECK_UPDATE_ON_CREATE,true))) {
                getNewFeature();
                RtEnv.set(RtEnv.KEY_SHOULD_CHECK_UPDATE_ON_CREATE,false);
            }

            if (isHomeActivity()) {   //只有首页会展示新版本特性
                showfeaturesDialog();
            }
            //注册广播
            registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                    Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        }

    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isTbs()) {
            long time = System.currentTimeMillis();
            DimensionValueSet dimensionValueSet = MonitorUtil.createDimensionValueSet(this);
            dimensionValueSet.setValue("activityName", getClass().getName());
            MeasureValueSet measureValueSet = MeasureValueSet.create();
            measureValueSet.setValue("loadTime", time - createTime);
            AppMonitor.Stat.commit("tvtaobao", "activityLoad", dimensionValueSet, measureValueSet);
        }

    }

    /**
     * 监听是否点击了home键将客户端推到后台
     */
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    if (DebugConfig.whetherIsMonkey()) {
                        Intent intent0 = new Intent(Intent.ACTION_MAIN);
                        intent0.addCategory(Intent.CATEGORY_LAUNCHER);
                        ComponentName cn = new ComponentName(AppInfo.getPackageName(), "com.yunos.tvtaobao.splashscreen.StartActivity");
                        intent0.setComponent(cn);
                        startActivity(intent0);
                    }
                    //表示按了home键,程序到了后台
                    onPause();
                    // 如果是外部调用的话不做清理工作,但在低内存设备上也要清理
                    clearAllOpenedActivity(CoreActivity.this);
                    exitChildProcessOfCoreActivity();
                    Intent intent1 = new Intent();
                    intent1.setPackage(getPackageName());
                    intent1.setAction("tech.zhiping.audioAction");
                    stopService(intent1);
                    CoreApplication.getApplication().clear();
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                } else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)) {
                    //表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };
    private Intent mNewIntent;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mNewIntent = intent;
        ActivityPathRecorder.getInstance().recordPathNode(this);
    }


    int localResumeCount = 0;

    @Override
    protected void onResume() {
        resumeCount++;
        localResumeCount++;
        resumeTime = System.currentTimeMillis();
        AppDebug.i(TAGS, TAGS + ".onResume isTbs()=" + isTbs() + ", mNotNeedRegisterUpdate = " + mNotNeedRegisterUpdate);

        if (RunMode.isYunos() == Environment.getInstance().isYunos()) {
            startUpdate();
            enterUT();
        }
        super.onResume();

        SDKInitConfig.setCurrentPage(getFullPageName());
        if (resumeCount == 1) {
            AppDebug.i(TAGS, TAGS + ".onResume reportAppLoadTime " + (resumeTime - CoreApplication.getOnCreateTime()));
            // 说明已有页面显示出来，打个点记录一下启动时间
            MonitorUtil.reportAppLoadTime(this, resumeTime - CoreApplication.getOnCreateTime());
        }
    }

    public boolean isFirstResume() {
        return localResumeCount == 1;
    }

    protected void startUpdate() {
        if (!isUpdateBlackList()) {
            if (Boolean.TRUE.equals(RtEnv.get(RtEnv.KEY_SHOULD_START_UPDATE,true))) {
                RtEnv.set(RtEnv.KEY_SHOULD_START_UPDATE,false);
                if (!mNotNeedRegisterUpdate) {
                    mAppUpdateBroadcast.registerUpdateBroadcast(new CoreOnShowAppUpdateListener(this));
                }
                //电信自升级
                startDXUpdate();
                //电视淘宝自升级
                startUpdateApp();
            }
        }
    }


    /**
     * 进入UT 埋点
     */
    protected void enterUT() {
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("debug_api_url","https://service-usertrack.alibaba-inc.com/upload_records_from_client");
//        map.put("debug_key", "utupdate20190111");
//        map.put("debug_sampling_option", "true");
//        UTTeamWork.getInstance().turnOnRealTimeDebug(map);
        if (isTbs()) {
            mPageName = getFullPageName();
            //如果这应页面需要做代码统计，就不能不设置名称
            if (TextUtils.isEmpty(mPageName)) {
                throw new IllegalArgumentException("The PageName was null and TBS is open");
            }
            Utils.utPageAppear(mPageName, mPageName);
            AppDebug.i(TAGS, TAGS + ".enterUT end mPageName=" + mPageName);
        }
    }

    /**
     * 退出UT
     */
    protected void exitUT() {
        try {
            if (!isTbs())
                return;
            if (!TextUtils.isEmpty(mPageName)) {
                Map<String, String> p = getPageProperties();
                AppDebug.i(TAGS, TAGS + ".exitUI TBS=updatePageProperties(" + p + ")");
                Utils.utUpdatePageProperties(mPageName, p);
                Utils.utPageDisAppear(mPageName);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        AppDebug.i(TAGS, TAGS + ".onPause ");
        if (com.yunos.RunMode.isYunos() == Environment.getInstance().isYunos()) {
            mAppUpdateBroadcast.unregisterUpdateBroadcast();
            try {
                super.onPause();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            exitUT();
        } else {
            try {
                super.onPause();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void finish() {
        AppDebug.i(TAGS, TAGS + ".finish ");
        super.finish();
    }

    @Override
    protected void onStop() {
        AppDebug.i(TAGS, TAGS + ".onStop ");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        AppDebug.i(TAGS, TAGS + ".onDestroy ");
        try {
            if (getFocusManager() != null) {
                getFocusManager().destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            // 有些情况下，Act被销毁，但是一些初始化任务可能还在，所以这里取消所有任务 参见：#16966270
            getWindow().getDecorView().getHandler().removeCallbacks(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (RunMode.isYunos() == Environment.getInstance().isYunos()) {
//        stopUpdateApp();
            mOpenedActivity.remove(this.hashCode());
            ActivityPathRecorder.getInstance().onDestroy(this);
            mNewIntent = null;
            unregisterReceiver(mHomeKeyEventReceiver);
        }
        try {
            Field[] fields = BzBaseActivity.class.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                boolean old = field.isAccessible();
                field.setAccessible(true);
                Object value = field.get(this);
                if (value != null && value instanceof BlitzBridgeSurfaceView) {
                    ((BlitzBridgeSurfaceView) value).getHolder().getSurface().release();
                    field.setAccessible(old);
                    break;
                }
                field.setAccessible(old);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getBlitzContext() != null) {
            getBlitzContext().deinitContext();
        }
        super.onDestroy();
    }

    /**
     * 结束应用自升级
     */
    private void stopUpdateApp() {
        if (mUpdateClient != null) {
            mUpdateClient.stopDownload();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        AppDebug.d(TAGS, "TAG  ---- >  startActivity;  this =  " + this);
        startActivityForResult(intent, -1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        AppDebug.d(TAGS, "TAG  ---- >  startActivityForResult;  this =  " + this.hashCode() + "---requestCode---" + requestCode);
        if (intent == null) {
            return;
        }
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            bundle = new Bundle();
        }
//        if(!StringUtils.isEmpty(mJoin)){
//            bundle.putString(CoreIntentKey.URI_JOIN, mJoin);
//        }
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
        recordPreviousNode(intent);
        intent.putExtras(bundle);
        AppDebug.d(TAGS,
                "TAG  ---- >  startActivityForResult;  intent =  " + intent + ", getExtras=" + intent.getExtras() + ", uri = " + intent.getData());
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
            if (!NetWorkUtil.isNetWorkAvailable() && !isGotoNetworkSetting && RunMode.isYunos() == Environment.getInstance().isYunos()) {
                onStartActivityNetWorkError();
                return;
            }
        }
        setInnerIntent(intent);

        // 是否在父类中捕获exception
        boolean catchException = intent.getBooleanExtra(CoreIntentKey.IS_CATCH_EXCEPTION, true);
        // 锁住启动页面的错误
        if (catchException) {
            try {
                super.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.startActivityForResult(intent, requestCode);
        }

    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        boolean rtn = false;
        try {
            rtn = super.dispatchGenericMotionEvent(ev);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return rtn;
    }

    /**
     * 设置内部的启动页面的关键参数
     * 内部之间的页面调用添加内部参数，这个会在调用完成后删除掉
     *
     * @param intent
     */
    public static void setInnerIntent(Intent intent) {
        if (intent != null) {
            intent.putExtra(INTENT_KEY_INNER, true);
        }
    }

    public void setCheckNetWork(boolean check) {
        mNetWorkCheck = check;
    }

    /**
     * 启动activity失败时调用
     */
    abstract protected void onStartActivityNetWorkError();

    /**
     * 初始化统计需要的3个参数，from，from_act, from_app,join参数
     */
    protected void initFromActApp() {
        //先从Bundle中找URI_FROM_BUNDLE,找不到再从uri中找URI_FROM关键词
        mFrom = IntentDataUtil.getStringFromUri(getIntent(), CoreIntentKey.URI_FROM, "");
        if (TextUtils.isEmpty(mFrom)) {
            mFrom = IntentDataUtil.getStringFromBundle(getIntent(), CoreIntentKey.URI_FROM_BUNDLE, "");
        }
        AppDebug.i(TAGS, TAGS + ".initFromActApp mFrom=" + mFrom + ", intent=" + getIntent() + ", getExtras = "
                + getIntent().getExtras());
        if (!TextUtils.isEmpty(mFrom)) {
            mFrom = mFrom.replaceAll("[\\r\\n\\t\\s\\|\\\\\\/]+", "");
        }

        mHuoDong = IntentDataUtil.getStringFromUri(getIntent(), CoreIntentKey.URI_HUODONG, null);
        if (TextUtils.isEmpty(mHuoDong)) {
            mHuoDong = IntentDataUtil.getStringFromBundle(getIntent(), CoreIntentKey.URI_HUODONG_BUNDLE, null);
        }
        if (!TextUtils.isEmpty(mHuoDong)) {
            mHuoDong = mHuoDong.replaceAll("[\\r\\n\\t\\s\\|\\\\\\/]+", "");
        }

        mApp = IntentDataUtil.getStringFromUri(getIntent(), CoreIntentKey.URI_FROM_APP, null);
        if (TextUtils.isEmpty(mApp)) {
            mApp = IntentDataUtil.getStringFromBundle(getIntent(), CoreIntentKey.URI_FROM_APP_BUNDLE, null);
        }
        if (!TextUtils.isEmpty(mApp)) {
            mApp = mApp.replaceAll("[\\r\\n\\t\\s\\|\\\\\\/]+", "");
        }
        AppDebug.i(TAGS, TAGS + ".initFromActApp mApp=" + mApp);
        if (null != mApp && mApp.equals(getAppName())) {
            mApp = null;
        }
        AppDebug.i(TAGS, TAGS + ".initFromActApp mFrom=" + mFrom + ", mHuoDong=" + mHuoDong + ", mApp=" + mApp);
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
     * 是否需要TBS（usertrack）统计
     *
     * @return
     */
    protected boolean isTbs() {
        return true;
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
     * 升级弹框
     *
     * @return
     */
    public boolean isUpdate() {
        return true;
    }

    /**
     * 提示升级弹框黑名单
     *
     * @return
     */
    public boolean isUpdateBlackList() {
        return false;
    }

    /**
     * 新版本特性只有首页弹出
     *
     * @return
     */
    public boolean isHomeActivity() {
        return false;
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

    public String getPageName() {
        return Pattern.compile("(activity|view|null|page|layout)$", Pattern.CASE_INSENSITIVE)
                .matcher(getClass().getSimpleName()).replaceAll("");
    }

    public Map<String, String> getPageProperties() {
        return Utils.getProperties(mFrom, mHuoDong, mApp);
    }

    /**
     * 清除所有已经打开activity
     *
     * @param selfActivity
     */
    public void clearAllOpenedActivity(Activity selfActivity) {
        int count = mOpenedActivity.size();
        for (int i = 0; i < count; i++) {
            Integer key = mOpenedActivity.keyAt(i);
            if (key != null) {
                Activity activity = mOpenedActivity.get(key);
                if (activity != null) {
                    if (selfActivity != null && selfActivity.equals(activity)) {
                        continue;
                    } else {
                        activity.finish();
                    }
                }
            }
        }
    }

    /**
     * 设置是否不需要注册应用更新的广播
     *
     * @param notNeed
     */
    protected void setNotNeedRegisterUpdate(boolean notNeed) {
        mNotNeedRegisterUpdate = notNeed;
    }

    /**
     * 显示应用更新页面强制更新
     *
     * @param bundle
     */
    protected void showAppUpdateActivity(Bundle bundle) {
        mAppUpdateBroadcast.startUpdateActivity(this, bundle);
    }

    private FocusManager focusManager = null;

    @Override
    public FocusManager getFocusManager() {
        if (focusManager == null) {
            focusManager = FocusManager.create(this);
        }
        return focusManager;
    }

    @Override
    public Window getAttachedWindow() {
        return getWindow();
    }

    @Override
    public Context getAttachedContext() {
        return this;
    }

    /**
     * 应用更新页面的监听回调
     *
     * @author tingmeng.ytm
     */
    private static class CoreOnShowAppUpdateListener implements OnShowAppUpdateListener {

        private WeakReference<CoreActivity> mCoreActivityRef;

        CoreOnShowAppUpdateListener(CoreActivity activity) {
            mCoreActivityRef = new WeakReference<CoreActivity>(activity);
        }

        @Override
        public void onShowAppUpdate(Bundle bundle) {
            CoreActivity coreActivity = mCoreActivityRef.get();
            if (coreActivity != null) {
                coreActivity.showAppUpdateActivity(bundle);
            }
        }

        @Override
        public void onShowAppUpdateDialog(Bundle bundle) {

        }
    }

    @Override
    public Uri getCurrentUri() {
        Intent intent = (recordNewIntent() && mNewIntent != null) ? mNewIntent : getIntent();
        Uri currentUri = intent.getParcelableExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_URI);
        if (intent.hasExtra("from_voice")) {
            currentUri = Uri.parse("tvtaobao://home?module=voice");
        }
        if (currentUri == null) {
            currentUri = intent.getData();
        }
        if (currentUri == null) {
            currentUri = ActivityDataUtil.getInstance().getPreviousUri(getClass(), intent);
        }

        if (currentUri != null && !currentUri.toString().startsWith("tvtaobao://home")) {
            currentUri = ActivityDataUtil.getInstance().getAppHostUri(currentUri);
        }
        return currentUri;
    }

    /**
     * not used by now
     */
    public boolean isEqualTo(ActivityPathRecorder.PathNode aNode) {
        if (aNode == this) {
            if (getCurrentUri() == aNode.getCurrentUri())
                return true;
            else if (getCurrentUri() != null && getCurrentUri().equals(aNode.getCurrentUri())) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean isFirstNode() {
        Intent intent = (recordNewIntent() && mNewIntent != null) ? mNewIntent : getIntent();
        return IntentDataUtil.getBoolean(intent, ActivityPathRecorder.INTENTKEY_FIRST, false);
    }

    @Override
    public boolean isIgnored() {
        return false;
    }

    protected void recordPreviousNode(@NonNull Intent intent) {
        if (isIgnored()) {
            intent.putExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_PREVIOUSACTIVITY,
                    getPreviousNodeHash());
            intent.putExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_PREVIOUSINTENT, getPreviousSecondHashCode());
            intent.putExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_PREVIOUSURI, getPreviousNodeUri());

        } else {
            intent.putExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_PREVIOUSACTIVITY,
                    getHashCode());
            intent.putExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_PREVIOUSINTENT, getSecondHashCode());
            intent.putExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_PREVIOUSURI, getCurrentUri());
        }
        if (intent.getData() == null && isIgnored())
            intent.putExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_URI, getCurrentUri());
        else
            intent.putExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_URI, intent.getData());


    }

    @Override
    public int getPreviousNodeHash() {
        Intent intent = (recordNewIntent() && mNewIntent != null) ? mNewIntent : getIntent();
        return intent.getIntExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_PREVIOUSACTIVITY, -1);
    }

    public int getPreviousSecondHashCode() {
        Intent intent = (recordNewIntent() && mNewIntent != null) ? mNewIntent : getIntent();
        return intent.getIntExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_PREVIOUSINTENT, -1);
    }

    @Override
    public Uri getPreviousNodeUri() {
        Intent intent = (recordNewIntent() && mNewIntent != null) ? mNewIntent : getIntent();
        return intent.getParcelableExtra(ActivityPathRecorder.INTENTKEY_PATHRECORDER_PREVIOUSURI);
    }

    @Override
    public int getHashCode() {
        return this.hashCode();
    }

    @Override
    public int getSecondHashCode() {
        Intent intent = (recordNewIntent() && mNewIntent != null) ? mNewIntent : getIntent();
        return intent == null ? 0 : intent.hashCode();
    }

    @Override
    public boolean recordNewIntent() {
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (("DLT").equals(BuildConfig.BUILD_NO)) {//大连天途渠道屏蔽 183-186 四个按键
            if (event.getKeyCode() == KeyEvent.KEYCODE_PROG_RED || event.getKeyCode() == KeyEvent.KEYCODE_PROG_GREEN || event.getKeyCode() == KeyEvent.KEYCODE_PROG_YELLOW || event.getKeyCode() == KeyEvent.KEYCODE_PROG_BLUE) {
                return true;
            }
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME || event.getKeyCode() == KeyEvent.KEYCODE_PROG_RED || event.getKeyCode() == KeyEvent.KEYCODE_PROG_YELLOW) {
            finishAndRemove();
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (CoreApplication.getApplication().getMyLifecycleHandler().isLastActivityInForeground()) {
//                finishAndRemove();
                RtEnv.set(RtEnv.KEY_SHOULD_START_UPDATE,true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    //杀死进程相关
    private void finishAndRemove() {
        onPause();
        // 如果是外部调用的话不做清理工作,但在低内存设备上也要清理
        clearAllOpenedActivity(CoreActivity.this);
        exitChildProcessOfCoreActivity();
        Intent intent = new Intent();
        intent.setPackage(getPackageName());
        intent.setAction("tech.zhiping.audioAction");
        stopService(intent);
        CoreApplication.getApplication().clear();
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 删除子进程（H5页面）
     */
    public void exitChildProcessOfCoreActivity() {
        String packageName = getPackageName();
        ActivityManager activityMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityMgr.getRunningAppProcesses()) {
            if (appProcess.processName.compareTo(packageName + ":bs_webbroser") == 0) {
                AppDebug.i(TAG, "kill processName=" + appProcess.processName);
                android.os.Process.killProcess(appProcess.pid);
            }
            if (appProcess.processName.compareTo(packageName + ":channel") == 0) {
                AppDebug.i(TAG, "kill processName=" + appProcess.processName);
                android.os.Process.killProcess(appProcess.pid);
            }
            if (appProcess.processName.compareTo(packageName + ":dexmerge") == 0) {
                AppDebug.i(TAG, "kill processName=" + appProcess.processName);
                android.os.Process.killProcess(appProcess.pid);
            }
            if (appProcess.processName.compareTo(packageName + ":dex2oat") == 0) {
                AppDebug.i(TAG, "kill processName=" + appProcess.processName);
                android.os.Process.killProcess(appProcess.pid);
            }
        }
    }


    /**
     * 获得升级接口
     */
    private void getNewFeature() {
        SharedPreferences sp = this.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
        int versionCode = sp.getInt(UpdatePreference.UPDATE_CURRENT_VERSION_CODE, 0);
        int nowAppVersionCode = AppInfo.getAppVersionNum();
        if (versionCode >= nowAppVersionCode)
            return;

//        showfeaturesDialog();
//
        try {
            PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packInfo.versionName;
            String versioncode = String.valueOf(packInfo.versionCode);
            String channelId = Config.getChannel();
            String deviceId = CloudUUIDWrapper.getCloudUUID();
            String code = UpdatePreference.TVTAOBAO;
            String version = Build.VERSION.RELEASE;
            String systemInfo = Build.MODEL + "/" + Build.VERSION.SDK;
            String umtoken = Config.getUmtoken(this);


            JSONObject object = new JSONObject();
            object.put("umToken", Config.getUmtoken(this));
            object.put("wua", Config.getWua(this));
            object.put("isSimulator", Config.isSimulator(this));
            object.put("userAgent", Config.getAndroidSystem(this));
            String extParams = object.toString();


            BusinessRequest.getBusinessRequest().requestUpGrade(version, deviceId, channelId, code, versioncode, versionName, systemInfo, umtoken, Config.getModelInfo(CoreActivity.this), extParams,
                    new UpgradeAppListener(this));

        } catch (Exception e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }

    }

    private static class UpgradeAppListener implements RequestListener<String> {


        private CoreActivity mContext;

        public UpgradeAppListener(CoreActivity context) {
            mContext = context;
        }

        @Override
        public void onRequestDone(String data, int resultCode, String handleMessagemsg) {
            AppDebug.d(TAG, "UpgradeAppListener onRequestDone " + data);


            if (resultCode == 200 && data != null) {  //有升级数据开始升级

                com.yunos.tvtaobao.biz.model.AppInfo appInfo = new com.yunos.tvtaobao.biz.model.AppInfo(JSON.parseObject(data));

                SharedPreferences p = mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor e = p.edit();
                e.putString(UpdatePreference.UPDATE_TIPS, StringUtil.isEmpty(appInfo.getReleaseNote()) ? "" : appInfo.getReleaseNote());

                e.commit();

            }


        }

    }


    //新版本特性
    private void showfeaturesDialog() {

        SharedPreferences sp = this.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
        int versionCode = sp.getInt(UpdatePreference.UPDATE_CURRENT_VERSION_CODE, 0);
        int nowAppVersionCode = AppInfo.getAppVersionNum();
        if (versionCode >= nowAppVersionCode)
            return;

        try {
            PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packInfo.versionName;
            String versioncode = String.valueOf(packInfo.versionCode);
            String channelId = Config.getChannel();
            String deviceId = CloudUUIDWrapper.getCloudUUID();
            String code = UpdatePreference.TVTAOBAO;
            String version = Build.VERSION.RELEASE;
            String systemInfo = Build.MODEL + "/" + Build.VERSION.SDK;
            String umtoken = Config.getUmtoken(this);

            BusinessRequest.getBusinessRequest().requestNewFeature(version, deviceId, channelId, code, versioncode, versionName, systemInfo, umtoken, Config.getModelInfo(CoreActivity.this),
                    new UpgradeNewFeatureListener(this));

        } catch (Exception e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }


    }


    private static class UpgradeNewFeatureListener implements RequestListener<String> {


        private CoreActivity mContext;

        public UpgradeNewFeatureListener(CoreActivity context) {
            mContext = context;
        }

        @Override
        public void onRequestDone(String data, int resultCode, String handleMessagemsg) {
            AppDebug.d(TAG, "UpgradeNewFeatureListener onRequestDone " + data);

            if (resultCode == 200 && data != null) {  //有升级数据开始升级

                com.yunos.tvtaobao.biz.model.AppInfo appInfo = new com.yunos.tvtaobao.biz.model.AppInfo(JSON.parseObject(data));

                if (StringUtil.isEmpty(appInfo.releaseAfterNote)) {
                    return;
                }
                mContext.mNewFeatureDialog = new NewFeatureDialog(mContext, appInfo.releaseAfterNote);
                mContext.mNewFeatureDialog.show();
                if (mContext.mUpdateClient == null)
                    mContext.mUpdateClient = UpdateClient.getInstance(mContext.getApplicationContext());
                mContext.mUpdateClient.addCurrentVersionToSP();

            }

        }

    }

    /**
     * 开始应用自升级
     */
    private void startUpdateApp() {
        try {
            PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packInfo.versionName;
            int versionCode = packInfo.versionCode;
            String channelId = Config.getChannel();
            String deviceId = CloudUUIDWrapper.getCloudUUID();
            mUpdateClient = UpdateClient.getInstance(getApplicationContext());
            mUpdateClient.startDownload("tvtaobao", versionName, versionCode, deviceId, channelId, null);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
    }


    /**
     * 开始电信渠道应用自升级
     */
    private void startDXUpdate() {
        if ("2017050920".equals(Config.getChannel()) || "2015082715".equals(Config.getChannel())) {
            UpdateFromDX.getInstance(this).appCheckUpdate();
        }
    }
}
