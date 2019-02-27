package com.yunos.tvtaobao.h5.commonbundle.activity;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.util.StringUtil;

import java.net.URLDecoder;

public class RecommendActivity extends TaoBaoBlitzActivity {

    // 进入页面类别，"0"精选分类界面,"1"淘系应用界面
    private String mCategory = "0";
    private String page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCategory = IntentDataUtil.getString(getIntent(), "category", "0");
        page = IntentDataUtil.getString(getIntent(), "page", null);
        AppDebug.i(TAG, TAG + ".onCreate --> page 1 = " + page + ", mCategory = " + mCategory);

        if (TextUtils.isEmpty(page)) {
            page = PageMapConfig.getPageUrlMap() == null ? "" : PageMapConfig.getPageUrlMap().get("recommend");
            if (TextUtils.isEmpty(page)) {
                Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_found_page),
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            } else {
                page = page + "?category=" + mCategory;
            }
        } else {
            page = URLDecoder.decode(page);
        }

        AppDebug.i(TAG, TAG + ".onCreate --> page 2 = " + page);

        page = PageUtil.processPageUrl(page);
        //构建语音路径
        page = buildVoiceUrl(page, getIntent());

        if (!StringUtil.isInnerUrl(page)) {
            Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_invalid_h5_url), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        onInitH5View(page);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public String getPageName() {
        String pageName = super.getPageName();
        pageName += "_" + mCategory;
        return pageName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void interceptBack(boolean isIntercept) {

    }
}
