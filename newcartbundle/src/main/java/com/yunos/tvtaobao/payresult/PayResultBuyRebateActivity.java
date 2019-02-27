package com.yunos.tvtaobao.payresult;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.newcart.R;

/**
 * Created by LJY on 18/9/6.
 */

public class PayResultBuyRebateActivity extends Activity{

    private LinearLayout pay_result_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result_buy_rebate);

        pay_result_layout= (LinearLayout) findViewById(R.id.pay_result_layout);
        pay_result_layout.requestFocus();
        pay_result_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (GlobalConfig.instance != null && GlobalConfig.instance.coupon.isShowH5Coupon()) {
                    String page = PageMapConfig.getPageUrlMap().get("coupon");
                    if (!TextUtils.isEmpty(page)) {
                        intent.setData(Uri.parse("tvtaobao://home?module=common&page=" + page));
                    } else {
                        intent.setClassName(PayResultBuyRebateActivity.this.getBaseContext(), BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                    }
                } else {
                    intent.setClassName(PayResultBuyRebateActivity.this.getBaseContext(), BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                }
                startActivity(intent);
                finish();
            }
        });
    }
}
