package com.yunos.tvtaobao.live.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.yunos.tv.app.widget.focus.FocusPositionManager;

/**
 * Created by wuhaoteng on 2018/9/14.
 */

public class LiveFocusPositionManager extends FocusPositionManager {
    private OnKeyDownListener mListener;

    public LiveFocusPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LiveFocusPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LiveFocusPositionManager(Context context) {
        super(context);
    }


    public void setOnkeyDownListener(OnKeyDownListener listener){
        mListener = listener;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(mListener!=null){
           return mListener.onKeyDown(keyCode,event);
        }
        return superOnKeyDown(keyCode, event);
    }

    public boolean superOnKeyDown(int keyCode, KeyEvent event){
        return super.onKeyDown(keyCode, event);
    }

    public interface OnKeyDownListener{
        boolean onKeyDown(int keyCode, KeyEvent event);
    }
}
