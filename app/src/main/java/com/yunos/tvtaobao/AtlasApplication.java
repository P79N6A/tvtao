package com.yunos.tvtaobao;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.os.Looper;
import android.taobao.atlas.bundleInfo.AtlasBundleInfoManager;
import android.taobao.atlas.framework.Atlas;
import android.taobao.atlas.runtime.ActivityTaskMgr;
import android.taobao.atlas.runtime.ClassNotFoundInterceptorCallback;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bftv.fui.constantplugin.TellCode;
import com.bftv.fui.tell.Tell;
import com.bftv.fui.tell.TellManager;
import com.tvtaobao.voicesdk.services.BftvASRService;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.blitz.account.AccountUtils;
import com.yunos.tv.blitz.account.BzDebugLog;
import com.yunos.tv.blitz.account.BzJsCallImpAccountListener;
import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.blitz.global.BzAppConfig;
import com.yunos.tv.blitz.global.BzAppContext;
import com.yunos.tv.blitz.global.BzAppMain;
import com.yunos.tv.blitz.global.BzAppParams;
import com.yunos.tv.blitz.global.BzApplication;
import com.yunos.tv.blitz.global.BzEnvEnum;
import com.yunos.tv.core.AppInitializer;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.ActivityQueueManager;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.net.network.NetworkManager;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.blitz.BzAccountListener;
import com.yunos.tvtaobao.blitz.TaobaoBaseBzJsCallBaseListener;
import com.yunos.tvtaobao.blitz.TaobaoBzMiscListener;
import com.yunos.tvtaobao.blitz.TaobaoBzPageStatusListener;
import com.yunos.tvtaobao.blitz.TaobaoUIBzJsCallUIListener;
import com.yunos.tvtaobao.payment.request.ScanBindRequest;
import com.yunos.tvtaobao.receiver.UpdateTimeReceiver;
import com.yunos.tvtaobao.request.DeviceChannelBuilder;

import org.osgi.framework.BundleException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtopsdk.xstate.util.PhoneInfo;


/**
 * Created by huangdaju on 17/5/22.
 */

public class AtlasApplication extends CoreApplication {

    private static final String TAG = "AtlasApplication";
    public static final String TVTAOBAO_SERVER_ID = "tvtaobao";
    public static final String POWER_SERVER_ID = "powermsg";
    public static volatile boolean mForceBindUser = false;
    private static final Map<String, String> accsServices;  //accs使用到的服务
    static {
        accsServices = new HashMap<>();
        accsServices.put("powermsg", "com.taobao.tao.messagekit.base.AccsReceiverService");
        accsServices.put("tvtaobao", "com.yunos.tvtaobao.tvtaomsg.TvTaobaoReceviceService");
        accsServices.put("orange", "com.taobao.orange.accssupport.OrangeAccsService");
        accsServices.put("agooSend", "org.android.agoo.accs.AgooService");
        accsServices.put("agooAck", "org.android.agoo.accs.AgooService");
        accsServices.put("agooTokenReport", "org.android.agoo.accs.AgooService");
    }
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        onCreateTime = System.currentTimeMillis();
        mApplication = this;
        LogPrint.d(TAG, TAG + ".onCreate");
        if (isACCSChannel()) {
            return;
        }

        super.onCreate();

        //初始化atlas
        atlasInstance();

        AppInitializer.doAppInit(Config.getTTid());

        initASR();
        initBlitzSdk();
        initReceiver();
        initExitBroadcast();

        NetworkManager.instance().init(this);
        //先注册mLoginListenerReceiver 防止activity销毁时未unregister
        getLoginHelper(getApplication()).registerLoginListener(getApplication());

        initDevice();

