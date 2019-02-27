package com.aliyun.base.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aliyun.base.info.MobileInfo;

public abstract class ImagePreviewDialog extends AlertDialog {
	private ImageView mImage;
	private ProgressBar mProgress;
	private Button mButton;
	private int dip7;
	private LinearLayout lay;
	private LayoutParams lp;

	public ImagePreviewDialog(final Context context, int btnRid) {
		super(context);
		setCanceledOnTouchOutside(true);
		dip7 = (int) Math.ceil(7 * MobileInfo.getDensity(context));
		lay = new LinearLayout(context);
		lay.setOrientation(LinearLayout.VERTICAL);
		lay.setGravity(Gravity.CENTER);
		mImage = new ImageView(context);
		LayoutParams btnLp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mButton = new Button(context);

		lay.addView(mImage, new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		lp = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		btnLp.topMargin = dip7;
		lay.addView(mButton, btnLp);
		mImage.setPadding(dip7, dip7, dip7, dip7);
		mImage.setAdjustViewBounds(true);
//		mImage.setScaleType(ScaleType.CENTER_INSIDE);
		mImage.setMinimumHeight(MobileInfo.dip2px(context, (int) Math.ceil(80 * MobileInfo.getDensity(context))));
		mImage.setMinimumWidth(MobileInfo.dip2px(context, (int) Math.ceil(80 * MobileInfo.getDensity(context))));
		mImage.setMaxHeight(MobileInfo.getScreenHeightPx(context) - 88);
		mImage.setBackgroundResource(Resources.getSystem().getIdentifier("panel_picture_frame_bg_normal", "drawable", "android"));
		mProgress = new ProgressBar(context, null, android.R.attr.progressBarStyle);
		mProgress.setProgressDrawable(new ColorDrawable(Color.WHITE));

		mButton.setBackgroundResource(btnRid);
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onButtonClick();
				cancel();
			}
		});
		mButton.setVisibility(View.GONE);

		setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				setContentView(lay);
				addContentView(mProgress, lp);
			}
		});
	}
	
	

	public abstract void loadImage(ImageView view);

	public abstract void onButtonClick();

	protected void hidePreProcess() {
		mButton.setVisibility(View.VISIBLE);
		mProgress.setVisibility(View.GONE);
	}

	protected void errorPreProcess() {
		Toast.makeText(getContext(), "图片不存在或已删除", Toast.LENGTH_SHORT).show();
		cancel();
	}

	@Override
	public void show() {
		super.show();
		mProgress.setVisibility(View.VISIBLE);
		loadImage(mImage);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			cancel();
			return true;
		}
		return false;
	}

}
