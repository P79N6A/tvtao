package com.yunos.tvtaobao.tradelink.activity;


import android.os.Bundle;

import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tvtaobao.biz.TradeBaseActivity;
import com.yunos.tvtaobao.tradelink.R;

import java.util.Map;

public class AddressEditActivity extends TradeBaseActivity {

    private String TAG = "AddressEdit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ytsdk_activity_address_edit);
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    public Map<String, String> getPageProperties() {

        Map<String, String> p = super.getPageProperties();
        p.put(SPMConfig.SPM_CNT, SPMConfig.ADDRESS_EDIT_SPM+".0.0");
        return p;
    }
    @Override
    protected void refreshData() {
        //TODO Auto-generated method stub
        
    }

}
