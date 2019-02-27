package com.yunos.tvtaobao.homebundle.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.yunos.RunMode;
import com.yunos.alitvcompliance.TVCompliance;
import com.yunos.alitvcompliance.types.RetCode;
import com.yunos.alitvcompliance.types.RetData;
import com.yunos.tv.blitz.activity.BzBaseActivity;
import com.yunos.tv.blitz.view.BlitzBridgeSurfaceView;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.DebugTestBuilder;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tv.lib.SystemProUtils;
import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.businessview.BuildConfig;
import com.yunos.tvtaobao.homebundle.R;
import com.yunos.tvtaobao.homebundle.config.BaseConfig;
import com.yunos.tvtaobao.homebundle.detainment.DetainMentDialog;
import com.yunos.tvtaobao.homebundle.detainment.DetainmentDataBuider;
import com.yunos.tvtaobao.homebundle.h5.plugin.EventBlitz;
import com.yunos.tvtaobao.homebundle.h5.plugin.GuessYouLikePlugin;
import com.yunos.tvtaobao.homebundle.h5.plugin.UpdatePlugin;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by huangdaju on 17/5/23.
 */

public class HomeActivity extends TaoBaoBlitzActivity {

    private boolean mIsFromOutside;
    private DetainMentDialog mDetainMentDialog;
    private boolean isDestroy = false;
    private DetainmentDataBuider detainmentDataBuider;

    private EventBlitz blitz;
    private boolean isIntercept = false;


    private DebugTestBuilder testBuilder;

    private UpdatePlugin mUpdatePlugin;

    private GuessYouLikePlugin guessYouLikePlugin;

//    private HomeVideoDialog mHomeVideoDialog;
//    private VideoDialogPresenter mVideoDialogPresenter;

    private String TAG = "HomeActivity";

    private int mBackFromLikeRequestCode = 777; //猜你喜欢返回的request code

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Toast.makeText(this, Environment.getInstance().isYunos() + "", Toast.LENGTH_LONG).show();
        registerLoginListener();
//        if (GlobalConfig.instance == null)
//            BusinessRequest.getBusinessRequest().requestGlobalConfig(new RequestListener<GlobalConfig>() {
//                @Override
//                public void onRequestDone(GlobalConfig data, int resultCode, String msg) {
//                    AppDebug.d(TAG, "GlobalConfig resultCode=" + resultCode + "data: " + data.toString());
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            loadPage();
//                        }
//                    });
//                }
//            });
//        else
        loadPage();

        if (Config.isDebug()) {
            testBuilder = new DebugTestBuilder(this);
        }

