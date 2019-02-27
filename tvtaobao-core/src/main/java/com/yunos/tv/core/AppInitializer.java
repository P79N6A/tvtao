package com.yunos.tv.core;

import android.text.TextUtils;
import android.util.Log;

import com.yunos.CloudUUIDWrapper;
import com.yunos.alitvcompliance.TVCompliance;
import com.yunos.alitvcompliance.types.RetCode;
import com.yunos.alitvcompliance.types.RetData;
import com.yunos.alitvcompliance.utils.UTHelper;
import com.ali.auth.third.core.MemberSDK;
import com.ali.auth.third.core.callback.InitResultCallback;
import com.ali.auth.third.core.config.ConfigManager;
import com.ali.auth.third.core.config.Environment;
import com.alibaba.motu.crashreporter.MotuCrashReporter;
import com.alibaba.motu.crashreporter.ReporterConfigure;
import com.alibaba.wireless.security.open.SecurityGuardManager;
import com.ut.mini.IUTApplication;
import com.ut.mini.UTAnalytics;
import com.ut.mini.UTHitBuilders;
import com.ut.mini.core.sign.IUTRequestAuthentication;
import com.ut.mini.core.sign.UTSecuritySDKRequestAuthentication;
import com.ut.mini.crashhandler.IUTCrashCaughtListner;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.RunMode;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.core.listener.MotuCrashCaughtListener;
import com.yunos.tv.core.util.MonitorUtil;
import com.yunos.tvtaobao.payment.CustomLoginFragment;

import java.util.HashMap;
import java.util.Map;

import mtopsdk.common.util.TBSdkLog;
import mtopsdk.mtop.domain.EnvModeEnum;
import mtopsdk.mtop.global.SDKUtils;
import mtopsdk.mtop.intf.Mtop;

/**
 * Created by GuoLiDong on 2019/1/3.
 * 启动之初，初始化一些SDK
 * 为了能清楚的管理各项启动任务
 * 把这些任务从CoreApplication中独立出来
 */
public class AppInitializer {
    private static final String TAG = AppInitializer.class.getSimpleName();

    private static boolean FLAG_MemberSDK_Fully_Done = false;

    private static Mtop mtopInstance;

    public static Mtop getMtopInstance() {
        if (mtopInstance == null) {
            synchronized (AppInitializer.class) {
                if (mtopInstance == null) {
                    mtopInstance = initMtopSDK();
                }
            }
        }
        return mtopInstance;
    }

    public static void doAppInit(final String channel) {
        // 监测设备和系统状态
        initDeviceAndSystemInfo();
        AppDebug.i(TAG, "doAppInit initDeviceAndSystemInfo");
        /***
         * 初始化安全保镖
         *   被各种集团内的SDK依赖，放前头没错！
         */
        initSecurityGuardSDK();
        AppDebug.i(TAG, "doAppInit initSecurityGuardSDK");
        // 初始化牌照
        initComplianceSDK();
        AppDebug.i(TAG, "doAppInit initComplianceSDK");
        // 初始化UT打点SDK
        initUTAnalyticsSDK(channel);
        AppDebug.i(TAG, "doAppInit initUTAnalyticsSDK");
        /**
         * 初始化MtopSDK
         *   会被MemberSDK依赖
         */
        getMtopInstance();
        AppDebug.i(TAG, "doAppInit getMtopInstance");
        // 初始化UUID
        initUUID();
        AppDebug.i(TAG, "doAppInit initUUID");
        // 初始化会员SDK
        initMemberSDK();
        AppDebug.i(TAG, "doAppInit initMemberSDK");
        // 检查初始化状态
        checkMemberSDKAndMtopState();
        AppDebug.i(TAG, "doAppInit checkMemberSDKAndMtopState");
        /**
         * 初始化CrashHandler
         *   内部实现中对MemberSDK内组件有依赖
         *   所以得放在其后初始化
         */
        initCrashReporterSDK();
        AppDebug.i(TAG, "doAppInit initCrashReporterSDK");
        // 性能数据收集SDK
        initMonitorPointsSDK();
        AppDebug.i(TAG, "doAppInit initMonitorPointsSDK");

    }

    /*---------------------------------------------------------------------*/

