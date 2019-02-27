package com.yunos.tvtaobao.h5.commonbundle.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.util.StringUtil;

import java.net.URLDecoder;

public class OrderListActivity extends TaoBaoBlitzActivity {

    private final String TAG = "Orders";
    private String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TvOptionsConfig.setTvOptionVoiceSystem(getIntent().getStringExtra(CoreIntentKey.URI_FROM));
        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.ORDER);

        page = IntentDataUtil.getString(getIntent(), "page", null);
        AppDebug.i(TAG, TAG + ".onCreate --> page 1 = " + page);

        if (TextUtils.isEmpty(page)) {
            page = PageMapConfig.getPageUrlMap().get("orderlist");
        } else {
            page = URLDecoder.decode(page);
        }

        if (TextUtils.isEmpty(page)) {
            Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_found_page), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        page = PageUtil.processPageUrl(page);
        //构建语音路径
        page = buildVoiceUrl(page,getIntent());

        AppDebug.i(TAG, TAG + ".onCreate --> page 2 = " + page);

        if (!StringUtil.isInnerUrl(page)) {
            Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_invalid_h5_url), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        onInitH5View(page);
    }

    @Override
    public String getPageName() {
        return "Orders";
    }

    @Override
    protected String getAppTag() {
        return "Tb";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void interceptBack(boolean isIntercept) {

    }
}
