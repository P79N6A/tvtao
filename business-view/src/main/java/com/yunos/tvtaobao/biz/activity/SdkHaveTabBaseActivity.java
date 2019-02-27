package com.yunos.tvtaobao.biz.activity;


import android.os.Bundle;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageHandleManager;

public abstract class SdkHaveTabBaseActivity extends TabBaseActivity {

    private final String BASETAG = "TbBaseActivity";
    protected final String TAOBAO_SDK_TV_COUPON_KEYWORD = "TVHongbao";

    // 主要是用来检查此Activity 是否释放
    protected boolean isDestroyActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppDebug.d(BASETAG, "TAG  ---> " + getTag() + ";   ---- >  onCreate;  this =  " + this);
        isDestroyActivity = false;
    }

    @Override
    protected void onStartActivityNetWorkError() {
        showNetworkErrorDialog(false);
    }

    public boolean isHasDestroyActivity() {
        return isDestroyActivity;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppDebug.d(BASETAG, "TAG  ---> " + getTag() + ";   ---- >  onPause;  this =  " + this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDebug.d(BASETAG, "TAG   ---> " + getTag() + ";   ---- >   onDestroy;  this =  " + this);

        //移除网络重连的监听
        removeNetworkOkDoListener();

        isDestroyActivity = true;

        // 清理图片处理的线程池
        ImageHandleManager.getImageHandleManager(getApplicationContext()).purge();

    }

    /**
     * tbsdk子应用页面开头，用于TBS统计
     */
    protected String getAppTag() {
        return "Tb";
    }


    protected abstract String getTag();
}
