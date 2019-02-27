package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

public class SelectMarqueeTextView extends TextView {

    public SelectMarqueeTextView(Context context) {
        super(context);
    }

    public SelectMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected) {
            setMarqueeRepeatLimit(-1);
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
        } else
            setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public boolean isFocused() {
        return isSelected();
    }
}
