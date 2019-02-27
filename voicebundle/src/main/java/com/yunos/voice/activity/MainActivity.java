package com.yunos.voice.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.tvtaobao.voicesdk.dialogs.NoviceGuideDialog;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.activity.BaseActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *     author : pan
 *     e-mail : panbeixing@zhiping.tech
 *     time   : 2017/11/21
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class MainActivity extends BaseActivity {
    private Map<String, Class> mSelfActivityMap;

    /* 判断应用的指定模块的关键字 */
    private static final String INTENT_KEY_MODULE = "module";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        deal(intent.getData());
    }

    private boolean deal(Uri uri) {
        if (uri == null) {
            openFail("打开页面失败");
            return false;
        }

        Bundle bundle = decodeUri(uri);
        if (bundle == null) {
            openFail("");
            return false;
        }

        return gotoSelfAppActivity(bundle);

    }

    /**
     * 启动自己的本身应用的activity
     *
     * @param bundle
     * @return boolean
     */
    private boolean gotoSelfAppActivity(Bundle bundle) {
        initSelfActivityMap();
        String selfModule = bundle.getString(INTENT_KEY_MODULE);
        if (Config.isDebug()) {
            AppDebug.i(TAG, TAG + ".gotoSelfAppActivity bundle=" + bundle + ".selfModule " + selfModule);
        }

        if (selfModule != null) {
            Intent intent = null;
            Class activityClass = mSelfActivityMap.get(selfModule);
            if (activityClass != null) {
                intent = new Intent();
                intent.setClass(this, activityClass);
            }
            if (intent != null) {
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return true;
            } else {
                openFail("NoIntent");
                return false;
            }
        } else {
            openFail("NoSelfModule");
            return false;
        }
    }

    /**
     * 打开失败
     *
     * @param failReason
     */
    private void openFail(String failReason) {
        Toast.makeText(this, failReason, Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 解析url地址
     *
     * @param uri
     * @return
     */
    private Bundle decodeUri(Uri uri) {
        if (Config.isDebug()) {
            AppDebug.i(TAG, TAG + ".decodeUri uri=" + uri.toString());
        }
        String query = uri.getEncodedQuery();
        if (query == null) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            Set<String> params = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                params = uri.getQueryParameterNames();
            }
            for (String key : params) {
                String value = uri.getQueryParameter(key);
                bundle.putString(key, value);
                if (Config.isDebug()) {
                    AppDebug.i(TAG, TAG + ".decodeUri key=" + key + " value=" + value);
                }
            }
            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 初始化自己主模块的activity
     */
    private void initSelfActivityMap() {
        if (mSelfActivityMap == null) {
            mSelfActivityMap = new HashMap<>();

            mSelfActivityMap.put("createorder", CreateOrderActivity.class);
            mSelfActivityMap.put("search", VoiceSearchActivity.class);
            mSelfActivityMap.put("voicevideo", VoiceVideoDemoActivity.class);
            mSelfActivityMap.put("guide", NoviceGuideActivity.class);
        }
    }
}
