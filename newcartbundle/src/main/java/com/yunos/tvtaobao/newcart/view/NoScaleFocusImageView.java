package com.yunos.tvtaobao.newcart.view;

import android.content.Context;
import android.util.AttributeSet;

import com.yunos.tv.app.widget.focus.FocusImageView;

/**
 * Created by wuhaoteng on 2018/8/30.
 * 聚焦不会放大的FocusImageView
 */

public class NoScaleFocusImageView extends FocusImageView {
    public NoScaleFocusImageView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    public NoScaleFocusImageView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public NoScaleFocusImageView(Context arg0) {
        super(arg0);
    }

    @Override
    public boolean isScale() {
        return false;
    }
}
