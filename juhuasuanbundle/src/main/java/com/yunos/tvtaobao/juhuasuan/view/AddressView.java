package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;

import com.yunos.tv.core.common.AppDebug;

public class AddressView extends FrameLayout implements OnKeyListener {

    private OnKeyListener mOnKeyListener;
    private OnKeyDownListener mOnKeyDownListener;
    private View backgroundView;
    private FrameLayout.LayoutParams frameLayoutParams;
    private Drawable backgroundDrawable;
    private OnClickListener mOnClickListener;

    public AddressView(Context context) {
        super(context);
        initBackground();
    }

    public AddressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBackground();
    }

    public AddressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initBackground();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AppDebug.i("AddressView", "scroll onKeyDown -> event addressView : " + getId());
        if (null != mOnKeyDownListener) {
            AppDebug.i("AddressView", "scroll onKeyDown -> event addressView mOnKeyDownListener : "
                    + mOnKeyDownListener);
            return mOnKeyDownListener.onKeyDown(this, keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        AppDebug.i("AddressView", "scroll onKey -> event addressView");
        if (null != mOnKeyListener) {
            return mOnKeyListener.onKey(v, keyCode, event);
        }
        return false;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    public boolean callAddressOnClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(this);
            return true;
        }
        return false;
    }

    public OnKeyListener getOnKeyListener() {
        return mOnKeyListener;
    }

    public void setOnKeyListener(OnKeyListener onKeyListener) {
        mOnKeyListener = onKeyListener;
    }

    public OnKeyDownListener getOnKeyDownListener() {
        return mOnKeyDownListener;
    }

    public void setOnKeyDownListener(OnKeyDownListener onKeyDownListener) {
        mOnKeyDownListener = onKeyDownListener;
    }

    public interface OnKeyDownListener {

        public boolean onKeyDown(View v, int keyCode, KeyEvent event);
    }

    public void setBackgroundLayoutParams(FrameLayout.LayoutParams lp) {
        frameLayoutParams = lp;
        backgroundView.setLayoutParams(frameLayoutParams);
    }

    private void initBackground() {
        if (null != backgroundView) {
            return;
        }
        backgroundView = new View(getContext());
        if (frameLayoutParams != null) {
            backgroundView.setLayoutParams(frameLayoutParams);
        }
        if (null == backgroundDrawable) {
            backgroundDrawable = getBackground();
            super.setBackgroundDrawable(null);
        }
        backgroundView.setBackgroundDrawable(backgroundDrawable);

        addView(backgroundView, 0);
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (null != backgroundView) {
            backgroundView.setBackgroundDrawable(backgroundDrawable);
        } else {
            super.setBackgroundDrawable(background);
        }
    }

    public void setBackgroundColor(int color) {
        if (null != backgroundView) {
            backgroundView.setBackgroundColor(color);
        } else {
            super.setBackgroundColor(color);
        }
    }

    public void setBackgroundResource(int resid) {
        if (null != backgroundView) {
            backgroundView.setBackgroundResource(resid);
        } else {
            super.setBackgroundResource(resid);
        }
    }

    public OnKeyListener getmOnKeyListener() {
        return mOnKeyListener;
    }

    public void setmOnKeyListener(OnKeyListener mOnKeyListener) {
        this.mOnKeyListener = mOnKeyListener;
    }

    public OnKeyDownListener getmOnKeyDownListener() {
        return mOnKeyDownListener;
    }

    public void setmOnKeyDownListener(OnKeyDownListener mOnKeyDownListener) {
        this.mOnKeyDownListener = mOnKeyDownListener;
    }

    public FrameLayout.LayoutParams getFrameLayoutParams() {
        return frameLayoutParams;
    }

    public void setFrameLayoutParams(FrameLayout.LayoutParams frameLayoutParams) {
        this.frameLayoutParams = frameLayoutParams;
    }

}
