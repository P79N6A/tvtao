package com.yunos.tvtaobao.tradelink.resconfig;


import android.content.Context;
import android.graphics.drawable.Drawable;

import com.yunos.tvtaobao.tradelink.R;

public class TaobaoResConfig extends AbstractResConfig {

    public TaobaoResConfig(Context context) {
        super(context);

    }

    @Override
    public GoodsType getGoodsType() {

        return GoodsType.TAOBAO;
    }

    @Override
    public int getColor() {

        return mContext.getResources().getColor(R.color.ytm_orange);
    }


    @Override
    public Drawable getQRCodeIcon() {

        return mContext.getResources().getDrawable(R.drawable.tradelink_qr_code_icon_taobao);
    }


    @Override
    public Context getContext() {

        return mContext;
    }


}
