package com.yunos.tvtaobao.h5.commonbundle.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.util.StringUtil;

/**
 * Created by huangdaju1 on 17/6/27.
 */

public class ShopBlizActivity extends TaoBaoBlitzActivity {

    private final String TAG = "ShopBliz";
    private String page;

    private boolean mIsBackHome;

    // 卖家id
    private String mSellerId;
    // 店铺类型 淘宝 or 天猫
    private String mShopType = BaseConfig.SELLER_TAOBAO;
    // shopid
    private String mShopId;
    //进入来源,为空表示店铺按钮,如果不是从店铺按钮进来,默认光标在店铺优惠券上,未实现
    private String shopFrom = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(OrderListActivity.class.getName());

        Intent intent = this.getIntent();
        if (intent == null) {
            return;
        }

        mShopType = intent.getStringExtra(BaseConfig.SELLER_TYPE);
        mSellerId = intent.getStringExtra(BaseConfig.SELLER_NUMID);
        mShopId = intent.getStringExtra(BaseConfig.SELLER_SHOPID);
        shopFrom = intent.getStringExtra(BaseConfig.SHOP_FROM);
        mIsBackHome = isBackHome();
        AppDebug.i(TAG, TAG + ".onCreate --> page 1 = " + page);

        page = PageMapConfig.getPageUrlMap() == null ? "" : PageMapConfig.getPageUrlMap().get("shopbliz");
        StringBuilder params = new StringBuilder();
        if (page.contains("?")) {
            params.append("&");
        } else {
            params.append("?");
        }
        if (!TextUtils.isEmpty(mShopType)) {
            params.append("wh_type=");
            params.append(mShopType);
        }
        if (!TextUtils.isEmpty(mSellerId)) {
            params.append("&wh_sellerId=");
            params.append(mSellerId);
        }
        if (!TextUtils.isEmpty(mShopId)) {
            params.append("&wh_shopId=");
            params.append(mShopId);
        }
        if (!TextUtils.isEmpty(shopFrom)) {
            params.append("&wh_shopFrom=");
            params.append(shopFrom);
        }

        if (TextUtils.isEmpty(page)) {
            Toast.makeText(this.getApplicationContext(), getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_found_page), Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        page += params.toString();
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
    public String getPageName() {
        return TAG;
    }

    @Override
    protected String getAppTag() {
        return "Tb";
    }

    @Override
    protected void onDestroy() {
        onRemoveKeepedActivity(OrderListActivity.class.getName());
        super.onDestroy();
    }


    @Override
    protected void handleBackPress() {
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

    @Override
    public void interceptBack(boolean isIntercept) {

    }
}
