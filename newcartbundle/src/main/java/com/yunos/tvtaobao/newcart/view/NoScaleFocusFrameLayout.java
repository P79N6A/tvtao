package com.yunos.tvtaobao.newcart.view;

import android.content.Context;
import android.util.AttributeSet;

import com.yunos.tvtaobao.biz.widget.FocusNoDeepFrameLayout;

/**
 * Created by wuhaoteng on 2018/9/8.
 * 基于Focusmanager聚焦不会放大的FrameLayout
 */

public class NoScaleFocusFrameLayout extends FocusNoDeepFrameLayout {

    public NoScaleFocusFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NoScaleFocusFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScaleFocusFrameLayout(Context context) {
        super(context);
    }


    @Override
    public boolean isScale() {
        return false;
    }
}