    /**
     * 初始化UT打点SDK
     *
     * @param channel
     */
    public static void initUTAnalyticsSDK(final String channel) {
        UTAnalytics.getInstance().setAppApplicationInstance(CoreApplication.getApplication(), new IUTApplication() {
            @Override
            public String getUTAppVersion() {
                return SystemConfig.APP_VERSION;
            }

            //发布的渠道号，如果不需要区分，可以随便填写
            @Override
            public String getUTChannel() {
                return channel;
            }

            @Override
            public IUTRequestAuthentication getUTRequestAuthInstance() {
                return new UTSecuritySDKRequestAuthentication(Config.getAppKey());
            }

            //是否调试，true模式下，logcat会输出相关日志，否则不会输出到logcat
            @Override
            public boolean isUTLogEnable() {
                return true;
            }

            //自定义yunos校验方式，非aliyun平台可以填false
            @Override
            public boolean isAliyunOsSystem() {
                return false;
            }

            @Override
            public IUTCrashCaughtListner getUTCrashCraughtListener() {
                return null;
            }

            //是否需要关掉通过ut捕获异常上报，填true表示关掉ut-analytics crash的捕获功能，填false表示启用
            @Override
            public boolean isUTCrashHandlerDisable() {
                return false;
            }
        });
        UTAnalytics.getInstance().turnOffAutoPageTrack();//关闭自动activity页面的采集的埋点，需要调试下是否关闭
//        if (Config.isDebug()) {
//            UTAnalytics.getInstance().turnOnDebug();
//        }
//
//        UTAnalytics.getInstance().setContext(getApplication());
//        UTAnalytics.getInstance().setAppApplicationInstance(getApplication());
//        UTAnalytics.getInstance().setChannel(channel);
//        UTAnalytics.getInstance().setRequestAuthentication(new UTSecuritySDKRequestAuthentication(Config.getAppKey()));
//        UTAnalytics.getInstance().setAppVersion(SystemConfig.APP_VERSION);
//        UTAnalytics.getInstance().setCrashCaughtListener(new IUTCrashCaughtListner() {
//
//            @Override
//            public Map<String, String> onCrashCaught(Thread pThread, Throwable pException) {
//                // 返回一个 map，追加特定数据到crash日志上
//                return null;
//            }
//        });
//
//        UTAnalytics.getInstance().turnOffAutoPageTrack();//关闭自动activity页面的采集的埋点，需要调试下是否关闭
//        mInitTBS = true;
//        AppDebug.i(TAG, TAG + ".initUTAnalyticsSDK finish");
    }

    /**
     * 初始化MtopSDK
     * 被BlitzSDK依赖（实际BlitzSDK中已内置Mtop，不过与外部使用的Mtop版本不同。
     * 或许是为了让网页能共享App内的Mtop的会话，所以两者使用同一个Mtop）
     */
    private static Mtop initMtopSDK() {
        // 测试时先关闭tLog，使得log在logcat中打印。当应用发布出去后打开收集tLog的log
        TBSdkLog.setTLogEnabled(false);  //关闭输出到TLog日志的开关
        if (Config.isDebug()) {
            TBSdkLog.setPrintLog(true); //打开输出到系统日志的开关，日常和预发默认开启，线上默认关闭
            TBSdkLog.setLogEnable(TBSdkLog.LogEnable.DebugEnable); //设置日志显示的最低级DEBUG<INFO<WARN<ERROR<NONE
            anet.channel.util.ALog.setUseTlog(false); //打开networksdk日志
        }

//        MtopSetting.setAppKeyIndex(0, 2);
//        MtopSetting.setAppVersion(SystemConfig.APP_VERSION);
        String ttid = Config.getTTid();
        Mtop mtop = Mtop.instance(CoreApplication.getApplication(), ttid);
        Log.e(TAG,"[initMtopSdk]"+User.getSessionId());
        if (!TextUtils.isEmpty(User.getSessionId())) {
            mtop.registerSessionInfo(User.getSessionId(), User.getUserId());
        }
        if (Config.getRunMode() == RunMode.PRODUCTION) {
            mtop.switchEnvMode(EnvModeEnum.ONLINE);
        } else if (Config.getRunMode() == RunMode.PREDEPLOY) {
            mtop.switchEnvMode(EnvModeEnum.PREPARE);
        } else {
            mtop.switchEnvMode(EnvModeEnum.TEST);
        }
        // 测试时打开logcat输出日志，发布后关闭。
//        TBSdkLog.setPrintLog(Config.isDebug());
        SDKUtils.registerTtid(ttid);
//        //TODO 关闭SDK的主机名校验(全部)
//        NetworkSdkSetting.setHostnameVerifier(NetworkSdkSetting.ALLOW_ALL_HOSTNAME_VERIFIER);
        return mtop;
    }

