package com.yunos.tvtaobao.homebundle.detainment;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusLinearLayout;

public class TouchFocusLinearLayout extends FocusLinearLayout {

    public TouchFocusLinearLayout(Context context) {
        super(context);
    }

    public TouchFocusLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchFocusLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnFocusChanged() {
        onFocusChanged(true, View.FOCUS_DOWN, null);
    }

}
