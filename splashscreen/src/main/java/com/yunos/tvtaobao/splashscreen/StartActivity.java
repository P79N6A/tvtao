package com.yunos.tvtaobao.splashscreen;

import android.content.Intent;
import android.os.Bundle;

import com.yunos.tv.core.common.AppDebug;

public class StartActivity extends LoadingActivity {

//    @Override
//    protected boolean isTbs() {
//        return true;
//    }
//
//    @Override
//    public Map<String, String> getPageProperties() {
//        return super.getPageProperties();
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null)
            AppDebug.d(TAG, "intent:" + intent.getAction() + intent.getComponent() + intent.toString());
        else
            AppDebug.d(TAG, "intent is null");
        super.onCreate(savedInstanceState);

    }
}
