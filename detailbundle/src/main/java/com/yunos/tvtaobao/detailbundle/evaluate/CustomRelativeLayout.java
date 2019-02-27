package com.yunos.tvtaobao.detailbundle.evaluate;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by xutingting on 2017/9/14.
 */

public class CustomRelativeLayout extends RelativeLayout {
    public CustomRelativeLayout(Context context) {
        super(context);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public View focusSearch(View focused, int direction) {

        Log.d("CustomRelativeLayout", focused +"=====" + direction);
        return super.focusSearch(focused, direction);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        

        return super.dispatchKeyEvent(event);
    }
}
