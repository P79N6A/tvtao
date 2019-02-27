package com.yunos.tvtaobao.detailbundle.view;

import android.content.Context;
import android.util.AttributeSet;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.FocusRelativeLayout;
import com.yunos.tv.app.widget.focus.params.Params;


public class DetailFocusRelativeLayout extends FocusRelativeLayout {

    public DetailFocusRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDetailFocusRelativeLayout(context);
    }

    public DetailFocusRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDetailFocusRelativeLayout(context);
    }

    public DetailFocusRelativeLayout(Context context) {
        super(context);
        initDetailFocusRelativeLayout(context);
    }
    
    private void initDetailFocusRelativeLayout(Context context){
        mParams = new Params(1.05f, 1.05f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    }
    
    @Override
    public boolean isScale() {
        return true;
    }
    
}
