package com.yunos.tvtaobao.splashscreen;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;

public class RedirectActivity extends Activity {
    private static final String TAG = RedirectActivity.class.getSimpleName();
    private boolean pauseToFinish = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intentValue = getIntent();
        if (intentValue == null) {
            return;
        }
        if (processIntent(intentValue)) {
            pauseToFinish = true;
            startActivity(intentValue);
        } else {
            finish();
        }
    }

    private boolean processIntent(Intent intent) {
        if (intent == null)
            return false;
        AppDebug.d(TAG, "intent:" + intent.getAction() + intent.getComponent() + intent.toString());
        intent.setPackage(getPackageName());
        if (intent.getData() != null) {
            intent.setComponent(null);
            return true;
        } else if ("com.tvtaobao.action.StartApp".equals(intent.getAction())) {
            String uri = intent.getStringExtra("uri");
            AppDebug.d(TAG, "uri is " + uri);
            if (!TextUtils.isEmpty(uri)) {
                Uri actionUri = Uri.parse(uri);
                intent.setAction(Intent.ACTION_VIEW);
                intent.removeExtra("uri");
                intent.setComponent(null);
                intent.setData(actionUri);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pauseToFinish) {
            finish();
        }
    }
}
