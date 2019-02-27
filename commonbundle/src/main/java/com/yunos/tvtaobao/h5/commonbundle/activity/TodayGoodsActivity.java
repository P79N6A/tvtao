package com.yunos.tvtaobao.h5.commonbundle.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.util.StringUtil;

import java.net.URLDecoder;
import java.util.Map;

public class TodayGoodsActivity extends TaoBaoBlitzActivity {

    private final static String TAG = "TodayGoods";
    private String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //        String url = "http://www.taobao.com/wow/yunos/act/tbtoday";
        page = IntentDataUtil.getString(getIntent(), "page", null);
        AppDebug.i(TAG, TAG + ".onCreate --> page 1 = " + page);
        if (TextUtils.isEmpty(page)) {
            page = PageMapConfig.getPageUrlMap().get("todaygoods");
        } else {
            page = URLDecoder.decode(page);
        }

        if (TextUtils.isEmpty(page)) {
            Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_found_page), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        AppDebug.i(TAG, TAG + ".onCreate --> page 2 = " + page);

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
        return "Today_Goods";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        p.put(SPMConfig.SPM_CNT, "a2o0j.7984573.0.0");
        return p;
    }

    @Override
    public void interceptBack(boolean isIntercept) {

    }
}
