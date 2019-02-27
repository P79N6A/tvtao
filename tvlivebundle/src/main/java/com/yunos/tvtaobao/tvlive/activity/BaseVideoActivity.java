package com.yunos.tvtaobao.tvlive.activity;

import android.os.Bundle;
import android.view.WindowManager;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;

/**
 * Created by huangdaju on 17/7/20.
 */

public class BaseVideoActivity extends BaseActivity {
    private static final String TAG = "BaseVideoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDebug.d(TAG,"class name " + this.getClass().getName());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        onKeepActivityOnlyOne(this.getClass().getName());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRemoveKeepedActivity(this.getClass().getName());
    }
}
