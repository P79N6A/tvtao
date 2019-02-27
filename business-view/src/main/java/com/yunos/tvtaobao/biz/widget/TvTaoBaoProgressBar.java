package com.yunos.tvtaobao.biz.widget;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.yunos.tvtaobao.businessview.R;


public class TvTaoBaoProgressBar extends ProgressBar {

    public TvTaoBaoProgressBar(Context context) {
        super(context);
        initTvTaoBaoProgressBar(context);
    }

    public TvTaoBaoProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTvTaoBaoProgressBar(context);
    }

    public TvTaoBaoProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTvTaoBaoProgressBar(context);
    }

    private void initTvTaoBaoProgressBar(Context context) {
        //  设置 Drawable
        Drawable drawable = context.getResources().getDrawable(R.drawable.ytbv_load_animation);
        setIndeterminateDrawable(drawable); 
        setIndeterminate(false); 
    }
}
