package com.yunos.tvtaobao.biz.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.businessview.R;


public class UpdateButton extends FrameLayout {
	
	private static final String TAG = "UpdateButton";
	
	private ImageView mFocus;
	
	private TextView mTextView;
	
	private ImageView mBg;
	
	private CharSequence mText;
	
	private float mTextSize;
	
	private int mTextFocusColor;
	
	private int mTextNormalColor;
	
	private int mBgFocusColor;
	
	private int mBgNormalColor;
	
	private boolean mIsNoBlur;
	
	private int mTextShadowY;
	
	private int mTextShadowR;
	
	private int mTextShadowColor;
	
	public UpdateButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public UpdateButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.buttonText);
		
		mText = a.getText(R.styleable.buttonText_text);
		mTextSize = a.getDimension(R.styleable.buttonText_textSize, 0);
		mIsNoBlur = a.getBoolean(R.styleable.buttonText_isNoBlur, false);
		
		a.recycle();
		
		if (mIsNoBlur) {
		    initNoBlur(context);
		} else {
		    initBlur(context);
		}
	}

	public UpdateButton(Context context) {
		super(context);
	}
	
	private void initNoBlur(Context context) {
	    if (isInEditMode()) return;
	    initBgNoBlur(context);
	    initTextViewNoBlur(context);
	}
	
	private void initBlur(Context context) {
		if (isInEditMode()) return;
		initBg(context);
		initTextView(context);
		initFocus(context);
	}
	
	private void initBgNoBlur(Context context) {
	    mBg = new ImageView(context);
        mBgFocusColor = context.getResources().getColor(R.color.bs_up_no_blur_button_focus);
        mBgNormalColor = context.getResources().getColor(R.color.bs_up_update_transparent);
        int width = context.getResources().getDimensionPixelSize(R.dimen.bs_up_no_blur_button_width);
        int height = context.getResources().getDimensionPixelSize(R.dimen.bs_up_no_blur_button_height);
        LayoutParams lp = new LayoutParams(width, height, Gravity.CENTER);
//        lp.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.bs_up_button_bg_margin_bottom);
        mBg.setBackgroundColor(mBgNormalColor);
        addView(mBg, lp);
	}
	
	private void initBg(Context context) {
		mBg = new ImageView(context);
		mBgFocusColor = context.getResources().getColor(R.color.bs_up_update_button_focus);
		mBgNormalColor = context.getResources().getColor(R.color.bs_up_update_button_normal);
		int width = context.getResources().getDimensionPixelSize(R.dimen.bs_up_button_width);
		int height = context.getResources().getDimensionPixelSize(R.dimen.bs_up_button_height);
		LayoutParams lp = new LayoutParams(width, height, Gravity.CENTER);
		lp.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.bs_up_button_bg_margin_bottom);
		mBg.setBackgroundColor(mBgNormalColor);
		addView(mBg, lp);
	}
	
	private void initTextViewNoBlur(Context context) {
	    mTextView = new TextView(context);
        mTextFocusColor = context.getResources().getColor(R.color.bs_up_update_white);
        mTextNormalColor = context.getResources().getColor(R.color.bs_up_update_black_50);
        mTextShadowColor = context.getResources().getColor(R.color.bs_up_no_blur_button_text_focus_shadow);
        mTextShadowY = context.getResources().getDimensionPixelSize(R.dimen.bs_up_no_blur_button_text_shadow_y);
        mTextShadowR = context.getResources().getDimensionPixelSize(R.dimen.bs_up_no_blur_button_text_shadow_r);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//        lp.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.bs_up_button_text_margin_bottom);
        mTextView.setText(mText);
        mTextView.setTextSize(mTextSize);
        mTextView.setTextColor(mTextNormalColor);
//        mTextView.setFocusable(true);
        addView(mTextView, lp);
	}
	
	private void initTextView(Context context) {
		mTextView = new TextView(context);
		mTextFocusColor = context.getResources().getColor(R.color.bs_up_update_white);
		mTextNormalColor = context.getResources().getColor(R.color.bs_up_update_white_50);
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		lp.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.bs_up_button_text_margin_bottom);
		mTextView.setText(mText);
		mTextView.setTextSize(mTextSize);
		mTextView.setTextColor(mTextNormalColor);
//		mTextView.setFocusable(true);
		addView(mTextView, lp);
	}
	
	private void initFocus(Context context) {
		mFocus = new ImageView(context);
		mFocus.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bs_up_focus));
		int width = context.getResources().getDimensionPixelSize(R.dimen.bs_up_focus_width);
		int height = context.getResources().getDimensionPixelSize(R.dimen.bs_up_focus_height);
		LayoutParams lp = new LayoutParams(width, height, Gravity.CENTER);
		addView(mFocus, lp);
		mFocus.setVisibility(View.INVISIBLE);
	}
	
	public void showFocus() {
		if (mFocus != null) {
			mFocus.setVisibility(View.VISIBLE);
			mFocus.bringToFront();
			mFocus.startAnimation(getFocusAnimation(true, 100));
		}
		if (mTextView != null) {
			mTextView.setTextColor(mTextFocusColor);
			if (mIsNoBlur) {
			    mTextView.setShadowLayer(mTextShadowR, 0, mTextShadowY, mTextShadowColor);
			}
		}
		if (mBg != null) {
			mBg.setBackgroundColor(mBgFocusColor);
		}
	}

	public void hideFocus() {
		if (mFocus != null) {
			mFocus.startAnimation(getFocusAnimation(false, 100));
		}
		if (mTextView != null) {
			mTextView.setTextColor(mTextNormalColor);
			if (mIsNoBlur) {
			    mTextView.setShadowLayer(0, 0, 0, 0);
			}
		}
		if (mBg != null) {
			mBg.setBackgroundColor(mBgNormalColor);
		}
	}
	
	private AnimationSet getFocusAnimation(final boolean show, long duration) {
		int from = 1;
		int to = 0;
		if (show) {
			from = 0;
			to = 1;
		}
		AnimationSet set = new AnimationSet(true);
		AlphaAnimation alpha = new AlphaAnimation(from, to);
		set.addAnimation(alpha);
		set.setDuration(duration);
		set.setFillAfter(true);
		return set;
	}
	
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		AppDebug.d(TAG, "onFocusChanged: " + gainFocus);
		if (gainFocus) {
			showFocus();
		} else {
			hideFocus();
		}
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

}
