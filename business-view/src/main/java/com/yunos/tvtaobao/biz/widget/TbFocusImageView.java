package com.yunos.tvtaobao.biz.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.FocusImageView;
import com.yunos.tv.app.widget.focus.params.Params;


public class TbFocusImageView extends FocusImageView {
    protected Params mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());

    public TbFocusImageView(Context arg0) {
        super(arg0);
    }
    
    public TbFocusImageView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }
    
    public TbFocusImageView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public Params getParams() {
        if (mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
        }
        return mParams;
    }
    
    @Override
    public boolean isScale() {
        return true;
    }
}
