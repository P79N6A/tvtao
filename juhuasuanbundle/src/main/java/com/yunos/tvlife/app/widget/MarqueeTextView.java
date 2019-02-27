package com.yunos.tvlife.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

public class MarqueeTextView extends TextView {
	public final static String TAG = "MarqueeTextView";
	private final static int MESSAGE_DRAW = 1;
	private float mTextLength = 0;// 文本长度
	private float mViewWidth = 0;
	private float mStep = 0;// 文字的横坐标
	private float mY = 0;// 文字的纵坐标
	private float temp_view_plus_text_length = 0;// 用于计算的临时变量
	private float temp_view_plus_text_two_length = 0;// 用于计算的临时变量
	public boolean mIsStarting = false;// 是否开始滚动
	private Paint mPaint = null;// 绘图样式
	private String mText = "";// 文本内容
	private float mIntervel = 1.0f;
	private boolean mIsFirst = true;
	private boolean mMarqueeStart = false;
	private int mFirstDrawIntervel = 2000;
	private int mDrawIntervel = 20;
	private int mTextIntervel = 50;// px
	private int mTextColor = Color.WHITE;

	public MarqueeTextView(Context context) {
		super(context);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!isStart() || temp_view_plus_text_length <= mViewWidth) {
			init();
			if (temp_view_plus_text_length <= mViewWidth) {
				mPaint.setColor(mTextColor);
				canvas.drawText(mText, Math.abs(temp_view_plus_text_length - mViewWidth) / 2, mY, mPaint);
			} else {
				super.onDraw(canvas);
			}

			return;
		}

		if (mIsFirst) {
			mIsFirst = false;
			drawText(canvas, true);
			mHandler.sendEmptyMessageDelayed(MESSAGE_DRAW, this.mFirstDrawIntervel);
		} else {

			if (mMarqueeStart) {
				drawText(canvas, false);
				mHandler.sendEmptyMessageDelayed(MESSAGE_DRAW, this.mDrawIntervel);
				// invalidate();
			} else {
				drawText(canvas, true);
			}
		}
	}

	void setStart(boolean isStart) {
		synchronized (this) {
			this.mIsStarting = isStart;
		}
	}

	boolean isStart() {
		synchronized (this) {
			return this.mIsStarting;
		}
	}

	void drawText(Canvas canvas, boolean isFirst) {
		mPaint.setColor(mTextColor);
		canvas.drawText(mText, temp_view_plus_text_length - mStep, mY, mPaint);
		if (mViewWidth + mStep - temp_view_plus_text_two_length > mTextIntervel) {
			canvas.drawText(mText, temp_view_plus_text_two_length - mStep + mTextIntervel, mY, mPaint);
		}

		if (!isFirst) {
			mStep += this.mIntervel;// 0.5为文字滚动速度。if
			float diff = temp_view_plus_text_two_length - mStep + mTextIntervel;
			if (Math.abs(diff) < this.mIntervel) {
				mStep = temp_view_plus_text_length + diff;
			}
		}

	}

	public void setTextColor(int color) {
		this.mTextColor = color;
	}

	public void setFirstDrawIntervel(int intervel) {
		this.mFirstDrawIntervel = intervel;
	}

	public void setDrawIntervel(int intervel) {
		this.mDrawIntervel = intervel;
	}

	public void setMarquee(int intervel) {
		this.mIntervel = intervel;
	}

	public void setText(String text) {
		stopMarquee();
		if (isStart()) {
			startMarquee();
		}
		super.setText(text);
		this.mIsInit = false;
	}

	boolean mIsInit = false;

	public void init() {
		if (mIsInit) {
			return;
		}

		mPaint = getPaint();
		mText = getText().toString();
		mTextLength = mPaint.measureText(mText);
		mTextColor = getTextColors().getDefaultColor();
		mViewWidth = getWidth();
		if (mViewWidth == 0) {
			ViewGroup.LayoutParams params = getLayoutParams();
			if (params != null) {
				mViewWidth = params.width;
			}
		}

		mStep = mTextLength;
		temp_view_plus_text_length = mTextLength;
		temp_view_plus_text_two_length = mTextLength * 2;
		mY = getTextSize() + getPaddingTop();

		mIsInit = true;
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);

		ss.step = mStep;
		ss.isStarting = isStart();
		ss.textColor = this.mTextColor;

		return ss;

	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		mStep = ss.step;
		setStart(ss.isStarting);
		mTextColor = ss.textColor;
	}

	public static class SavedState extends BaseSavedState {
		public boolean isStarting = false;
		public float step = 0;
		public int textColor = Color.WHITE;

		SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeBooleanArray(new boolean[] { isStarting });
			out.writeFloat(step);
			out.writeInt(textColor);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}

			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
		};

		private SavedState(Parcel in) {
			super(in);
			boolean[] b = new boolean[1];
			in.readBooleanArray(b);
			if (b != null && b.length > 0)
				isStarting = b[0];
			step = in.readFloat();
			textColor = in.readInt();
		}
	}

	public void startMarquee() {
		setStart(true);
		init();
		invalidate();
	}

	public void stopMarquee() {
		setStart(false);
		mMarqueeStart = false;
		mIsFirst = true;
		mStep = mTextLength;
		mHandler.removeMessages(MESSAGE_DRAW);
		invalidate();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mMarqueeStart = true;
			invalidate();
		}
	};
}
