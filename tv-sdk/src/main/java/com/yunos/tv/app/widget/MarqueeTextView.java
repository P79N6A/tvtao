package com.yunos.tv.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
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

import com.yunos.tv.aliTvSdk.R;


@SuppressLint("AppCompatCustomView")
public class MarqueeTextView extends TextView {
	public final static String TAG = "MarqueeTextView";
	private final static int MESSAGE_DRAW = 1;
	private float mTextLength = 0;// 文本长度
	private float mViewWidth = 0;
	private float mStep = 0;// 文字的横坐标
	private float mY = 0;// 文字的纵坐标
	private float mOffsetY = 0;
	private float temp_view_plus_text_length = 0;// 用于计算的临时变量
	private float temp_view_plus_text_two_length = 0;// 用于计算的临时变量
	public boolean mIsStarting = false;// 是否开始滚动
	private Paint mPaint = null;// 绘图样式
	private String mText = "";// 文本内容
	private float mIntervel = 1.0f;
	private boolean mIsFirst = true;
	private boolean mMarqueeStart = false;
	private int mFirstDrawIntervel = 1000;
	private int mDrawIntervel = 20;
	private int mTextIntervel = 50;// px
	private int mTextColor = Color.WHITE;
	private int mTextGravity = 0; // 默认居左。
	private float mScale = 1.0f; // 默认不放大

	public MarqueeTextView(Context context) {
		super(context);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private final void init(Context context, AttributeSet attrs) {
		try {
			TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.MarqueeTextViewAttr);
			mFirstDrawIntervel = types.getInteger(R.styleable.MarqueeTextViewAttr_firstDrawIntervel, 1000);
			mTextGravity = types.getInt(R.styleable.MarqueeTextViewAttr_textGravity, 1);
			types.recycle();
		} catch (Exception e) {
		}
	}

	public void setScale(float scale) {
		if (scale > 1.0 && scale <= 2.0) { // 只能在次放大倍数之内 TODO 之后改进逻辑
			this.mScale = scale;
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!isStart() || temp_view_plus_text_length <= mViewWidth) {
			initPaint();
			if (temp_view_plus_text_length <= mViewWidth) {
				mPaint.setColor(mTextColor);
				int offset = 0;

				switch (mTextGravity) {
				case 0:
					offset = 0;
					break;
				case 1:
					offset = (int) (Math.abs(temp_view_plus_text_length - mViewWidth) / 2);
					break;
				case 2:
					offset = (int) Math.abs(temp_view_plus_text_length - mViewWidth);
					break;
				}
				canvas.drawText(mText, offset, mY, mPaint);
			} else {
				super.onDraw(canvas);
			}

			return;
		}

		if (mIsFirst) {
			drawText(canvas, true);
			mHandler.sendEmptyMessageDelayed(MESSAGE_DRAW, this.mFirstDrawIntervel);
			mIsFirst = false;
		} else {
			initViewWidth();
			if (mMarqueeStart) {
				drawText(canvas, false);
				mHandler.sendEmptyMessageDelayed(MESSAGE_DRAW, this.mDrawIntervel);
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

	protected boolean isStart() {
		synchronized (this) {
			return this.mIsStarting;
		}
	}

	void drawText(Canvas canvas, boolean isFirst) {
		mPaint.setColor(mTextColor);
		if (mIsStarting) {
			canvas.drawText(mText, temp_view_plus_text_length - mStep, mY * mScale, mPaint);
		} else {
			canvas.drawText(mText, temp_view_plus_text_length - mStep, mY, mPaint);
		}
		if (mViewWidth + mStep - temp_view_plus_text_two_length > mTextIntervel) {
			if (mIsStarting) {
				canvas.drawText(mText, temp_view_plus_text_two_length - mStep + mTextIntervel, mY * mScale, mPaint);
			} else {
				canvas.drawText(mText, temp_view_plus_text_two_length - mStep + mTextIntervel, mY, mPaint);
			}
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
		super.setTextColor(color);
	}

	public void setFirstDrawIntervel(int intervel) {
		this.mFirstDrawIntervel = intervel;
	}

	public void setDrawIntervel(int intervel) {
		this.mDrawIntervel = intervel;
	}

	public void setMarquee(float intervel) {
		this.mIntervel = intervel;
	}

	public void setTextIntervel(int textIntervel) {
		if (textIntervel < 1) {
			throw new IllegalArgumentException("textIntervel must be > 1.0f");
		}

		mTextIntervel = textIntervel;
	}

	public void setText(String text) {
		stopMarquee();

		super.setText(text);

		reset();
//		if (isStart()) {
//			startMarquee();
//		}
	}

	boolean mIsInit = false;

	protected void setInit(boolean isInit) {
		mIsInit = isInit;
	}

	private void initPaint() {
		initViewWidth();
		if (mIsInit) {
			return;
		}

		mPaint = getPaint();
		mPaint.setDither(true);// 防抖动
		mText = getText().toString();
		mTextLength = mPaint.measureText(mText);
		mTextColor = getTextColors().getDefaultColor();

		mStep = calcuStep();
		temp_view_plus_text_length = mTextLength;
		temp_view_plus_text_two_length = mTextLength * 2;
		mY = getTextSize() + getPaddingTop() + mOffsetY;

		mIsInit = true;
	}

	protected float calcuStep() {
		return mTextLength;
	}

	void initViewWidth() {
		if (mViewWidth <= 0) {
			mViewWidth = getWidth();
			ViewGroup.LayoutParams params = getLayoutParams();
			if (params != null) {
				mViewWidth = params.width;
			}
			if (mViewWidth < 0) {
				mViewWidth = getMeasuredWidth();
			}
		}
	}

	public void setOffsetY(int y) {
		mOffsetY = y;
		mY += y;
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

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

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
		initPaint();
		invalidate();
	}

	public void stopMarquee() {
		setStart(false);
		mMarqueeStart = false;
		mIsFirst = true;
		mStep = calcuStep();
		mHandler.removeMessages(MESSAGE_DRAW);
		invalidate();
	}

	void reset() {
		setInit(false);
		mViewWidth = -1;
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mMarqueeStart = true;
			invalidate();
		}
	};
}
