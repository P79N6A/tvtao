/**
 * $
 * PROJECT NAME: TvTaoBaoBase
 * PACKAGE NAME: com.yunos.tv.tvtaobaobase.config
 * FILE NAME: Config.java
 * CREATED TIME: 2014-10-23
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tv.core.config;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.wireless.security.open.SecException;
import com.alibaba.wireless.security.open.SecurityGuardManager;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.R;
import com.yunos.tv.core.common.SharePreferences;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * Class Descripton.
 *
 * @author hanqi
 * @data 2014-10-23 下午5:02:52
 */
public class Config {

    private static final String TAG = "CoreConfig";

    public static final String MOHE = "701229";
    public static final String LIANMENG = "10003226";
    public static final String YITIJI = "10004416";
    public static final String CESHI = "142857";

    private static Config instance = null;
    private boolean debug = true;
    private RunMode RUN_MODE = RunMode.PRODUCTION;
    private String channel;
    private String channelName;
    private String ttid;
    private String buildId;
    private String mtopApiVersion;
    private boolean agreementPay = true;
    private String versionName;

    private final String[] getBlackList() {
        String[] rtn = new String[]{
                "INPHIC_RK3368",
                "IDER_BBA71",
                "10MOONS_GT7",
                "ZM_Z88"
        };
        return rtn;
    }

    private Config(){}
    private Config(Context context){
        initDebugConfig(context);

        initVersionName(context);

        initAgreementPayMode(context);

        initChannel(context);

        initChannelName(context);

        initRunMode(context);

        initBuildId(context);

        initMtopVersion(context);

        log("BuildID: " + buildId + " mTopApiVersion: " + mtopApiVersion + " RunMode: " + RUN_MODE
                + " isDebug: " + debug);
    }

    public static Config getInstance() {
        if (instance==null){
            synchronized (Config.class){
                if (instance==null){
                    instance = new Config(CoreApplication.getApplication());
                }
            }
        }
        return instance;
    }

    private void initAgreementPayMode(Context context) {
        agreementPay = context.getResources().getBoolean(R.bool.agreementPay);
        if (agreementPay) {
            for (String dev : getBlackList()) {
                if (dev.equals(Build.MODEL)) {
                    agreementPay = false;
                    return;
                }
            }
        }
        boolean localPref = SharePreferences.getBoolean("agreement_enable", true);
        agreementPay = agreementPay && localPref;
        log("initAgreementPayMode :"+ agreementPay +"," + localPref);
    }

    private void initChannel(Context context) {
        String appkey = SharePreferences.getString("device_appkey", "");
        if (TextUtils.isEmpty(appkey))
            channel = context.getString(R.string.channelId);
        else
            channel = appkey;
        ttid = channel + "@tvtaobao_android_" + AppInfo.getAppVersionName();
        log("initChannel:" + channel + ","+ttid);
    }

    private void initChannelName(Context context) {
        channelName = context.getString(R.string.channel);
        log("initChannelName:" + channelName);
    }

    private void initMtopVersion(Context context) {
        mtopApiVersion = context.getString(R.string.mtopApiVersion);
        log("initMtopVersion:" + mtopApiVersion);
    }

    private void initDebugConfig(Context context) {
        debug = context.getResources().getBoolean(R.bool.isDebug);
        log("initDebugConfig:" + debug);
    }

    private void initBuildId(Context context) {
        buildId = context.getString(R.string.buildId);
        log("initBuildId:" + buildId);
    }

    private void initVersionName(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (info != null)
            versionName = info.versionName;
        log("initVersionName:" + versionName);
    }

    private void initRunMode(Context context) {
        String s = "";
        if (debug) {
            s = SharePreferences.getString("device_env", "");
        } else {
            s = context.getString(R.string.runMode);
        }
        if ("PRODUCTION".equals(s)) {
            RUN_MODE = RunMode.PRODUCTION;
        } else if ("PREDEPLOY".equals(s)) {
            RUN_MODE = RunMode.PREDEPLOY;
        } else if ("DAILY".equals(s)) {
            RUN_MODE = RunMode.DAILY;
        } else {
            RUN_MODE = RunMode.PRODUCTION;
        }
        log("initRunMode:" + RUN_MODE);
    }

    private static void log(String msg){
        Log.e(TAG,msg);
    }

    /**
     * 设置协议支付开关
     * @param agreementPay
     */
    public static void setAgreementPay(boolean agreementPay) {
        getInstance().agreementPay = agreementPay;
        SharePreferences.put("agreement_enable", agreementPay);
        log("setAgreementPay:" + getInstance().agreementPay);
    }

    /**
     * 得到版本名
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        return getInstance().versionName;
    }

    /**
     * MTOP接口用到的appKey
     *
     * @return
     * @author hanqi
     * @date 2014-5-27
     */
    public static String getAppKey() {
        String rtn = null;
        if (getRunMode() == RunMode.DAILY) {
            rtn = "4272";
        } else if (getRunMode() == RunMode.PREDEPLOY) {
            rtn = "23039499";
        } else {
            rtn = "23039499";
        }
        return rtn;
    }

    /**
     * 获取渠道号
     * @return
     */
    public static String getChannel() {
        String  channel = getInstance().channel;
        return channel;
    }

