package com.aliyun.base.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aliyun.base.info.MobileInfo;

public class LoadingAlert extends FrameLayout {

	private TextView mText;

	public LoadingAlert(Activity activity) {
		super(activity);
		createView(activity);
		LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lay.gravity = Gravity.CENTER;
		activity.addContentView(this, lay);
	}

	public LoadingAlert(Dialog dialog) {
		super(dialog.getContext());
		createView(dialog.getContext());
		LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(dialog.getWindow().peekDecorView().getWidth(), dialog.getWindow()
				.peekDecorView().getHeight());
		lay.gravity = Gravity.CENTER;
		dialog.addContentView(this, lay);
	}

	public void createView(Context context) {
		LinearLayout view = new LinearLayout(context);
		LayoutParams viewLay = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		viewLay.gravity = Gravity.CENTER;
		view.setLayoutParams(viewLay);
		view.setPadding((int) MobileInfo.dip2px(context, 10), (int) MobileInfo.dip2px(context, 20),
				(int) MobileInfo.dip2px(context, 10), (int) MobileInfo.dip2px(context, 20));
		view.setGravity(Gravity.CENTER);
		view.setBackgroundColor(Color.TRANSPARENT);
		view.setOrientation(LinearLayout.HORIZONTAL);
		// RoundRectShape shape = new RoundRectShape(new
		// float[]{ImageUtils.dipTopx(context, 10f), ImageUtils.dipTopx(context,
		// 10f), ImageUtils.dipTopx(context, 10f), ImageUtils.dipTopx(context,
		// 10f), ImageUtils.dipTopx(context, 10f), ImageUtils.dipTopx(context,
		// 10f), ImageUtils.dipTopx(context, 10f), ImageUtils.dipTopx(context,
		// 10f)}, new RectF(3, 3, 3, 3), new float[]{12f, 12f, 12f, 12f, 12f,
		// 12f, 12f, 12f});
		// ShapeDrawable draw = new ShapeDrawable(shape);
		// draw.getPaint().setColor(Color.GRAY);
		// view.setBackgroundDrawable(draw);

		ProgressBar progress = new ProgressBar(context, null, android.R.attr.progressBarStyle);
		progress.setProgressDrawable(new ColorDrawable(Color.BLACK));
		LinearLayout.LayoutParams parpmProgress = new LinearLayout.LayoutParams((int) MobileInfo.dip2px(context, 25),
				(int) MobileInfo.dip2px(context, 25));
		parpmProgress.setMargins(0, 0, (int) MobileInfo.dip2px(context, 5), 0);
		parpmProgress.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		progress.setLayoutParams(parpmProgress);
		view.addView(progress);

		mText = new TextView(context);
		mText.setTextColor(Color.BLACK);
		mText.setText("正在加载...");
//		LinearLayout.LayoutParams paramTextView = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		view.addView(mText);

		addView(view);
	}

	public void setMessage(String message) {
		mText.setText(message);
	}

}
