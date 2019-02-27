package com.yunos.tvtaobao.search.view;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.core.common.AppDebug;

import java.lang.ref.WeakReference;

public class CustomAlphaView extends View {

    public static final String TAG = "TVTaobaoAlphaView";

    public static final int MSG_ALPHA_PLAY = 51;
    public static final int MSG_ALPHA_STOP = 52;

    private int mFrame = 10;
    private int mCurrentFrame = 1;
    private float alpha = 0.0f;

    public CustomAlphaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomAlphaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomAlphaView(Context context) {
        super(context);
    }

    MsgHandler mHandler = new MsgHandler(this);

    protected static class MsgHandler extends Handler {

        private WeakReference<CustomAlphaView> mAlphaViewRef;

        public MsgHandler(CustomAlphaView view) {
            mAlphaViewRef = new WeakReference<CustomAlphaView>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            CustomAlphaView tabform = mAlphaViewRef.get();
            if (tabform != null) {
                tabform.handleMessage(msg);
            }
        }
    }

    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case MSG_ALPHA_PLAY:
                if (mCurrentFrame <= mFrame) {
                    alpha = (1.0f - (1.0f * mCurrentFrame / mFrame));
                    setAlpha(alpha);
                    //AppDebug.v("", "handleMessage.MSG_ALPHA_PLAY alpha = " + alpha);
                    mCurrentFrame++;
                    postInvalidateDelayed(200);
                    mHandler.sendEmptyMessage(MSG_ALPHA_PLAY);
                }
                break;
            case MSG_ALPHA_STOP:
                if (mCurrentFrame <= mFrame) {
                    alpha = (1.0f * mCurrentFrame / mFrame);
                    setAlpha(alpha);
                    AppDebug.v("", "handleMessage.MSG_ALPHA_STOP alpha = " + alpha);
                    mCurrentFrame++;
                    postInvalidateDelayed(100);
                    mHandler.sendEmptyMessage(MSG_ALPHA_STOP);
                }
                break;
        }
    };

    /**
     * 蒙版显示
     */
    public boolean isShown() {
        if (alpha > 0.0f) {
            return true;
        }
        return false;
    }

    /**
     * 逐渐变透明过程
     */
    public void playAlpha() {
        alpha = 0.0f;
        mCurrentFrame = 0;
        mHandler.sendEmptyMessage(MSG_ALPHA_PLAY);
    }

    /**
     * 逐渐变不透明过程
     */
    public void stopAlpha() {
        alpha = 0.0f;
        mCurrentFrame = 0;
        mHandler.sendEmptyMessage(MSG_ALPHA_STOP);
    }

}