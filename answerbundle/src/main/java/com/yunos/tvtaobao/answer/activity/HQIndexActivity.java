package com.yunos.tvtaobao.answer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.util.StringUtil;

import java.net.URLDecoder;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/01/10
 *     desc   : 直播问答项目首页
 *     version: 1.0
 * </pre>
 */

public class HQIndexActivity extends TaoBaoBlitzActivity {
    private String page;
    private boolean mIsBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        page = IntentDataUtil.getString(getIntent(), "page", null);
        AppDebug.v(TAG, TAG + ".onCreate.page = " + page);
        if (TextUtils.isEmpty(page)) {
            Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_found_page), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        mIsBackHome = isBackHome();

        page = URLDecoder.decode(page);

        if (!Config.isDebug() && !StringUtil.isInnerUrl(page)) {
            Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_invalid_h5_url), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }
        onInitH5View(page);
    }

    @Override
    public void handleBackPress() {
        if (mIsBackHome) {
            String homeUri = "tvtaobao://home?module=main&" + CoreIntentKey.URI_FROM_APP + "=" + getAppName();
            Intent intent = new Intent();
            intent.putExtra(CoreActivity.INTENT_KEY_INHERIT_FLAGS, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setData(Uri.parse(homeUri));
            startActivity(intent);
            AppDebug.i(TAG, "onBackPressed mIsBackHome  = " + mIsBackHome + "; homeUri = " + homeUri + "; intent = "
                    + intent);
            return;
        }
        super.handleBackPress();
    }

    @Override
    public void interceptBack(boolean isIntercept) {

    }

    /**
     * 是否返回首页
     *
     * @return
     */
    protected boolean isBackHome() {
        boolean isbackhome = false;
        String isBackHomeValue = IntentDataUtil.getString(getIntent(), "isbackhome", null);
        String isJoin = IntentDataUtil.getString(getIntent(), CoreIntentKey.URI_JOIN, null);

        AppDebug.i(TAG, "isBackHome isBackHomeValue  = " + isBackHomeValue + " ,isJoin= " + isJoin);
        if (!TextUtils.isEmpty(isBackHomeValue)) {
            isbackhome = isBackHomeValue.toLowerCase().equals("true");
        }

        AppDebug.i(TAG, "isBackHome isbackhome = " + isbackhome);
        return isbackhome;
    }
}
