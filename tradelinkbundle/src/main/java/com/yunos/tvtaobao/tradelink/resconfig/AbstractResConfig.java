package com.yunos.tvtaobao.tradelink.resconfig;


import android.content.Context;

public abstract class AbstractResConfig implements IResConfig {

    protected Context mContext;
    protected boolean canBuy        = true;
    protected boolean isGreenStatus = true;

    public AbstractResConfig(Context context) {
        this.mContext = context;
    }

    @Override
    public void setCanBuy(boolean canBuy) {
        // TODO Auto-generated method stub
        this.canBuy = canBuy;
    }

    public void setGreenStatus(String buyStatus) {
        if ("即将开始".equals(buyStatus)) {
            isGreenStatus = true;
        } else {
            isGreenStatus = false;
        }

    }
}
