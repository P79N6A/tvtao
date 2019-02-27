package com.yunos.tvtaobao.tradelink.resconfig;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.yunos.tvtaobao.tradelink.R;


public class TmallResConfig extends AbstractResConfig {

    public TmallResConfig(Context context) {
        super(context);

    }

    @Override
    public GoodsType getGoodsType() {

        return GoodsType.TMALL;
    }

    @Override
    public int getColor() {

        return mContext.getResources().getColor(R.color.ytm_tmall_red);
    }

    @Override
    public Drawable getQRCodeIcon() {

        return mContext.getResources().getDrawable(R.drawable.tradelink_qr_code_icon_tmail);
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
