package com.yunos.tvtaobao.h5.commonbundle.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.util.StringUtil;

import java.net.URLDecoder;

public class RelativeRecommendActivity extends TaoBaoBlitzActivity {

    private final String TAG = "RelativeRecommend";
    private String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String itemId = IntentDataUtil.getString(getIntent(), "itemId", null);
        String sellerId = IntentDataUtil.getString(getIntent(), "sellerId", null);
        page = IntentDataUtil.getString(getIntent(), "page", null);

        AppDebug.v(TAG, TAG + ".onCreate.itemId = " + itemId + ".sellerId = " + sellerId + ", page = " + page);

        if (TextUtils.isEmpty(page)) {
            page = PageMapConfig.getPageUrlMap().get("relativerecommend");
            AppDebug.i(TAG, TAG + ".onCreate --> relativerecommend = " + page);
            if (TextUtils.isEmpty(page) || TextUtils.isEmpty(itemId) || TextUtils.isEmpty(sellerId)) {
                Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_found_page),
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else {
                page = page + "?itemId=" + itemId + "&sellerId=" + sellerId;
            }
        } else {
            page = URLDecoder.decode(page);
        }

        AppDebug.i(TAG, TAG + ".onCreate --> page = " + page);

        page = PageUtil.processPageUrl(page);
        //构建语音路径
        page = buildVoiceUrl(page,getIntent());
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
        return TAG;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void interceptBack(boolean isIntercept) {

    }
}