    /**
     * 初始化会员三方登陆SDK
     */
    private static void initMemberSDK() {
        if (Config.isAgreementPay()) {
            ConfigManager.getInstance();
            if (Config.isDebug()) {
                MemberSDK.turnOnDebug();
            }
            AppDebug.d("test", "memberSDK init begin");
            MemberSDK.init(CoreApplication.getApplication(), new InitResultCallback() {
                @Override
                public void onSuccess() {
                    AppDebug.d("test", "memberSDK init success");
                    if (Config.getRunMode() == RunMode.PRODUCTION) {
                        MemberSDK.setEnvironment(Environment.ONLINE);
                    } else if (Config.getRunMode() == RunMode.PREDEPLOY) {
                        MemberSDK.setEnvironment(Environment.PRE);
                    } else {
                        MemberSDK.setEnvironment(Environment.TEST);
                    }
//                initTBS();
//                MemberSDK.setEnvironment(Environment.TEST);
//                MtopSetting.setAppKeyIndex(0, 2);
//
//                ConfigManager.getInstance().setAppKeyIndex(2);
                    done();
                }

                @Override
                public void onFailure(int i, String s) {
                    AppDebug.d("test", "memberSDK init fail " + s);
                    try {
                        if (Config.getRunMode() == RunMode.PRODUCTION) {
                            MemberSDK.setEnvironment(Environment.ONLINE);
                        } else if (Config.getRunMode() == RunMode.PREDEPLOY) {
                            MemberSDK.setEnvironment(Environment.PRE);
                        } else {
                            MemberSDK.setEnvironment(Environment.TEST);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    done();
                }

                private void done(){
                    FLAG_MemberSDK_Fully_Done = true;
                }
            });


            ConfigManager.getInstance().setFullyCustomizedLoginFragment(
                    CustomLoginFragment.class);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("config", "{\"qrwidth\": 80}");
            ConfigManager.getInstance().setScanParams(params);
        }
    }

    /**
     * 检查memberSDK和Mtop的初始化状态。
     *  memberSDK初始化函数调用后，需要100ms左右完成会话凭证的初始化，
     *  并且将会话注入MtopSDK中（注入发生在非主线程中）。
     *  所以这里要有个循环检查，确保进行下一步初始化的时候已经准备好相应信息，
     *  这样在页面打开时候在能这缺读取到会话信息
     */
    private static void checkMemberSDKAndMtopState(){
        try {
            long limit = 300;
            long checkTime = System.currentTimeMillis();
            String sid = null;
            String uid = null;
            AppDebug.i(TAG,"[checkMemberSDKAndMtopState]"+sid+","+uid);
            while (true){
                sid = getMtopInstance().getMultiAccountSid(null);
                uid = getMtopInstance().getMultiAccountUserId(null);
                if ((!TextUtils.isEmpty(sid)) || (!TextUtils.isEmpty(uid))){
                    break;
                }
                if (System.currentTimeMillis()-checkTime>limit){
                    break;
                }
                if (FLAG_MemberSDK_Fully_Done){
                    break;
                }
                Thread.yield();
            }
            AppDebug.i(TAG,"[checkMemberSDKAndMtopState]"+sid+","+uid);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化安全保镖
     */
    private static void initSecurityGuardSDK() {
        AppDebug.i(TAG, TAG + ".initSecurity() is running!");
        try {
            SecurityGuardManager.getInstance(CoreApplication.getApplication().getBaseContext());
            SecurityGuardManager.getInitializer().initialize(CoreApplication.getApplication());
        } catch (Exception e) {
            e.printStackTrace();
            AppDebug.i(TAG, TAG + ".initSecurity() is SecException!");
        }

    }

    /**
     * 初始化崩溃上报SDK
     */
    private static void initCrashReporterSDK() {
//        UTAnalytics.getInstance().turnOffCrashHandler();

        ReporterConfigure reporterConfigure = new ReporterConfigure();

        reporterConfigure.setEnableDebug(Config.isDebug()); //开启debug模式，可以看到调试信息输出
        reporterConfigure.setEnableDumpSysLog(true); //开启Dump系统日志模式，可以在crash时上传系统日志
        reporterConfigure.setEnableDumpRadioLog(true); //开启Dump射频相关的log模式，SIM、STK也会在里面，modem相关的ATcommand等信息会在crash时上传
        reporterConfigure.setEnableDumpEventsLog(true); //开启Dump事件日志模式，如发生登入登出等事件将会被记录，crash时一并上传
        reporterConfigure.setEnableCatchANRException(true); //开启ANR监听，ANR默认是开启的，设置该接口可关闭ANR功能
        reporterConfigure.setEnableANRMainThreadOnly(false); //开启传递主线程的ANR信息模式，如果设置为false，将上传ANR时的所有线程信息
        reporterConfigure.setEnableDumpAllThread(true); //开启Dump所有线程数据模式，这个设置只对java及native crash有效
        reporterConfigure.enableDeduplication = false; //关闭去重

        String version = SystemConfig.APP_VERSION;

        AppDebug.i(TAG, TAG + ".initMotuCrashSDK version = " + version);

        MotuCrashReporter.getInstance().enable(CoreApplication.getApplication(), Config.getAppId(), Config.getAppKey(), version, Config.getChannel(), null,
                reporterConfigure);

        MotuCrashReporter.getInstance().setUserNick(User.getNick());

        MotuCrashReporter.getInstance().setCrashCaughtListener(new MotuCrashCaughtListener());
    }

    /**
     * 初始化牌照SDK（D模式上不需要初始化牌照，YunOS上需要）
     */
    private static void initComplianceSDK() {
        if (com.yunos.RunMode.isYunos() == com.yunos.ott.sdk.core.Environment.getInstance().isYunos()) {
            TVCompliance.init(CoreApplication.getApplication(), Config.isDebug(), null, new UTHelper.IUTCustomEventSender() {
                @Override
                public void sendCustomEvent(String s, Map<String, String> map) {
                    UTHitBuilders.UTCustomHitBuilder builder = new UTHitBuilders.UTCustomHitBuilder(s);
                    builder.setProperties(map);
                    UTAnalytics.getInstance().getDefaultTracker().send(builder.build());
                }
            });
        }
    }

    /**
     * 初始化性能监测SDK
     */
    private static void initMonitorPointsSDK() {
        MonitorUtil.init();
    }

    /**
     * 初始化UUID
     * 很多地方都要使用UUID
     */
    private static void initUUID() {
        if (!com.yunos.RunMode.isYunos()){
            CloudUUIDWrapper.init(CoreApplication.getApplication(), true);
            Log.d(TAG, TAG + ".initUUID uuid = " + CloudUUIDWrapper.getCloudUUID());
            if (TextUtils.isEmpty(CloudUUIDWrapper.getCloudUUID())) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, TAG + ".GenerateUUIDRunnable, genereate uuid");
                        CloudUUIDWrapper.setAndroidOnly(true);
                        CloudUUIDWrapper.generateUUIDAsyn(new CloudUUIDWrapper.IUUIDListener() {
                            @Override
                            public void onCompleted(int error, float time) {
                                Log.d(TAG, TAG + ".GenerateUUIDRunnable onCompleted: error=" + error + " time:" + time + ", uuid = " + CloudUUIDWrapper.getCloudUUID());
                                if (error == 0) {
                                    // UUID生成成功
                                }
                            }
                        }, "TVAppStore", null);
                    }
                });
                thread.start();
            } else {

            }
        }
    }

    /**
     * 初始化设备信息和系统信息
     */
    private static void initDeviceAndSystemInfo(){
        try {
            // 初始化设备信息
            DeviceJudge.initSystemInfo(CoreApplication.getApplication());
            AppDebug.i(TAG, DeviceJudge.getDevicePerformanceString());
            // 初始化系统配置
            SystemConfig.init(CoreApplication.getApplication());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
