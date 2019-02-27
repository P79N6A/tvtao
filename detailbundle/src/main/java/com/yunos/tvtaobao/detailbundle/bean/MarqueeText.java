package com.yunos.tvtaobao.detailbundle.bean;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeText extends TextView {
    private boolean is_focus = false;

    public MarqueeText(Context con) {
        super(con);
    }

    public MarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        //AppDebug.d("jiangyg", "isFocused  : " + is_focus);
        return is_focus;
    }

    public void setFocus(boolean is_focus) {
        this.is_focus = is_focus;
        //requestLayout();
        //invalidate();
        setEllipsize(is_focus ? TextUtils.TruncateAt.MARQUEE : TextUtils.TruncateAt.END);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
    }
}