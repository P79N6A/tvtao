package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.yunos.tv.app.widget.focus.FocusTextView;
import com.yunos.tv.core.common.AppDebug;

public class BuildOrderTextView extends FocusTextView {

    public BuildOrderTextView(Context context) {
        super(context);
    }

    public BuildOrderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BuildOrderTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        AppDebug.i("BuildOrderTextView", "BuildOrderTextView" + ".preOnKeyDown.event = " + event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                return true;

            default:
                break;
        }
        return super.preOnKeyDown(keyCode, event);
    }
}
