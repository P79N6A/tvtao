package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.R;

public class CategoryButton extends Button {

    private final String TAG = "CategoryButton";
    private Context mContext;

    private int mLoseFocusHeight = 0;
    private int mLoseFocusWidth = 0;
    private int mFocusHeight = 0;
    private int mFocusWidth = 0;
    private int mMarginChange = 0;

    public CategoryButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        onInitLayout(context);
    }

    public CategoryButton(Context context) {
        super(context);

        onInitLayout(context);
    }

    public CategoryButton(Context context, AttributeSet attrs) {
        // TODO Auto-generated constructor stub
        super(context, attrs);

        onInitLayout(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void onInitLayout(Context context) {
        mContext = context;
        setAlpha(0.7f);
        setBackgroundDrawable(getResources().getDrawable(R.drawable.jhs_box));

        mLoseFocusHeight = getResources().getDimensionPixelSize(R.dimen.dp_90);
        mLoseFocusWidth = getResources().getDimensionPixelSize(R.dimen.dp_216);

        mFocusHeight = getResources().getDimensionPixelSize(R.dimen.dp_106);
        mFocusWidth = getResources().getDimensionPixelSize(R.dimen.dp_232);

        mMarginChange = getResources().getDimensionPixelSize(R.dimen.dp_8);
        AppDebug.i(TAG, TAG + ".onInitLayout mLoseFocusHeight=" + mLoseFocusHeight + ", mLoseFocusWidth="
                + mLoseFocusWidth + ", mFocusWidth=" + mFocusWidth + ", mFocusHeight=" + mFocusHeight);

    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if (!focused) {// 获得焦点
            setLoseFocusStatusView();
        } else {
            setFocusStatusView();
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    public void setFocusStatusView() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        setAlpha(1f);
        setBackgroundDrawable(getResources().getDrawable(R.drawable.jhs_box_right));
        AppDebug.i(TAG, TAG + ".onInitLayout mFocusWidth=" + mFocusWidth + ", mFocusHeight=" + mFocusHeight
                + ", params.width=" + params.width + ", params.height=" + params.height);
        if (params.height != mFocusHeight || params.width != mFocusWidth) {
            params.height = mFocusHeight;
            params.width = mFocusWidth;
            params.topMargin = params.topMargin - mMarginChange;
            params.leftMargin = params.leftMargin - mMarginChange;
        }
        setLayoutParams(params);
    }

    public void setLoseFocusStatusView() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        setAlpha(0.7f);
        AppDebug.i(TAG, TAG + ".onInitLayout mLoseFocusWidth=" + mLoseFocusWidth + ", mLoseFocusHeight="
                + mLoseFocusHeight + ", params.width=" + params.width + ", params.height=" + params.height);
        setBackgroundDrawable(getResources().getDrawable(R.drawable.jhs_box));
        if (params.width != mLoseFocusWidth || params.height != mLoseFocusHeight) {
            params.height = mLoseFocusHeight;
            params.width = mLoseFocusWidth;
            params.topMargin = params.topMargin + mMarginChange;
            params.leftMargin = params.leftMargin + mMarginChange;
        }
        setLayoutParams(params);
    }

}
