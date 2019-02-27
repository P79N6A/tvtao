package com.yunos.tvtaobao.juhuasuan.homepage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.yunos.tvtaobao.juhuasuan.R;


public class ShadeFrameLayout extends FrameLayout {
    
    private static final String TAG = "ShadeFrameLayout";
    
//    private GradientDrawable       rightShadeDraw         = null;
//    private GradientDrawable       leftShadeDraw         = null;
    
    private LinearLayout leftShadeView;
    private LinearLayout rightShadeView;

    public ShadeFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public ShadeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShadeFrameLayout(Context context) {
        super(context);
        init(context);
    }
    
    public void init(Context context) {
        FrameLayout.LayoutParams PleftShadeView = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.FILL_PARENT);
        PleftShadeView.gravity = Gravity.LEFT;
        leftShadeView = new LinearLayout(context);
        leftShadeView.setBackgroundResource(R.drawable.jhs_shade_left);
        leftShadeView.setVisibility(View.GONE);
        addView(leftShadeView, PleftShadeView);

        FrameLayout.LayoutParams PrightShadeView = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.FILL_PARENT);
        PrightShadeView.gravity = Gravity.RIGHT;
        rightShadeView = new LinearLayout(context);
        rightShadeView.setBackgroundResource(R.drawable.jhs_shade_right);
        rightShadeView.setVisibility(View.GONE);
        addView(rightShadeView, PrightShadeView);
    }

    public void clean () {
        invalidate();
    }

    public void setLeftShade (int screenWidth, int screenHeight, int leftShade) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(leftShade, screenHeight);
        params.width = leftShade;
        leftShadeView.setLayoutParams(params);
        leftShadeView.setVisibility(View.VISIBLE);
        invalidate();
    }
    
    public void hideLeftShade() {
        leftShadeView.setVisibility(View.GONE);
    }
    
    public void setRightShade (int screenWidth, int screenHeight, int rightShade) {
        ViewGroup.LayoutParams params = rightShadeView.getLayoutParams();
        params.width = rightShade;
        rightShadeView.setLayoutParams(params);
        rightShadeView.setVisibility(View.VISIBLE);
        invalidate();
    }
    
    public void hideRightShade() {
        rightShadeView.setVisibility(View.GONE);
    }

}