        mUpdatePlugin = new UpdatePlugin(new WeakReference<HomeActivity>(this));
        guessYouLikePlugin = new GuessYouLikePlugin(new WeakReference<HomeActivity>(this));
    }

    private void loadPage() {
        String page = "";
        String gatedUrl = getIntent().getStringExtra(BaseConfig.INTENT_HOME_PAGE_URL);
        AppDebug.v(TAG, TAG + ".onCreate.gatedUrl = " + gatedUrl);

        //TODO  此处可能需要加上域名转换和检测
        if (!TextUtils.isEmpty(gatedUrl)) {
            page = gatedUrl;
        } else {
            String cacheUrl = SharePreferences.getString(BaseConfig.INTENT_HOME_PAGE_URL);
            if (!TextUtils.isEmpty(cacheUrl)) {
                page = cacheUrl;
            }
        }


        if (TextUtils.isEmpty(page)) {
            page = PageMapConfig.getPageUrlMap().get("home");
//            page += "?wh_showError=true&wh_appkey=2016092423"; //预发环境参数
            //NOTE: 这里之前添加参数的方式不严谨，导致后续页面上解析appVersionCode参数时失败！
        }

        String appkey = SharePreferences.getString("device_appkey", "");
        String brandName = SharePreferences.getString("device_brandname", "");
        StringBuilder params = new StringBuilder();
        if(page!=null){
            if (page.contains("?")) {
                params.append("&");
            } else {
                params.append("?");
            }
        }else {
            page="";
        }



        if (TextUtils.isEmpty(appkey)) {
            params.append("wh_appkey=");
            params.append(Config.getChannel());
        } else {
            params.append("wh_appkey=");
            params.append(appkey);
        }
        if (RunMode.isYunos()) {
            if (!TextUtils.isEmpty(brandName)) {
                params.append("&brand=");
                params.append(brandName);
            } else {
                params.append("&brand=null");
            }
        }
        boolean beta = GlobalConfig.instance != null && GlobalConfig.instance.isBeta();
        if (beta) {
            params.append("&wh_beta=true");
            params.append("&wh_version=" + AppInfo.getAppVersionName());
        }

        page += params.toString();

//        AppDebug.d(TAG, " pag " + page);

        if (TextUtils.isEmpty(page)) {
            Toast.makeText(this.getApplicationContext(), getString(R.string.ytbv_not_found_page), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }
        page = getCompliancePage(page);

        Bundle bundle = getIntent().getExtras();
        mIsFromOutside = false;
        if (bundle != null) {
            mIsFromOutside = bundle.getBoolean(CoreActivity.INTENT_KEY_IS_FROM_OUTSIDE, false);
        }

        AppDebug.v(TAG, TAG + ".onCreate.page = " + page + ", mIsFromOutside = " + mIsFromOutside);
//        mHomeVideoPlugin = new HomeVideoPlugin(new WeakReference<HomeActivity>(this));
        blitz = new EventBlitz(new WeakReference<HomeActivity>(this));
//        Toast.makeText(getBaseContext(), "happy to you, everybody", Toast.LENGTH_SHORT).show();

        onInitH5View(page);

    }


    @Override
    protected void onResume() {
        super.onResume();
        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.OTHER);
//        if (mVideoDialogPresenter != null)
//            mVideoDialogPresenter.videoStart();
//        anaylisysTaoke(); //淘客首页打点(聚划算接口)
    }


    @Override
    protected void onPause() {
        super.onPause();
        unRegisterLoginListener();
    }

    @Override
    public void onPageLoadFinished(String param) {
        AppDebug.v(TAG, TAG + ".onCreate.loadfinish = " + param);
        super.onPageLoadFinished(param);
    }

    /**
     * 牌照管控补充，当sdk无法转换某些新域名时在此转换，目前只处理tvos.taobao.com
     *
     * @param page
     * @return
     */
    protected String getCompliancePage(String page) {
        if (!com.yunos.RunMode.isYunos()) {
            return page;
        }
        String host = Utils.parseHost(page);
        RetData retData = null;
        if (!TextUtils.isEmpty(host))
            retData = TVCompliance.getComplianceDomain(host);
        if (retData == null) {
            return page;
        }
        AppDebug.d(TAG, "Converted domain host " + host);
        AppDebug.d(TAG, "Converted domain is " + retData.toString());
        if (retData.getCode() == RetCode.Success || retData.getCode() == RetCode.Default) {
            AppDebug.d(TAG, "Converted domain is " + retData.getResult());
            String domainName = retData.getResult();
            String replace = page.replace(host, domainName);
            AppDebug.d(TAG, "Original page is " + page);
            AppDebug.d(TAG, "replace page is " + replace);
            if (!domainName.equals(host)) {
                return replace.replaceFirst("http://", "https://");
            }
            return replace;
        } else {
            AppDebug.d(TAG, "Original domain is " + retData.getResult());
            String license = SystemProUtils.getLicense();
            String domainName = host;
            if ("tvos.taobao.com".equals(host)) {
                if ("1".equals(license)) {//wasu
                    domainName = "tb.cp12.wasu.tv";
                }
                if ("7".equals(license)) {//icintv
                    domainName = "tb.cp12.ott.cibntv.net";
                }
            }
            if (!domainName.equals(host)) {
                return page.replace(host, domainName).replaceFirst("http://", "https://");
            }
            return page.replace(host, domainName);
        }
    }


        /**
         * 防止surfaceView 具有top属性 and 加入前景色
         *
         * @param url
         */
        @Override
        public void loadWithUrl (String url){
            super.loadWithUrl(url);
            try {
                Field field = BzBaseActivity.class.getDeclaredField("mBlitzBridgeView");
                field.setAccessible(true);
                BlitzBridgeSurfaceView blitzBridgeSurfaceView = (BlitzBridgeSurfaceView) field.get(HomeActivity.this);
                blitzBridgeSurfaceView.setZOrderOnTop(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 防止surfaceView 具有top属性 and 加入前景色
         *
         * @param url
         */
        @Override
        public void loadWithUrlAndPagedata (String url, String pagedata){
            super.loadWithUrlAndPagedata(url, pagedata);
            try {
                Field field = BzBaseActivity.class.getDeclaredField("mBlitzBridgeView");
                field.setAccessible(true);
                BlitzBridgeSurfaceView blitzBridgeSurfaceView = (BlitzBridgeSurfaceView) field.get(HomeActivity.this);
                blitzBridgeSurfaceView.setZOrderOnTop(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * 自定义聚划算列表页打点
         */

    private void anaylisysTaoke() {
        if (CoreApplication.getLoginHelper(this).isLogin()) {
            String stbId = DeviceUtil.initMacAddress(this);
            BusinessRequest.getBusinessRequest().requestTaokeJHSListAnalysis(stbId, User.getNick(), null);
        }
    }

    @Override
    public void onWebviewPageDone(String url) {
        AppDebug.d(TAG, "homeActivity onWebviewPageDone " + url);
        if (mDetainMentDialog == null && !isDestroy) {
            mDetainMentDialog = new DetainMentDialog(this);
        }

        initDetainment();
        anaylisysTaoke();
    }


    @Override
    public void interceptBack(boolean isIntercept) {
        this.isIntercept = isIntercept;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(("DLT").equals(BuildConfig.BUILD_NO)){//大连天途渠道屏蔽 183-186 四个按键
            if(event.getKeyCode() == KeyEvent.KEYCODE_PROG_RED||event.getKeyCode() == KeyEvent.KEYCODE_PROG_GREEN||event.getKeyCode() == KeyEvent.KEYCODE_PROG_YELLOW||event.getKeyCode() == KeyEvent.KEYCODE_PROG_BLUE){
                return true;
            }
        }
        if (keyCode == KeyEvent.KEYCODE_BACK && (!isIntercept)) {
            if (!Utils.isFastClick()) {
                enterDetainMent();
            }
            return true;
        }
        //debug版本专用
        if (Config.isDebug() && testBuilder != null) {
            AppDebug.d(TAG, "keyCode +++ aciton");
            testBuilder.onKeyAction(keyCode);
        }
        isIntercept = false;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && (!isIntercept)) {
            return true;
        }
        isIntercept = false;
        return super.onKeyUp(keyCode, event);
    }

    private boolean enterDetainMent() {
        AppDebug.i(TAG, "enterDetainMent --> isFinishing = " + isFinishing());
        if (mDetainMentDialog != null && !mDetainMentDialog.hasData()) {
            // 如果没有数据，则直接退出
            handleExit();
        } else {
            if (!isDestroy && mDetainMentDialog != null && !mDetainMentDialog.isShowing()) {
                mDetainMentDialog.show();
            } else {
                handleExit();
            }
        }
        return true;
    }

    public void handleExit() {
        if (mDetainMentDialog != null) {
            mDetainMentDialog.onDestroy();
        }

        if (mDetainMentDialog != null && mDetainMentDialog.isShowing()) {
            if (mDetainMentDialog.isShowing()) {
                mDetainMentDialog.dismiss();
            }
        }

        exit();
    }

    /**
     * 最终退出的处理
     */
    private void exit() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        AppDebug.i(TAG, "exit --> mIsFromOutside = " + mIsFromOutside);
        // 如果是外部调用的话不做清理工作,但在低内存设备上也要清理
        if (!mIsFromOutside || !DeviceJudge.MemoryType.HighMemoryDevice.equals(DeviceJudge.getMemoryType())) {
            AppDebug.i(TAG, "exit --> killProcess ");
            clearAllOpenedActivity(HomeActivity.this);
        }

        finish();
        if (exitHandler != null) {
            exitHandler.removeCallbacksAndMessages(null);
            exitHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    exitChildProcess();
                    CoreApplication.getApplication().clear();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }, 1000);
        }
//        android.os.Process.killProcess(android.os.Process.myPid());

    }


    @Override
    protected void onDestroy() {
        isDestroy = true;

        if (Config.isDebug() && testBuilder != null) {
            testBuilder.onDistroy();
            testBuilder = null;
        }
        super.onDestroy();
    }

    @Override
    protected boolean isTbs() {
        return false;
    }

    /**
     * 初始化detain
     */
    private void initDetainment() {
        if (detainmentDataBuider == null) {
            detainmentDataBuider = new DetainmentDataBuider(this);
            detainmentDataBuider.checkDetainmentData();
        }
    }


    public void showVideo(String url) {
//        if (mHomeVideoDialog == null) {
//            //1.创建videoDialog；2.显示视频
//        }
    }

    public void dismissForJs() {

    }

    public void showVideoForJs() {
//        if (mHomeVideoDialog == null) {
//            //1.创建videoDialog；2.获取服务端视频数据；3.加载视频
//            mHomeVideoDialog = new HomeVideoDialog(this);
//        }
//        if (mVideoDialogPresenter == null) {
//            mVideoDialogPresenter = new VideoDialogPresenterImpl(this, mHomeVideoDialog);
//        }
//        mVideoDialogPresenter.loadVideo(3, this);

    }

    @Override
    protected void initBlitzContext(String initStr, int type) {
        super.initBlitzContext(initStr, type);
        ArrayList<BzBaseActivity> activityArrayList = null;
        Class clz = BzBaseActivity.class;
        try {
            Field field = clz.getDeclaredField("ActivityList");
            field.setAccessible(true);
            activityArrayList = (ArrayList<BzBaseActivity>) field.get(null);
            activityArrayList.remove(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void fullScreenForJs(int liveType) {
//        if (mHomeVideoDialog != null && mVideoDialogPresenter != null) {
//            mVideoDialogPresenter.dismiss();
//            mVideoDialogPresenter.fullscreenVideo();
//        }
    }

    public void changeVideoSizeForJS(int width, int height, int marginTop, int marginLeft) {
//        if (mHomeVideoDialog == null) {
//            //1.创建videoDialog；2.获取服务端视频数据；3.加载视频
//            mHomeVideoDialog = new HomeVideoDialog(this);
//        }
//        if (mVideoDialogPresenter == null) {
//            mVideoDialogPresenter = new VideoDialogPresenterImpl(this, mHomeVideoDialog);
//        }
//        mVideoDialogPresenter.changeVideoDialogSize(width, height, marginTop, marginLeft);
    }

    public void changeLiveType(int liveType) {

    }

    @Override
    public boolean isHomeActivity() {
        return true;
    }

    public void homeToGuessYouLike(String bgUrl){
        Intent intent = new Intent();
        intent.setClassName(HomeActivity.this, com.yunos.tvtaobao.biz.common.BaseConfig.SWITCH_TO_GUESS_YOU_LIKE_ACTIVITY);
        intent.putExtra("guess_like_from","home");
        intent.putExtra("guess_like_bg_url",bgUrl);
        startActivityForResult(intent,mBackFromLikeRequestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
         if(requestCode == mBackFromLikeRequestCode && resultCode == RESULT_OK){
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}