        GlobalConfigInfo.getInstance().requestGlobalConfigData();
    }

    /**
     * 初始化语音
     */
    private void initASR(){
        if (com.yunos.RunMode.isYunos() || Config.getChannel().equals("2016102417")) { //康佳接了yunos的语音
            Intent intent = new Intent();
            intent.setPackage(getApplication().getPackageName());
            intent.setAction("com.yunos.tvtaobao.asr.startASRService");
            startService(intent);
        } else if (Config.getChannel().equals("2016010811")) {  //暴风渠道号2016010811
            LogPrint.e(TAG, TAG + ".initBaofengASR registed all asr");
            Tell tell = new Tell();
            tell.pck = AppInfo.getPackageName();
            tell.tellType = TellCode.TELL_ASR;
            tell.key = "MFwwDQYJKoZIhvcNAQE";
            TellManager.getInstance().tell(CoreApplication.getApplication(), tell);
        }
    }


    private void initDevice() {
        if (com.yunos.RunMode.isYunos()){
            String appkey = SharePreferences.getString("device_appkey", "");
            String brandName = SharePreferences.getString("device_brandname", "");
            ScanBindRequest.setAppKey(appkey);
            if (TextUtils.isEmpty(appkey) && TextUtils.isEmpty(brandName)) {
                //当device_appkey和device_brandname都为空的时候，请求设备appkey
                DeviceChannelBuilder deviceChannelBuilder = new DeviceChannelBuilder(getApplication());
                deviceChannelBuilder.onRequestData();
            }
        }
    }

    /**
     * 初始化blitz
     */
    private void initBlitzSdk() {
        BzAppMain.mMtopInstance = AppInitializer.getMtopInstance();

        getLoginHelper(this);

        // register  base  Listener
        setJsCallBaseListener(new TaobaoBaseBzJsCallBaseListener());

        // register  ui  Listener
        setJsCallUIListener(new TaobaoUIBzJsCallUIListener());


        // register  request  Listener
        setMiscListener(new TaobaoBzMiscListener());

        // register  page status  Listener
        setPageStatusListener(new TaobaoBzPageStatusListener());

        // register  account status  Listener
        setAppGlobalListener(new BzAccountListener());

        setJsCallAccountListener(new BzJsCallImpAccountListener() {
            @Override
            public String onAccountGetUserInfo(Context context, String params, final int callback) {
                final BzResult result = new BzResult();
                final BzApplication app = (BzApplication) BzAppConfig.context.getContext();
                int tokenType = this.getTokeType(params);
                int tyidVersion = AccountUtils.getVersioncode(BzAppConfig.context.getContext(), "com.aliyun.ams.tyid");
                Log.d(TAG, "tyidVersion=" + tyidVersion + " ,type = " + tokenType);
                boolean isLogin;
                final LoginHelper helper;
                if (tokenType == 1) {
                    isLogin = BzApplication.getLoginHelper(BzAppConfig.context.getContext()).isLogin();
                    if (isLogin) {
                        LoginHelper loginHelper = BzApplication.getLoginHelper(BzAppConfig.context.getContext());
                        result.addData("userNick", loginHelper.getNick());
                        result.addData("sid", loginHelper.getSessionId());
                        BzDebugLog.d(TAG, "usernick:" + loginHelper.getNick() + "sid:" + loginHelper.getSessionId());
                        result.setSuccess();
                        app.replyCallBack(callback, true, result.toJsonString());

                        result.setSuccess();
                        return result.toJsonString();
                    } else {
                        if (BzApplication.getLoginHelper(BzAppConfig.context.getContext()).getRegistedContext() == null) {
                            BzApplication.getLoginHelper(BzAppConfig.context.getContext()).registerLoginListener(BzAppConfig.context.getContext());
                        }

                        helper = BzApplication.getLoginHelper(BzAppConfig.context.getContext());
                        helper.addSyncLoginListener(new LoginHelper.SyncLoginListener() {
                            public void onLogin(boolean isSuccess) {
                                BzApplication.getLoginHelper(BzAppConfig.context.getContext()).removeSyncLoginListener(this);
                                if (isSuccess) {
                                    result.addData("userNick", helper.getNick());
                                    result.addData("sid", helper.getSessionId());
                                    BzDebugLog.d(TAG, "usernick:" + helper.getNick() + "sid:" + helper.getSessionId());
                                    result.setSuccess();
                                    app.replyCallBack(callback, true, result.toJsonString());
                                } else {
                                    app.replyCallBack(callback, false, BzResult.RET_NOT_LOGIN.toJsonString());
                                }

                            }
                        });
                        BzApplication.getLoginHelper(BzAppConfig.context.getContext()).login(BzAppConfig.context.getContext());
                        result.setSuccess();
                        return result.toJsonString();
                    }
                } else if (tokenType == 0) {
                    isLogin = BzApplication.getLoginHelper(BzAppConfig.context.getContext()).isLogin();
                    if (isLogin) {
                        LoginHelper loginHelper = BzApplication.getLoginHelper(BzAppConfig.context.getContext());
                        result.addData("userNick", loginHelper.getNick());
                        result.addData("sid", loginHelper.getSessionId());
                        BzDebugLog.d(TAG, "usernick:" + loginHelper.getNick() + "sid:" + loginHelper.getSessionId());
                        result.setSuccess();
                        app.replyCallBack(callback, true, result.toJsonString());

                        result.setSuccess();
                        return result.toJsonString();
                    } else {
                        if (BzApplication.getLoginHelper(BzAppConfig.context.getContext()).getRegistedContext() == null) {
                            BzApplication.getLoginHelper(BzAppConfig.context.getContext()).registerLoginListener(BzAppConfig.context.getContext());
                        }

                        helper = BzApplication.getLoginHelper(BzAppConfig.context.getContext());
                        helper.addSyncLoginListener(new LoginHelper.SyncLoginListener() {
                            public void onLogin(boolean isSuccess) {
                                BzApplication.getLoginHelper(BzAppConfig.context.getContext()).removeSyncLoginListener(this);
                                if (isSuccess) {
                                    result.addData("userNick", helper.getNick());
                                    result.addData("sid", helper.getSessionId());
                                    BzDebugLog.d(TAG, "usernick:" + helper.getNick() + "sid:" + helper.getSessionId());
                                    result.setSuccess();
                                    app.replyCallBack(callback, true, result.toJsonString());
                                } else {
                                    app.replyCallBack(callback, false, BzResult.RET_NOT_LOGIN.toJsonString());
                                }

                            }
                        });
                        BzApplication.getLoginHelper(BzAppConfig.context.getContext()).login(BzAppConfig.context.getContext());
                        result.setSuccess();
                        return result.toJsonString();
                    }
                } else {
                    app.replyCallBack(callback, false, BzResult.RET_NOT_LOGIN.toJsonString());
                    return "";
                }
            }
        });
        BzDebugLog.setLogSwitcher(Config.isDebug());
        BzAppContext.setEnvMode(RunMode.DAILY.equals(Config.getRunMode()) ? BzEnvEnum.DAILY : BzEnvEnum.ONLINE);
        BzAppParams params = new BzAppParams();
        params.imei = PhoneInfo.getImei(this);
        params.imsi = PhoneInfo.getImsi(this);
        params.appKey = Config.getAppKey();
        params.ttid = Config.getTTid();
        params.appTag = "TVTB";
        params.appVersion = SystemConfig.APP_VERSION;
        params.uuid = CloudUUIDWrapper.getCloudUUID();
        BzAppContext.setBzAppParams(params);
    }

    /**
     * 初始化Atlas
     */
    private void atlasInstance() {
        Atlas.getInstance().setClassNotFoundInterceptorCallback(new ClassNotFoundInterceptorCallback() {
            @Override
            public Intent returnIntent(Intent intent) {
                final String className = intent.getComponent().getClassName();
                final String bundleName = AtlasBundleInfoManager.instance().getBundleForComponet(className);

                if (!TextUtils.isEmpty(bundleName) && !AtlasBundleInfoManager.instance().isInternalBundle(bundleName)) {

                    //远程bundle
                    Activity activity = ActivityTaskMgr.getInstance().peekTopActivity();
                    File remoteBundleFile = new File(activity.getExternalCacheDir(), "lib" + bundleName.replace(".", "_") + ".so");

                    String path = "";
                    if (remoteBundleFile.exists()) {
                        path = remoteBundleFile.getAbsolutePath();
                    } else {
                        Toast.makeText(activity, " 远程bundle不存在，请确定 : " + remoteBundleFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        return intent;
                    }


                    PackageInfo info = activity.getPackageManager().getPackageArchiveInfo(path, 0);
                    try {
                        Atlas.getInstance().installBundle(info.packageName, new File(path));
                    } catch (BundleException e) {
                        Toast.makeText(activity, " 远程bundle 安装失败，" + e.getMessage(), Toast.LENGTH_LONG).show();

                        e.printStackTrace();
                    }

                    activity.startActivities(new Intent[]{intent});

                }

                return intent;
            }
        });
    }


    private void initReceiver() {
        //生成广播处理
        UpdateTimeReceiver receiver = new UpdateTimeReceiver();
        //实例化过滤器并设置要过滤的广播
        IntentFilter dateChange = new IntentFilter("android.intent.action.DATE_CHANGED");
        IntentFilter timeSet = new IntentFilter("android.intent.action.TIME_SET");
        IntentFilter timezoneChange = new IntentFilter("android.intent.action.TIMEZONE_CHANGED");

        //注册广播
        registerReceiver(receiver, dateChange);
        registerReceiver(receiver, timeSet);
        registerReceiver(receiver, timezoneChange);
    }

    /**
     * 判断是不是accs进程
     *
     * @return
     */
    protected boolean isACCSChannel() {
        ActivityManager activityMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int myPid = android.os.Process.myPid();//获取当前运行进程pid
        for (ActivityManager.RunningAppProcessInfo appProcess : activityMgr.getRunningAppProcesses()) {
            if (("com.yunos.tvtaobao:channel".equals(appProcess.processName) || "rca.rc.tvtaobao:channel".equals(appProcess.processName)) && appProcess.pid == myPid) {
                AppDebug.i(TAG, "processName=" + appProcess.processName);
                return true;
            }
        }
        return false;
    }


    @Override
    public void onTerminate() {
//        CoreApplication.getLoginHelper(CoreApplication.getApplication()).unregisterLoginListener();
        // 程序终止的时候执行
        Intent intent = new Intent();
        if (com.yunos.RunMode.isYunos()) {
            intent.setPackage(getApplication().getPackageName());
            intent.setAction("com.yunos.tvtaobao.asr.startASRService");
            stopService(intent);
        }
        super.onTerminate();
    }

    @Override
    public void clear() {
        AppDebug.d(TAG, TAG + "------clear");
        if (com.yunos.RunMode.isYunos()) {
            Intent intent = new Intent();
            intent.setPackage(getApplication().getPackageName());
            intent.setAction("com.yunos.tvtaobao.asr.startASRService");
            stopService(intent);
        } else if (Config.getChannel().equals("2016010811")) {  //暴风渠道号2016010811
            Intent intent = new Intent();
            intent.setPackage(getApplication().getPackageName());
            intent.setAction("intent.action.user.rca.rc.tvtaobao");
            stopService(intent);

            Intent intent1 = new Intent();
            intent1.setClass(this, BftvASRService.class);
            stopService(intent1);
            AppDebug.i(TAG, "关闭BftvASRService");
        }
        super.clear();
    }

    private void initExitBroadcast() {
        ExitReceiver exitReceiver = new ExitReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.yunos.tvtaobao.exit.application");
        registerReceiver(exitReceiver, intentFilter);
    }

    private class ExitReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.yunos.tvtaobao.exit.application".equals(intent.getAction())) {
                clear();
                try {
                    DialogManager.getManager().dismissAllDialog();
                    ActivityQueueManager.getInstance().onClearAllMapActivityList();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(0);
                }
            }
        }
    }
}
