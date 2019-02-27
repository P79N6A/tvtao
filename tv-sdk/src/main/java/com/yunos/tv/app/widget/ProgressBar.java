package com.yunos.tv.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ProgressBar extends FrameLayout {

	public static final int SNAKE_TYPE = 0;
	public static final int ROUND_TYPE = 1;

	public View mProgressBar;

	public ProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public ProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public ProgressBar(Context context) {
		super(context);
		init(null);
	}

	void init(AttributeSet attrs) {
		mProgressBar = new SnakeProgressBar(this.getContext(), attrs);
		this.addView(mProgressBar, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
	}

	public void setType(int type) {
		if (type == ROUND_TYPE) {
			this.removeAllViews();
			mProgressBar = new RoundProgressBar(this.getContext());
			this.addView(mProgressBar, new LayoutParams(MATCH_PARENT,
					MATCH_PARENT));
		}
	}

}