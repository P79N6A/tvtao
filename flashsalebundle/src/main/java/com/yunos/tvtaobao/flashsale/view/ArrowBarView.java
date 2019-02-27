/**
*  Copyright (C) 2015 The ALI OS Project
*
*  Version     Date            Author
*  
*             2015-4-19       lizhi.ywp
*
*/
package com.yunos.tvtaobao.flashsale.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;


public class ArrowBarView extends View {
	/** 左箭头或者上箭头 */
	public final static byte ARROW_LEFT = 0;
	public final static byte ARROW_RIGHT = 1;
	public final static boolean DEBUG = false;
	
	private int mWidth;
	private Rect mClientRect;
	private int mMarginWidth;
	private int mArrowWidth;
	private int mArrowHeight;
	private int mArrowOffset;
	
	private Paint mTextPaint = new Paint();

	final private ArrowBarParam mSrcParam = new ArrowBarParam();
	private ArrowBarParam mDrawParam = mSrcParam;

	public ArrowBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView();
	}

	public ArrowBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
	}

	public ArrowBarView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	private void initView() {
		mWidth =  AppConfig.ARROWBAR_WIDTH;
		mMarginWidth = AppConfig.ARROWBAR_MARGIN;
		mArrowWidth = AppConfig.ARROW_WIDTH;
		mArrowHeight = AppConfig.ARROW_HEIGHT;
		mArrowOffset = AppConfig.ARROW_OFFSET;
		mTextPaint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Align.CENTER);
	}

	private void drawBackground() {
		if (mDrawParam.mBackgroundResId > 0) {
			super.setBackgroundResource(mDrawParam.mBackgroundResId);
		} else {
			super.setBackgroundColor(mDrawParam.mBackgroundColor);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		ArrowBarParam param = mDrawParam;

		if (null == param) {
			return;
		}
		/** 绘制箭头相关内容 */
		if (null == mClientRect) {
			mClientRect = new Rect();
		}
		mClientRect.set(0, 0, super.getWidth(), super.getHeight());
		boolean focus = super.hasFocus();
		int centerY = mClientRect.centerY();
		int startX;
		Align align;
		
		if (ARROW_LEFT == param.mArrowType) {
			mClientRect.left += mMarginWidth;
			mClientRect.right = mClientRect.left + mArrowWidth;			
			startX = mClientRect.left + mArrowOffset;
			align = Align.LEFT;
		} else if (ARROW_RIGHT == param.mArrowType) {
			mClientRect.right -= mMarginWidth;
			mClientRect.left = mClientRect.right - mArrowWidth;			
			startX = mClientRect.right - mArrowOffset;		
			align = Align.RIGHT;
		} else {
			return;
		}

		Drawable arrowDrawble = getArrowDrawable(super.getContext(),
				param.mArrowType, focus);
		if (null != arrowDrawble) {
			int halfHeight = mArrowHeight / 2;

			mClientRect.top = centerY - halfHeight;
			mClientRect.bottom = centerY + halfHeight;
			arrowDrawble.setBounds(mClientRect);
			if( DEBUG){
				canvas.drawRect(mClientRect, mTextPaint);
			}
		
			arrowDrawble.draw(canvas);
		}
		drawText(param, canvas, startX, centerY, align);
		if(DEBUG){
			canvas.drawLine(0, super.getHeight()/2, super.getWidth(), super.getHeight()/2, mTextPaint);
			canvas.drawLine(super.getWidth()/2, 0, super.getWidth()/2, super.getHeight(), mTextPaint);
			
			canvas.drawText("(" + super.getWidth() + "," + super.getHeight() + ")", super.getWidth()/2, super.getHeight()/2, mTextPaint);
		}
	}

	private void drawText(ArrowBarParam param, Canvas canvas, int x,
                          int centerY, Align align) {
		if (!TextUtils.isEmpty(param.mTextInfo)) {
			mTextPaint.setTextSize(param.mFontSize);
			mTextPaint.setColor(param.mTextColor);
			mTextPaint.setTextAlign(align);
			
			FontMetrics fm = mTextPaint.getFontMetrics();

			int baseline = (int) (centerY - (fm.bottom + fm.top) / 2);

			canvas.drawText(param.mTextInfo, x, baseline, mTextPaint);
		}
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		setMeasuredDimension(mWidth, heightSpecSize);
	}

	/**
	 * This method set arrow relative content
	 * 
	 * 
	 * @param arrowType
	 *            the arrow type.
	 * @param backgroundId
	 *            id of the background resource.
	 * @param backgroundColor
	 *            color of the background resource.
	 * @param textId
	 *            id of the text resource.
	 * @param text
	 *            string of the text.
	 * @param textColor
	 *            color of the text.
	 * @param fontSize
	 *            font size of the text.
	 * 
	 */
	private void setContent(byte arrowType, int backgroundId,
                            int backgroundColor, int textId, String text, int textColor,
                            int fontSize) {
		if (textId > 0) {
			text = super.getResources().getString(textId);
		}
		mSrcParam.setParam(arrowType, backgroundId, backgroundColor, text,
				textColor, fontSize);
		drawBackground();

	}

	public void setContent(byte arrowType, int backgroundId, int textId,
			int textColor, int fontSize) {
		setContent(arrowType, backgroundId, -1, textId, null, textColor,
				fontSize);
	}

	public void setContentBackgroundColor(byte arrowType, int backgroundColor,
			int textId, int textColor, int fontSize) {
		setContent(arrowType, -1, backgroundColor, textId, null, textColor,
				fontSize);
	}

	private ArrowBarParam mTmp;
	
	/**
	 * This method reset arrow info
	 * 
	 * 
	 * @param other
	 *            the arrow type.
	 * 
	 **/
	public void reset(ArrowBarView other) {
		ArrowBarParam param = (null != other) ? other.mDrawParam : null;
		if (null != param) {
			if( null == mTmp){
				mTmp = new ArrowBarParam();
			}
			mTmp.mArrowType = param.mArrowType;
			mTmp.mBackgroundColor = mSrcParam.mBackgroundColor;
			mTmp.mBackgroundResId = mSrcParam.mBackgroundResId;
			mTmp.mFontSize = param.mFontSize;
			mTmp.mTextColor  = param.mTextColor;
			mTmp.mTextInfo = param.mTextInfo;
					
			mDrawParam = mTmp;
			drawBackground();
		}
	}

	public void restore() {
		if (mDrawParam != mSrcParam) {
			mDrawParam = mSrcParam;
			drawBackground();
			invalidate();
		}
	}

	private class ArrowBarParam {
		private byte mArrowType;
		private int mBackgroundResId = -1;
		private int mBackgroundColor;
		private String mTextInfo;
		private int mTextColor;
		private int mFontSize;

		public void setParam(byte arrowType, int backgroundId,
                             int backgroundColor, String text, int textColor, int fontSize) {
			mArrowType = arrowType;
			mBackgroundResId = backgroundId;
			mBackgroundColor = backgroundColor;
			mTextInfo = text;
			mTextColor = textColor;
			mFontSize = fontSize;
		}

	}

	/**
	 * 箭头资源的管理
	 **/
	private final static int[] ARROW_RESOURCE_ID = { R.drawable.arrow_left,
			R.drawable.arrow_left, R.drawable.arrow_right,
			R.drawable.arrow_right };
	private final static Drawable[] ARROW_DRAWABLE = new Drawable[ARROW_RESOURCE_ID.length];

	/**
	 * 此函数必须在主线程调用，用来读取箭头不同状态下的资源
	 * 
	 * @author mty
	 * @param con,type,hasFocus
	 * @return
	 */
	public static Drawable getArrowDrawable(Context con, byte type,
                                            boolean hasFocus) {
		int index = type << 1;

		if (hasFocus) {
			index++;
		}
		if (index < 0 || index >= ARROW_RESOURCE_ID.length) {
			return null;
		}
		Drawable drawble = ARROW_DRAWABLE[index];
		if (null == drawble) {
			drawble = con.getResources().getDrawable(ARROW_RESOURCE_ID[index]);
			ARROW_DRAWABLE[index] = drawble;
		}
		return drawble;

	}
}
