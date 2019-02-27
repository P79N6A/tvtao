package com.yunos.tvtaobao.juhuasuan.activity;


import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.util.SystemUtil;
import com.yunos.tvtaobao.juhuasuan.util.Utils;

import java.util.List;

/**
 * 基础Activity，用户页面统计
 * @author hanqi
 */
public abstract class JuBaseActivity extends BaseActivity {

    private final String TAGS = "JuBaseActivity[" + TAG + "]";

    private static int numActivity = 0;
    private boolean isCreate = false;
    private boolean isFromHomeCateActivity = false;
    private boolean isBackToHome = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        numActivity++;
        isCreate = true;
        AppDebug.i(TAGS, TAGS + ".onCreate numActivity=" + numActivity);
        Intent intent = getIntent();
        isFromHomeCateActivity = intent.getBooleanExtra(HomeActivity.INTENT_FROM_HOME_CATE_ACTIVITY, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        AppDebug.i(TAGS, TAGS + "onbackpress isFromHomeCateActivity:" + isFromHomeCateActivity + "isBackToHome:"
                + isBackToHome);
        if (SystemConfig.DIPEI_BOX && isFromHomeCateActivity && isBackToHome) {
            Intent intent = new Intent();
            intent.setData(Uri.parse("tvtaobao://juhuasuan?app=juhuasuan&type=home&" + CoreIntentKey.URI_FROM + "="
                    + getmFrom() + "&" + CoreIntentKey.URI_HUODONG + "=" + getmHuoDong() + "&"
                    + CoreIntentKey.URI_FROM_APP + "=" + getmApp()));
            Utils.setInnerActivityIntent(intent);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    public void setBackToHome(boolean isBackToHome) {
        this.isBackToHome = isBackToHome;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SystemUtil.initMagnification();
    }

    @Override
    protected void onDestroy() {
        AppDebug.i(TAGS, TAGS + ".onDestroy this=" + this + ", " + this.getClass());
        if (isCreate) {
            numActivity--;
        }
        AppDebug.i(TAGS, TAGS + ".onDestroy numActivity=" + numActivity);

        NetWorkCheck.unRegisterReceiver(this);

        if (numActivity <= 0) {
            AppDebug.i(TAGS, TAGS + ".onDestroy killProcess is running from positio 1");
            super.onDestroy();
            //            AppHolder.killProcess();
            return;
        } else {
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasklist = am.getRunningTasks(100);
            ActivityManager.RunningTaskInfo runTask = tasklist.get(0);
            if (runTask.numActivities < 1
                    || (!runTask.baseActivity.getPackageName().equals(this.getPageName()) && runTask.numActivities <= 1)) {
                AppDebug.i(TAGS, TAGS + ".onDestroy killProcess is running from positio 2 baseActivity="
                        + runTask.baseActivity);
                super.onDestroy();
                //                AppHolder.killProcess();
                return;
            }
        }

        // 下面这两者 如果具体要查看一下生命周期
        // 清理网络下载的线程池
        //        AsyncDataLoader.purge();

        System.gc();

        super.onDestroy();
    }

    @Override
    protected void onStartActivityNetWorkError() {
        NetWorkCheck.netWorkError(this);
    }


    protected String getAppTag() {
        return "Ju";
    }

    @Override
    protected String getAppName() {
        return "tvtaobao";
    }

    public void afterApiLoad(boolean success, String errorMsg, Object obj) {

    }

}