    /**
     * 获取渠道名字
     * @return
     */
    public static String getChannelName() {
        String channelName = getInstance().channelName;
        return channelName;
    }

    /**
     * 获取AppID
     * @return
     */
    public static String getAppId() {
        String rtn = getAppKey() + "@tvtaobao_android";
        log("getAppId=" + rtn);
        return rtn;
    }

    /**
     * 设置渠道号
     * @param channel
     */
    public static void setChannel(String channel) {
        getInstance().channel = channel;
        getInstance().ttid = getInstance().channel + "@tvtaobao_android_" + AppInfo.getAppVersionName();
        log("setChannel:" + getInstance().channel+","+getInstance().ttid );
    }

    /**
     * 获取TTid
     * @return
     */
    public static String getTTid() {
        String ttid = getInstance().ttid;
        log( "getTTid:" + ttid);
        return ttid;
    }

    /**
     * 验证协议支付开关
     * @return
     */
    public static boolean isAgreementPay() {//d模式强制开启协议支付
        return true;//return !com.yunos.RunMode.isYunos() || agreementPay;
    }

    /**
     * @param debug the debug to set
     */
    public static void setDebug(boolean debug) {
        getInstance().debug = debug;
        log("setDebug =" + getInstance().debug );
    }

    /**
     * @return the RUN_MODE
     */
    public static RunMode getRunMode() {
        return getInstance().RUN_MODE;
    }

    /**
     * 验证调试开关
     * @return
     */
    public static boolean isDebug() {
        return getInstance().debug;
    }

    /**
     * 获得buildId
     * @return
     */
    public static String getBuildId() {
        return getInstance().buildId;
    }

    /**
     * 获得MtopApi版本
     * @return
     */
    public static String getMtopApiVersion() {
        if ("default".equals(getInstance().mtopApiVersion))
            return "";
        else
            return getInstance().mtopApiVersion;
    }

    /**
     * 获取盒子设备信息
     * @param context
     * @return
     */
    public static String getModelInfo(Context context) {
        if (context == null) {
            return "";
        }
        JSONObject jsonObject = new JSONObject();
        try {
            double ran = getAvailMemory(context) / 1000;
            BigDecimal c = new BigDecimal(ran);
            double d = c.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
            jsonObject.put("model", android.os.Build.MODEL);//产品型号
            jsonObject.put("ram", d + "GB");//存储
            jsonObject.put("cpu", android.os.Build.CPU_ABI + "，" + getMaxCpuFreq() + "GHz*");//cpu
            jsonObject.put("display", android.os.Build.DISPLAY);//版本显示
            jsonObject.put("baseband_ver", getBaseband_Ver());//基带版本
            jsonObject.put("brand", android.os.Build.BRAND);//系统定制商
            jsonObject.put("device", android.os.Build.DEVICE);//设备参数
            jsonObject.put("codename", android.os.Build.VERSION.CODENAME);//开发代号
            jsonObject.put("sdk_int", android.os.Build.VERSION.SDK_INT);//SDK版本号
            jsonObject.put("hardware", android.os.Build.HARDWARE);//硬件类型
            jsonObject.put("host", android.os.Build.HOST);//主机
            jsonObject.put("id", android.os.Build.ID);//生产ID
            jsonObject.put("manufacturer", android.os.Build.MANUFACTURER);//ROM制造商

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject == null ? "" : jsonObject.toString();
    }

    /**
     * 获取设备基带版本
     */
    public static String getBaseband_Ver() {
        String Version = "";
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[]{String.class, String.class});
            Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "no message"});
            Version = (String) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Version;
    }

    /**
     * CPU最大运行频率
     */
    public static double getMaxCpuFreq() {
        String result = "";
        ProcessBuilder cmd;
        try {
            String[] args = {"/system/bin/cat",
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            result = "N/A";
        }
        double a = Integer.parseInt(result.trim());
        double b = a / 1000000;
        BigDecimal c = new BigDecimal(b);
        double d = c.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return d;
    }

    /**
     * 获取android当前可用内存大小
     */
    private static double getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        double d = 0;
        try {
            am.getMemoryInfo(mi);
            d = (double) mi.totalMem / (1024 * 1024);
        } catch (NoSuchFieldError e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * 获取设备umtoken
     */
    public static String getUmtoken(Context context) {
        String umidToken = "";
        try {
            umidToken = SecurityGuardManager.getInstance(context).getUMIDComp().getSecurityToken(0);
        } catch (SecException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return umidToken;
    }

    /**
     * 获取设备wua
     */
    public static String getWua(Context context) {
        String wua = "";
        try {
            wua = SecurityGuardManager.getInstance(context).getSecurityBodyComp().getSecurityBodyDataEx(String.valueOf(System.currentTimeMillis()), getAppKey(), null, null, 0x0, 0);
        } catch (SecException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wua;
    }

    /**
     * 获取设备是否是模拟器
     */
    public static boolean isSimulator(Context context) {
        boolean isSimulator = false;
        try {
            isSimulator = SecurityGuardManager.getInstance(context).getSimulatorDetectComp().isSimulator();
        } catch (SecException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSimulator;
    }

    /**
     * 获取设备系统
     */
    public static String getAndroidSystem(Context context) {
        Log.e("ljyljyljy","android "+Build.VERSION.RELEASE);
        return "android "+Build.VERSION.RELEASE;
    }

}
