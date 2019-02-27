package com.yunos.tvtaobao.homebundle.detainment;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.FocusHListView;
import com.yunos.tv.app.widget.focus.params.Params;

public class DetainMentListView extends FocusHListView {

    private Params mParamsFocus = new Params(1.05f, 1.05f, 5, null, true, 10,
            new AccelerateDecelerateFrameInterpolator());

    public DetainMentListView(Context context) {
        super(context);
    }

    public DetainMentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetainMentListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public Params getParams() {
        if (mParamsFocus == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
        }
        return mParamsFocus;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            return true;
        }
        return super.preOnKeyDown(keyCode, event);
    }

}
