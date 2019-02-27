package com.yunos.tvtaobao.homebundle.detainment;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tvtaobao.homebundle.R;

public class DetainMentTextView extends TextView implements ItemListener {

    private int step_pad;

    public DetainMentTextView(Context context) {
        super(context);
        init();
    }

    public DetainMentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetainMentTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        step_pad = getResources().getDimensionPixelSize(R.dimen.dp_1);
    }

    private void onAdjustFocusedRect(Rect r) {
        r.top += step_pad;
        r.bottom -= step_pad;
    }

    private Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        onAdjustFocusedRect(r);
        return r;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect focuse = getFocusedRect();
        FocusRectParams focusRectParams = new FocusRectParams(focuse, 0.5f, 0.5f);
        return focusRectParams;
    }

    @Override
    public boolean isScale() {
        return true;
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public void drawBeforeFocus(Canvas canvas) {
    }

    @Override
    public void drawAfterFocus(Canvas canvas) {

    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
