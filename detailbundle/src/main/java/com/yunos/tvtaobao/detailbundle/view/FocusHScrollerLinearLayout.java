package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.FocusScrollerLinearLayout;
import com.yunos.tv.app.widget.focus.params.Params;

public class FocusHScrollerLinearLayout extends FocusScrollerLinearLayout {

    protected static final String TAG = "FocusHScrollerLinearLayout";
    protected Params mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());

    public FocusHScrollerLinearLayout(Context context) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public FocusHScrollerLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public FocusHScrollerLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(LinearLayout.HORIZONTAL);
    }
    
}
