package com.yunos.tvtaobao.biz.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;

import com.tvtaobao.voicesdk.tts.TTSUtils;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tvtaobao.biz.TradeBaseActivity;
import com.yunos.tvtaobao.biz.blitz.TaobaoBzPageStatusListener;
import com.yunos.tvtaobao.biz.h5.plugin.AppInfoPlugin;
import com.yunos.tvtaobao.biz.h5.plugin.CouponPlugin;
import com.yunos.tvtaobao.biz.h5.plugin.TvTaoBaoBlitzPlugin;
import com.yunos.tvtaobao.biz.h5.plugin.TvTaoBaoPayPlugin;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.List;

/***
 * this Activity is Blitz(H5) parent
 */

public abstract class TaoBaoBlitzActivity extends TradeBaseActivity {

    protected TaobaoBzPageStatusListener.LOAD_MODE mLoadMode = TaobaoBzPageStatusListener.LOAD_MODE.URL_MODE;

    protected TvTaoBaoPayPlugin mPayPlugin;
    //    protected AndroidSharkForJs androidSharkForJs;
    protected CouponPlugin mCouponPlugin;
    //    protected AndroidEventForJS androidEventForJS;
    protected AppInfoPlugin mAppInfoPlugin;
    //protected AndroidAdvertisementForJs androidAdvertisementForJs;

    protected final static String appkey = "appkey";
    private final static String versionCodeKey = "appVersionCode";
    private final static String queryParamsFlag = "?";
    private final static String queryParamsConcatSign = "&";
    protected String from = "from";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GlobalConfig.instance == null || !GlobalConfig.instance.isBeta()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TvTaoBaoBlitzPlugin.register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        TvTaoBaoBlitzPlugin.unregister(this);
    }

    protected void registerPlugins() {
        AppDebug.e("TAG", "registerPlugins  : " + this);
//        androidEventForJS = new AndroidEventForJS(new WeakReference<TaoBaoBlitzActivity>(this));
        mPayPlugin = new TvTaoBaoPayPlugin(new WeakReference<TaoBaoBlitzActivity>(this));
        mCouponPlugin = new CouponPlugin(new WeakReference<TaoBaoBlitzActivity>(this));
        mAppInfoPlugin = new AppInfoPlugin(new WeakReference<TaoBaoBlitzActivity>(this));
    }

    /***
     * 初始化h5页面
     * @param url
     */
    protected void onInitH5View(String url) {
        String preparedUrl = prepareUrlParams(url);
        loadWithUrl(preparedUrl);
        setH5BackGroud();
        mLoadMode = TaobaoBzPageStatusListener.LOAD_MODE.URL_MODE;
        registerPlugins();
        OnWaitProgressDialog(true);
    }

    /*
     * 额外的给URL增加业务参数的方法，目前父类实现了appVersionCode参数的添加
     */
    protected String prepareUrlParams(String originUrl) {
        String newUrl = originUrl;
        try {
            if (shouldAppendVersionCode())
                newUrl = naiveAppendQueryParam(originUrl, versionCodeKey, Integer.toString(AppInfo.getAppVersionNum()));
        } catch (UnsupportedEncodingException exception) {
            exception.printStackTrace();
        } finally {
            return newUrl;
        }
    }

    /*
     * 简单的给URL添加Query参数的方法
     */
    protected String naiveAppendQueryParam(String originUrl, String queryKey, String queryValue) throws UnsupportedEncodingException {
        if (originUrl.contains(queryParamsFlag)) {
            if (originUrl.contains(queryKey)) {
                return originUrl;
            } else {
                //TODO:更好的拼接字符串
                return originUrl + queryParamsConcatSign + queryKey + "=" + URLEncoder.encode(queryValue, AppInfo.HTTP_PARAMS_ENCODING);
            }
        } else {
            return originUrl + queryParamsFlag + queryKey + "=" + URLEncoder.encode(queryValue, AppInfo.HTTP_PARAMS_ENCODING);
        }
    }

    protected boolean shouldAppendVersionCode() {
        return true;
    }

    public void loadDataForWeb(String data) {
        mLoadMode = TaobaoBzPageStatusListener.LOAD_MODE.DATA_MODE;
        loadWithData(data);
//        setH5BackGroud();
    }

    public void onPageLoadFinished(String param) {
        onWebviewPageDone(param);
    }

    public String buildVoiceUrl(String page, Intent intent) {
        from = intent.getStringExtra("from");
        String v_from = intent.getStringExtra("v_from");

        //因为h5暂时还不支持h5页面之前的from字段传参，所以这里客户端做了一个处理
        List<String> pathList = ActivityPathRecorder.getInstance().getCurrentPath(this);
        if (pathList != null) {
            for (int i = 0; i < pathList.size(); i++) {
                //i越小，activity越接近栈顶
                String currentPath = pathList.get(i);
                if (currentPath.contains("voice_system")) {
                    v_from = "voice_system";
                    break;
                }

                if (currentPath.contains("voice_application")) {
                    v_from = "voice_application";
                    break;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(page);
        if ("voice_system".equals(from) || "voice_application".equals(from)) {
            //native语音跳转
            if (page.contains("?")) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            sb.append("v_from=").append(from);
            TvOptionsConfig.setTvOptionVoiceSystem(from);
        } else if (!TextUtils.isEmpty(v_from)) {
            //h5跳h5
            if (page.contains("?")) {
                sb.append("&");
            } else {
                sb.append("?");
            }
            sb.append("v_from=").append(v_from);
            TvOptionsConfig.setTvOptionVoiceSystem(v_from);
        } else {
            TvOptionsConfig.setTvOptionVoiceSystem("");
        }
        page = sb.toString();
        return page;
    }

    /**
     * Webview加载完成的回调方法，供h5页面重写
     *
     * @param url
     */
    public void onWebviewPageDone(String url) {
        if ("voice_system".equals(from) || "voice_application".equals(from)) {
            TTSUtils.getInstance().showDialog(this, 1);
        }
    }

    @Override
    protected void onStartActivityNetWorkError() {

    }

    public void interceptBack(boolean isInterceptBack){

    }


    @Override
    protected boolean isTbs() {
        return true;
    }

    /**
     * 删除子进程（H5页面）
     */
    public void exitChildProcess() {
        String packageName = getPackageName();
        ActivityManager activityMgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityMgr != null && activityMgr.getRunningAppProcesses() != null && activityMgr.getRunningAppProcesses().size() > 0) {
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
    }
}