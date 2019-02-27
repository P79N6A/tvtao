package com.yunos.tvtaobao.juhuasuan.homepage;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.AccelerateFrameInterpolator;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.FrameInterpolator;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.ItemInterface;

public class ItemHomeImageView extends ImageView implements ItemInterface {
    
    private boolean mScale = true;

    public ItemHomeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public ItemHomeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public ItemHomeImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getItemWidth() {
        // TODO Auto-generated method stub
        return getWidth();
        //        return getDp(496);
    }

    @Override
    public int getItemHeight() {
        // TODO Auto-generated method stub
        return getHeight();
        //        return getDp(507);
    }

    @Override
    public Rect getOriginalRect() {
        // TODO Auto-generated method stub
        Rect rect = new Rect();
        rect.left = getLeft();
        rect.right = getRight();
        rect.top = getTop();
        rect.bottom = getBottom();

        return rect;
    }

    @Override
    public Rect getItemScaledRect(float scaledX, float scaledY) {
        // TODO Auto-generated method stub
        Rect rect = new Rect();
        // int[] location = new int[2];
        // getLocationOnScreen(location);

        int imgW = getWidth();
        int imgH = getHeight();

        rect.left = (int) (getLeft() - (scaledX - 1.0f) * imgW / 2 + 0.5f);
        rect.right = (int) (rect.left + imgW * scaledX - 0.6f);
        rect.top = (int) (getTop() - (scaledY - 1.0f) * imgH / 2 + 0.5f);
        rect.bottom = (int) (rect.top + imgH * scaledY - 0.6f);
        return rect;
    }

    @Override
    public boolean getIfScale() {
        return mScale;
    }

    AccelerateFrameInterpolator mScaleInterpolator = new AccelerateFrameInterpolator();
    AccelerateFrameInterpolator mFocusInterpolator = new AccelerateFrameInterpolator(0.5f);

    @Override
    public FrameInterpolator getFrameScaleInterpolator() {
        // TODO Auto-generated method stub
        return mScaleInterpolator;
    }

    @Override
    public FrameInterpolator getFrameFocusInterpolator() {
        // TODO Auto-generated method stub
        return mFocusInterpolator;
    }

    @Override
    public Rect getFocusPadding(Rect originalRect, Rect lastOriginalRect, int direction, int focusFrameRate) {
        // TODO Auto-generated method stub
        return null;
    }

    //    private int getDp(int value) {
    //        return (int) (value * SystemUtil.getMagnification());
    //    }
    
    
    public void setViewScale(boolean scale) {
        mScale = scale;        
    }
}
