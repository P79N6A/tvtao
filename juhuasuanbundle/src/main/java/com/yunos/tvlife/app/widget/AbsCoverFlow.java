/*
 * Copyright 2013 Alibaba Group.
 */
package com.yunos.tvlife.app.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Adapter;

import com.yunos.tvlife.lib.HorizontalPosInfo;

/**
 * CoverFlow控件，类似抽屉的列表控件；
 * <p>
 * Items进行透视偏移，越靠近中心角度越小，中间item为正面；
 * </p>
 * <p>
 * 需要设置paddingTop和paddingBottom扩展selector和item标题的绘制空间；
 * </p>
 * <p>
 * 可以通过{@link Adapter}的getItem传入对应Item的title，绘制出item的标题；
 * </p>
 * <p>
 * 通过setSelectorPadding(l,t,r,b)，b中传负值实现带阴影的选择框效果
 * </p>
 */
public class AbsCoverFlow extends Gallery {
	private static final String TAG = "CoverFlow";

	private static final int DEFAULT_IMAGE_WIDTH = 200;

	private static final int DEFAULT_IMAGE_HEIGHT = 200;

	private static final float DEFAULT_IMAGE_REFLECTION_RATIO = 0.3f;

	private static final int DEFAULT_REFLECTION_GAP = 4;

	private static final int DEFAULT_SPACING = 0;

	private static final int DEFULT_SELECTOR_PADDING_LEFT = 10;

	private static final int DEFULT_SELECTOR_PADDING_TOP = 10;

	private static final int DEFULT_SELECTOR_PADDING_RIGHT = 10;

	private static final int DEFULT_SELECTOR_PADDING_BOTTOM = 10;

	private static final int MAX_SHOW_COVER_FLOW_TEXT_COUNT = 2;

	// default max line of cover flow text
	private static final int DEFAULT_MAX_COVERFLOW_TEXT_LINE = 2;

	private static final boolean DBG = false;
	private static final int DEFAULT_MAX_COVERFLOW_TEXT_CACHE_COUNT = 100;

	private static final int TEXT_SHADOW_COLOR = 0xff0099ff;
	// private static final int MAX_COVERFLOW_TEXT_LINE = 2;

	/**
	 * Graphics Camera used for transforming the matrix of ImageViews.
	 */
	private final Camera mCamera = new Camera();

	/**
	 * The maximum angle the Child ImageView will be rotated by.
	 */
	int mMaxRotationAngle = 60;

	/**
	 * The maximum zoom on the centre Child.
	 */
	private int mMaxZoom = -120;

	/**
	 * The Centre of the Coverflow.
	 */
	int mCoveflowCenter;

	/** The image height. */
	private int imageHeight;

	/** The image width. */
	private int imageWidth;

	/** The reflection gap. */
	private int reflectionGap;

	/** The with reflection. */
	private boolean withReflection;

	/** The image reflection ratio. */
	private float imageReflectionRatio;

	private Drawable mSelector;
	private Drawable mDividerDrawable;
	private int mDividerHeight;

	private int mCoverFlowSelectedTextColor;
	private int mCoverFlowTextColor;
	private boolean forceFocus;
	// @Deprecated
	int mSelectorBorderWidth;
	// @Deprecated
	int mSelectorBorderHeight;
	// LruCache<Integer, String[]> cc = new LruCache(50);
	protected int mCurrentSelectedPosition = -1;

	LruCache<Integer, String[]> mCoverFlowBreakTextCache = new LruCache(DEFAULT_MAX_COVERFLOW_TEXT_CACHE_COUNT);
	TextPaint mTextPaint;

	/**
	 * Indicates whether the list selector should be drawn on top of the
	 * children or behind
	 */
	boolean mDrawSelectorOnTop = false;

	/**
	 * set a exactly selected size
	 */
	Rect mExactlyUserSelectedRect;

	private int mCoverFlowTextSpacing;

	private float mCoverFlowTextSize;

	private int mCoverFlowTextMaxLine;

	private int mCoverFlowTextLineHeight;

	private int mCoverflowBaselineHeight;// text matrix top(a negative value)

	MyHandler mHandler = new MyHandler();
	int mMarqueePos = -1;

	class MyHandler extends Handler {
		public MyHandler() {
		}

		public MyHandler(Looper L) {
			super(L);
		}

		// 子类必须重写此方法,接受数据
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == MyHandler.MSG_MARQUEE_START) {
				setUseMarquee(true);
				postInvalidate();
			} else if (msg.what == MyHandler.MSG_MARQUEE_END) {

			} else {
				super.handleMessage(msg);
			}
		}

		static final int MSG_MARQUEE_START = 1;
		static final int MSG_MARQUEE_END = 2;
	}

	private boolean mIsUseMarquee = false;

	private void setUseMarquee(boolean isUseMarquee) {
		mIsUseMarquee = isUseMarquee;
	}

	private void startMarqueeTickTime() {
		Message msg = Message.obtain(mHandler, MyHandler.MSG_MARQUEE_START);

		// 删除
		removeMarqueeTickTime();

		// 2000ms后发送信息
		mHandler.sendMessageDelayed(msg, 2000);
	}

	private void removeMarqueeTickTime() {
		mHandler.removeMessages(MyHandler.MSG_MARQUEE_START);
	}

	/**
	 * 返回图片的高度
	 * 
	 * @return 图片高度
	 */
	public float getImageHeight() {
		return imageHeight;
	}

	/**
	 * 设置图片高度（使用{@link AbsCoverFlowAdapter}时设置item的高度）
	 * 
	 * @param imageHeight
	 *            图片高度
	 */
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	/**
	 * 返回图片宽度.
	 * 
	 * @return 图片宽度
	 */
	public float getImageWidth() {
		return imageWidth;
	}

	/**
	 * 设置图片宽度（使用{@link AbsCoverFlowAdapter}时设置item的宽度）.
	 * 
	 * @param imageWidth
	 *            图片宽度
	 */
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	/**
	 * 返回阴影区域到item的距离.
	 * 
	 * @return 阴影区域到item的距离
	 */
	public float getReflectionGap() {
		return reflectionGap;
	}

	/**
	 * 设置阴影区域到item的距离.
	 * 
	 * @param reflectionGap
	 *            阴影区域到item的距离
	 */
	public void setReflectionGap(int reflectionGap) {
		this.reflectionGap = reflectionGap;
	}

	/**
	 * 返回是否用了阴影效果.
	 * 
	 * @return true/false 是否用了阴影效果
	 */
	public boolean isWithReflection() {
		return withReflection;
	}

	/**
	 * 设置是否使用阴影效果.
	 * 
	 * @param withReflection
	 *            true/false 是否用了阴影效果
	 */
	public void setWithReflection(final boolean withReflection) {
		this.withReflection = withReflection;
	}

	/**
	 * 设置图片阴影的宽度比.
	 * 
	 * @param imageReflectionRatio
	 *            图片阴影的宽度比
	 */
	public void setImageReflectionRatio(final float imageReflectionRatio) {
		this.imageReflectionRatio = imageReflectionRatio;
	}

	/**
	 * 返回图片阴影的宽度比.
	 * 
	 * @return 图片阴影的宽度比
	 */
	public float getImageReflectionRatio() {
		return imageReflectionRatio;
	}

	public AbsCoverFlow(final Context context) {
		super(context);
		setStaticTransformationsEnabled(true);

		mTextOffsetMarquee = 0;
	}

	public AbsCoverFlow(final Context context, final AttributeSet attrs) {
		this(context, attrs, android.R.attr.galleryStyle);

	}

	public AbsCoverFlow(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		parseAttributes(context, attrs);
		setStaticTransformationsEnabled(true);

		mTextOffsetMarquee = 0;
	}

	/**
	 * 返回选择框的drawable
	 * 
	 * @return 选择框的drawable
	 */
	public Drawable getSelector() {
		return mSelector;
	}

	/**
	 * 返回最大的透视角度.
	 * 
	 * @return 最大的透视角度
	 */
	public int getMaxRotationAngle() {
		return mMaxRotationAngle;
	}

	/**
	 * 设置adapter（适配器）.
	 * <p>
	 * 可以传自定义的{@link Adapter},或者{@link AbsCoverFlowAdapter};
	 * </p>
	 * 
	 * @param adapter
	 *            自定义的{@link Adapter},或者{@link AbsCoverFlowAdapter}
	 */
	public void setAdapter(Adapter adapter) {
		if (adapter instanceof AbsCoverFlowAdapter) {
			final AbsCoverFlowAdapter coverAdapter = (AbsCoverFlowAdapter) adapter;
			coverAdapter.setWidth(imageWidth);
			coverAdapter.setHeight(imageHeight);
			if (withReflection) {
				final ReflectingImageAdapter reflectAdapter = new ReflectingImageAdapter(coverAdapter);
				reflectAdapter.setReflectionGap(reflectionGap);
				reflectAdapter.setWidthRatio(imageReflectionRatio);
				reflectAdapter.setWidth(imageWidth);
				reflectAdapter.setHeight(imageHeight * (1 + imageReflectionRatio));
				super.setAdapter(reflectAdapter);
			} else {
				super.setAdapter(coverAdapter);
			}
		} else {
			super.setAdapter(adapter);
		}
	}

	// private void setTextArray(HashMap<Integer, String> textArray){
	// if(textArray != null && textArray.size() > 0){
	// if(mTextPaint == null){
	// mTextPaint = new TextPaint();
	// mTextPaint.setTextSize(AuiResourceFetcher.getResources(getContext()).getDimension(yunos.R.dimen.tui_text_size_2));
	// mTextPaint.setColor(mCoverFlowTextColor);
	// }
	// }
	// }

	// public void setTextArrayOnCoverFlow(HashMap<Integer, String> list) {
	// mCoverFlowTextDisplayList = list;
	// setTextArray(mCoverFlowTextDisplayList);
	// }

	/**
	 * 设置最大的透视角度.
	 * 
	 * @param maxRotationAngle
	 *            最大的透视角度
	 */
	public void setMaxRotationAngle(final int maxRotationAngle) {
		mMaxRotationAngle = maxRotationAngle;
	}

	/**
	 * 返回最大的放大倍数.
	 * 
	 * @return 最大的放大倍数
	 */
	public int getMaxZoom() {
		return mMaxZoom;
	}

	/**
	 * 设置最大的放大倍数.
	 * 
	 * @param maxZoom
	 *            最大的放大倍数
	 */
	public void setMaxZoom(final int maxZoom) {
		mMaxZoom = maxZoom;
	}

	/**
	 * Get the Centre of the Coverflow.
	 * 
	 * @return The centre of this Coverflow.
	 */
	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	/**
	 * Get the Centre of the View.
	 * 
	 * @return The centre of the given view.
	 */
	static int getCenterOfView(final View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	/**
	 * Called when dataset changed
	 */
	public void reset() {
		mCoverFlowBreakTextCache.evictAll();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setStaticTransformationsEnabled(boolean)
	 */
	@Override
	protected boolean getChildStaticTransformation(final View child, final Transformation t) {

		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		int rotationAngle = 0;

		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		if (childCenter == mCoveflowCenter) {
			transformImageBitmap(child, t, 0);
		} else {
			rotationAngle = (int) ((float) (mCoveflowCenter - childCenter) / childWidth * mMaxRotationAngle / 2);
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = rotationAngle < 0 ? -mMaxRotationAngle : mMaxRotationAngle;
			}
			transformImageBitmap(child, t, rotationAngle);
		}
		return true;
	}

	boolean mTextInsideDraw = true;

	protected void setDrawTextInside(boolean in) {
		mTextInsideDraw = in;
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		// TODO Auto-generated method stub
		// return super.drawChild(canvas, child, drawingTime);

		boolean drawChild = super.drawChild(canvas, child, drawingTime);

		if (mTextInsideDraw) {
			drawChildInside(canvas, child, drawingTime);
		}

		return drawChild;
	}

	protected void drawChildInside(Canvas canvas, View child, long drawingTime) {
		int selPos = getRelSelectedPosition();
		if (mCurrentSelectedPosition >= 0) {
			selPos = mCurrentSelectedPosition;
		}
		int pos = getPositionForView(child);
		if (selPos == pos) {
			if (mMarqueePos != pos) {
				mMarqueePos = pos;
				endMarquee();
				startMarquee();
			}
		}
		// if ((selPos == pos)) {
		// if (mIsDrawShadow) {
		// drawShadow(canvas, child, drawingTime);
		// }
		// }
		if (mEllipsize == null) {
			drawCoverFlowText(canvas, child, drawingTime, pos, child.getScaleX(), child.getScaleY());
		} else {
			// scrolling
			if (isCoverFlowScrolling()) {
				mTextOffsetMarquee = 0;
				drawCoverFlowTextInTruncateMode(canvas, child, drawingTime, pos, true, child.getScaleX(), child.getScaleY());
			} else {
				// draw scrolling text
				drawCoverFlowTextInTruncateMode(canvas, child, drawingTime, pos, false, child.getScaleX(), child.getScaleY());
			}
		}

	}

	private void drawShadow(Canvas canvas, View child, long drawingTime) {
		int index = getSelectedItemPosition();

		Rect rect = new Rect();

		rect = getRectOfView(child);

		if (child != null) {
			canvas.save();

			renderChildShadowPre(canvas, child, index, 255, rect);// 0-->255
			canvas.restore();

			renderChildShadowNext(canvas, child, index, 255, rect);// 0---255
		}

		/*
		 * int l = child.getLeft(); int r = child.getRight(); int t =
		 * child.getTop(); int b = child.getBottom();
		 * 
		 * float[] vector = new float[8];
		 * 
		 * vector[0] = l; vector[1] = t;
		 * 
		 * vector[2] = l; vector[3] = b;
		 * 
		 * vector[4] = r; vector[5] = b;
		 * 
		 * vector[6] = r; vector[7] = t;
		 * 
		 * Paint paint = new Paint(); paint.setAlpha(255);
		 * paint.setColor(Color.WHITE);
		 * 
		 * //canvas.drawLines(vector, paint);
		 * 
		 * canvas.drawLine(vector[0], vector[1], vector[2], vector[3], paint);
		 * canvas.drawLine(vector[2], vector[3], vector[4], vector[5], paint);
		 * canvas.drawLine(vector[4], vector[5], vector[6], vector[7], paint);
		 * canvas.drawLine(vector[6], vector[7], vector[0], vector[1], paint);
		 */
	}

	private Rect mTextClipRect = new Rect();
	private volatile long mStarMarqueetTime = 0;

	int mTextOffsetMarquee = 0;
	int mTextSpeedMarquee = 2;

	public enum TruncateAt {
		START, MIDDLE, END, MARQUEE,
		/**
		 * @hide
		 */
		END_SMALL
	}

	TruncateAt mEllipsize;

	public void setEllipsize(AbsCoverFlow.TruncateAt where) {
		// TruncateAt is an enum. != comparison is ok between these singleton
		// objects.
		mEllipsize = where;
	}

	private void drawCoverFlowTextInTruncateMode(Canvas canvas, View child, long drawingTime, int pos, boolean isScrolling, float scaleX,
                                                 float scaleY) {

		if (!needDrawCoverFlowText(pos) || mAdapter == null || mAdapter.getItem(pos) == null) {
			return;
		}
		String text = "";
		Object o = mAdapter.getItem(pos);
		try {
			text = (String) o;
		} catch (Exception e) {
			if (DBG)
				Log.d(TAG, "CoverFlow Adapter getItem(int position) need return String value");
			// throw new
			// Exception("CoverFlow Adapter getItem(int position) need return String value");
		}

		if (text.isEmpty()) {
			return;
		}

		initialDrawTextEnv(pos, Math.min(scaleX, scaleY));

		Rect rect = getCoverflowTextRect(child, text);
		int width = child.getWidth();

		int space = mCoverFlowTextSpacing;
		if (DBG)
			Log.d(TAG, "drawCoverFlowText text = " + text + " , rect = " + rect);
		String[] breakArray = getTextInTruncateMode(text, pos);

		drawTextInTruncateMode(canvas, breakArray[0], rect, width, pos, isScrolling, scaleX, scaleY);
	}

	private void drawCoverFlowText(Canvas canvas, View child, long drawingTime, int pos, float scaleX, float scaleY) {

		if (!needDrawCoverFlowText(pos) || mAdapter == null || mAdapter.getItem(pos) == null) {
			return;
		}
		String text = "";
		Object o = mAdapter.getItem(pos);
		try {
			text = (String) o;
		} catch (Exception e) {
			if (DBG)
				Log.d(TAG, "CoverFlow Adapter getItem(int position) need return String value");
			// throw new
			// Exception("CoverFlow Adapter getItem(int position) need return String value");
		}
		// if(o instanceof String){
		// text = (String)o;
		// }
		if (text.isEmpty()) {
			return;
		}

		initialDrawTextEnv(pos, Math.max(scaleX, scaleY));

		Rect rect = getCoverflowTextRect(child, text);
		int width = child.getWidth();

		int space = mCoverFlowTextSpacing;
		if (DBG)
			Log.d(TAG, "drawCoverFlowText text = " + text + " , rect = " + rect);
		String[] breakArray = getText(text, width, pos);

		drawText(canvas, breakArray, rect, width, pos, scaleX, scaleY);
	}

	private void initialDrawTextEnv(int pos, float textScale) {
		if (mTextPaint == null) {
			mTextPaint = new TextPaint();
			mTextPaint.setTextSize(mCoverFlowTextSize);
			FontMetricsInt fMatrix = new FontMetricsInt();
			mTextPaint.getFontMetricsInt(fMatrix);
			mCoverFlowTextLineHeight = fMatrix.bottom - fMatrix.top;
			mCoverflowBaselineHeight = fMatrix.top;
		}

		mTextPaint.setTextAlign(Align.CENTER);
		mTextPaint.setTextSize(mCoverFlowTextSize * textScale);
		boolean isSelected = false;
		if (mCurrentSelectedPosition >= 0) {
			isSelected = (mCurrentSelectedPosition == pos);
		} else {
			isSelected = (pos == getRelSelectedPosition());
		}

		if (isSelected && (this.gainFocus || this.forceFocus)) {
			mTextPaint.setColor(mCoverFlowSelectedTextColor);
			if (mIsDrawShadow) {
				mTextPaint.setShadowLayer(20, 0, 0, TEXT_SHADOW_COLOR);
			}
		} else {
			mTextPaint.setColor(mCoverFlowTextColor);
			if (mIsDrawShadow) {
				mTextPaint.setShadowLayer(0, 0, 0, getResources().getColor(android.R.color.transparent));
			}
		}
	}

	private String[] getText(String text, int width, int pos) {
		String[] breakArray = mCoverFlowBreakTextCache.get(pos);
		if (breakArray == null) {
			breakArray = getBreakStringArray(text, width);
			mCoverFlowBreakTextCache.put(pos, breakArray);
		}

		return breakArray;
	}

	private String[] getTextInTruncateMode(String text, int pos) {
		String[] breakArray = mCoverFlowBreakTextCache.get(pos);
		if (breakArray == null) {
			breakArray = new String[1];
			breakArray[0] = "";
			breakArray[0] += text;
			mCoverFlowBreakTextCache.put(pos, breakArray);
		}

		return breakArray;
	}

	/**
	 * 自定义focus状态
	 * 
	 * @param forceGainFocus
	 */
	public void setForceGainFocus(boolean forceFocus) {
		this.forceFocus = forceFocus;
		mSelectedPosition = getSelectedItemPosition();
		Log.d(TAG, "setForceGainFocus forceFocus = " + forceFocus + ", mSelectedPosition = " + mSelectedPosition);
		if (forceFocus) {
			positionSelector(mSelectedPosition, getSelectedView());
			startMarquee();
		} else {
			clearSelectorRect();
			endMarquee();
		}
		invalidate();
	}

	private void clearSelectorRect() {
		mSelectorRect.setEmpty();
	}

	private Rect getCoverflowTextRect(View child, String text) {
		Rect r = new Rect();
		r.left = child.getLeft();
		r.top = child.getTop();
		r.right = child.getRight();
		r.bottom = child.getBottom();
		return r;
	}

	// private void drawText(Canvas canvas, String text, int x, int y){
	// canvas.drawText(text, x, y, mTextPaint);
	// }

	/**
	 * 设置coverflow的普通文字颜色
	 * 
	 * @param color
	 *            coverflow的普通文字颜色
	 */
	public void setCoverFlowTextColor(int color) {
		mCoverFlowTextColor = color;
	}

	/**
	 * 设置coverflow的选中状态下的文字颜色
	 * 
	 * @param color
	 *            coverflow的选中状态下的文字颜色
	 */
	public void setCoverFlowSelectedTextColor(int color) {
		mCoverFlowSelectedTextColor = color;
	}

	/**
	 * 设置CoverFlow文字和item之间的间隔
	 * 
	 * @param spacing
	 *            CoverFlow文字和item之间的间隔
	 */
	public void setCoverFlowTextSpacing(int spacing) {
		mCoverFlowTextSpacing = spacing;
	}

	/**
	 * 设置CoverFow文字的字体大小
	 * 
	 * @param size
	 *            CoverFow文字的字体大小
	 */
	public void setCoverFlowTextSize(float size) {
		mCoverFlowTextSize = size;
	}

	/**
	 * 设置CoverFow文字的最大行数，默认是2行，超过2行以"..."结束
	 * 
	 * @param maxLine
	 *            CoverFow文字的最大行数
	 */
	public void setCoverFlowTextMaxLine(int maxLine) {
		mCoverFlowTextMaxLine = maxLine;
	}

	/**
	 * support two line draw text, some special char, such as '\n' draw need be
	 * improve later
	 */
	private void drawText(Canvas canvas, String[] text, Rect rect, int width, int pos, float scaleX, float scaleY) {
		// text.replaceAll("\n", "");
		int x = rect.left;
		int y = rect.bottom + mCoverFlowTextSpacing;
		// FontMetricsInt fMatrix = new FontMetricsInt();
		// mTextPaint.getFontMetricsInt(fMatrix);
		// int h = fMatrix.bottom - fMatrix.top;
		int h = mCoverFlowTextLineHeight;
		// String[] breakStringArray = getBreakStringArray(text, width);

		int relSlectedPos = getRelSelectedPosition();
		if (mCurrentSelectedPosition >= 0) {
			relSlectedPos = mCurrentSelectedPosition;
		}

		if (relSlectedPos == pos) {
			y = (int) (rect.bottom + mCoverFlowTextSpacing * Math.min(scaleX, scaleY) + rect.height() * (scaleY - 1.0) / 2);
		}

		int lineCount = text.length;
		for (int i = 0; i < lineCount; i++) {
			String lineText = text[i];
			if (DBG)
				Log.d(TAG, ", lineText = " + lineText + ", i = " + i);
			if (lineText == null || lineText.isEmpty()) {
				// not draw
			} else {
				canvas.drawText(lineText, x + width / 2, y - mCoverflowBaselineHeight + 0.5f + h * i, mTextPaint);// draw
																													// from
																													// baseline
																													// y
			}
		}

		/*
		 * int displayWidth = (int) StaticLayout.getDesiredWidth(firstLine, 0,
		 * firstLine.length(), mTextPaint); canvas.drawText(firstLine, x +
		 * width/2, y - fMatrix.top + 0.5f, mTextPaint);//draw from baseline y
		 * if(!secondLine.isEmpty()){ displayWidth = (int)
		 * StaticLayout.getDesiredWidth(secondLine, 0, secondLine.length(),
		 * mTextPaint); canvas.drawText(secondLine, x + width/2, y - fMatrix.top
		 * + 0.5f + h, mTextPaint);//draw from baseline y }
		 */
	}

	private long now() {
		return SystemClock.uptimeMillis();
	}

	private void startMarquee() {
		// 只有当绘制文字是跑马灯绘制的时候，才启动
		if ((mEllipsize != null) && (mEllipsize == TruncateAt.MARQUEE)) {
			mStarMarqueetTime = now();//

			mTextOffsetMarquee = 0;

			mIsUseMarquee = false;

			startMarqueeTickTime();
		}
	}

	private void endMarquee() {
		// 只有当绘制文字是跑马灯绘制的时候，才关闭
		if ((mEllipsize != null) && (mEllipsize == TruncateAt.MARQUEE)) {
			mIsUseMarquee = false;

			removeMarqueeTickTime();
		}
	}

	protected void OnScrolling(boolean isScrolling) {
		if (isScrolling) {
			// 开始滚动的时候，设置mIsUseMarquee状态，并且清除计时器
			endMarquee();
		} else {
			// 停止滚动的时候，开始计时器
			startMarquee();
		}

		// Log.v(TAG, "OnScrolling" + isScrolling +
		// " thsi ththitti mStarMarqueetTime = " + mStarMarqueetTime + " now = "
		// + now());

		super.OnScrolling(isScrolling);
	}

	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		if (gainFocus) {
			// 获得焦点的时候，开始跑马灯计时
			startMarquee();
		} else {
			// 失去焦点的时候，关闭跑马灯计时，并修改跑马灯设计
			endMarquee();
		}

		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	private void drawTextInTruncateMode(Canvas canvas, String lineText, Rect rect, int width, int pos, boolean isScrolling, float scaleX,
                                        float scaleY) {
		int x = rect.left;
		int y = rect.bottom + mCoverFlowTextSpacing;
		int h = mCoverFlowTextLineHeight;

		canvas.save();

		// Paint paint = new Paint();
		// paint.setColor(Color.WHITE);

		mTextClipRect.left = rect.left;
		mTextClipRect.right = rect.right;
		mTextClipRect.top = rect.bottom;
		mTextClipRect.bottom = getBottom();// mTextClipRect.top +
											// mCoverFlowTextSpacing +
											// mCoverFlowTextLineHeight;

		// canvas.drawRect(mTextClipRect, paint);

		// set clip rect
		canvas.clipRect(mTextClipRect);

		if (DBG)
			Log.d(TAG, ", lineText = " + lineText);

		int interval = width / 2;

		int xStart;

		if (!(lineText == null || lineText.isEmpty())) {
			// 文字的实际宽度
			int totalWidth = (int) StaticLayout.getDesiredWidth(lineText, 0, lineText.length(), mTextPaint);

			if (mEllipsize != null) {
				if (mEllipsize == TruncateAt.START) {
					xStart = x;
					mTextPaint.setTextAlign(Align.LEFT);
					canvas.drawText(lineText, xStart, y - mCoverflowBaselineHeight + 0.5f, mTextPaint);// draw
																										// from
																										// baseline
																										// y
				} else if (mEllipsize == TruncateAt.END) {
					xStart = x + width;
					mTextPaint.setTextAlign(Align.RIGHT);
					canvas.drawText(lineText, xStart, y - mCoverflowBaselineHeight + 0.5f, mTextPaint);// draw
																										// from
																										// baseline
																										// y
				} else if (mEllipsize == TruncateAt.MIDDLE) {
					xStart = x + width / 2;
					mTextPaint.setTextAlign(Align.CENTER);
					canvas.drawText(lineText, xStart, y - mCoverflowBaselineHeight + 0.5f, mTextPaint);// draw
																										// from
																										// baseline
																										// y
				} else {
					int relSlectedPos = getRelSelectedPosition();
					if (mCurrentSelectedPosition >= 0) {
						relSlectedPos = mCurrentSelectedPosition;
					}

					if (pos == relSlectedPos) {
						y = (int) (rect.bottom + mCoverFlowTextSpacing * Math.min(scaleX, scaleY) + rect.height() * (scaleY - 1.0) / 2);
					}

					if (totalWidth < width) {
						// 文字比较短，设置字体为居中模式
						mTextPaint.setTextAlign(Align.CENTER);
						xStart = x + width / 2;

						canvas.drawText(lineText, xStart, y - mCoverflowBaselineHeight + 0.5f, mTextPaint);// draw
																											// from
																											// baseline
																											// y
					} else {
						// 文字比较长，设置靠左边，然后跑马灯的模式
						mTextPaint.setTextAlign(Align.LEFT);
						xStart = x;

						if (isScrolling) {// when scrolling direct draw not
											// marquee
							canvas.drawText(lineText, xStart, y - mCoverflowBaselineHeight + 0.5f, mTextPaint);// draw
																												// from
																												// baseline
																												// y
						} else {
							if (pos == relSlectedPos && (this.gainFocus || this.forceFocus)) {
								if (mIsUseMarquee) {
									// Log.v(TAG, "mStarMarqueetTime = " +
									// mStarMarqueetTime + "now = " + now());

									if ((now() - mStarMarqueetTime) > 2500) {
										// next position，前进的速度
										mTextOffsetMarquee -= mTextSpeedMarquee;

										// draw head
										canvas.drawText(lineText, mTextOffsetMarquee + xStart, y - mCoverflowBaselineHeight + 0.5f,
												mTextPaint);// draw from
															// baseline y

										// draw another
										if (mTextOffsetMarquee < -(totalWidth - (width - interval))) {
											canvas.drawText(lineText, (mTextOffsetMarquee + totalWidth + interval) + xStart, y
													- mCoverflowBaselineHeight + 0.5f, mTextPaint);// draw
																									// from
																									// baseline
																									// y
										}

										// 移动超过这个循环长度，开始下一个循环
										if (mTextOffsetMarquee < -(totalWidth + interval)) {
											mTextOffsetMarquee = 0;
										}

									} else {
										canvas.drawText(lineText, xStart, y - mCoverflowBaselineHeight + 0.5f, mTextPaint);// draw
																															// from
																															// baseline
																															// y
									}

									postInvalidateDelayed(80, mTextClipRect.left, mTextClipRect.top, mTextClipRect.right,
											mTextClipRect.bottom);
								} else {
									canvas.drawText(lineText, xStart, y - mCoverflowBaselineHeight + 0.5f, mTextPaint);// draw
																														// from
																														// baseline
																														// y
								}
							} else {
								canvas.drawText(lineText, xStart, y - mCoverflowBaselineHeight + 0.5f, mTextPaint);// draw
																													// from
																													// baseline
																													// y
							}
						}
					}
				}
			}
		}

		canvas.restore();
	}

	private String[] getBreakStringArray(String text, int width) {
		final int count = mCoverFlowTextMaxLine;
		String[] breakArray = new String[count];
		int index = text.indexOf("\n");
		// replace '\n' char
		if (index > 0) {
			text.replaceAll("\n", "");
		}
		;
		int start = 0, end = 0;
		int len = text.length();
		int totalWidth = (int) StaticLayout.getDesiredWidth(text, start, len, mTextPaint);
		int scale = totalWidth / width;
		if (scale == 0) {
			breakArray[0] = text;
		} else {
			// per line contains char count
			int perLineCharCount = len / scale;

			for (int i = 0; i < count; i++) {
				int tw = (int) StaticLayout.getDesiredWidth(text, start, len, mTextPaint);
				if (tw <= width) {
					end = len;
					breakArray[i] = text.substring(start, end);
					break;
				} else {
					// get line end char, find break charIndex
					end = (i + 1) * perLineCharCount >= len ? len : (i + 1) * perLineCharCount;
					int lineEnd = getLineEndIndex(text, start, end, width);
					if (i == count - 1 && lineEnd < len) {
						// need ellipse
						breakArray[i] = text.substring(start, lineEnd - 2) + "...";
					} else {
						breakArray[i] = text.substring(start, lineEnd);
					}
					start = lineEnd;
				}
			}
		}
		return breakArray;
	}

	private int getLineEndIndex(String text, int start, int end, int width) {
		int len = text.length();

		int tw = (int) StaticLayout.getDesiredWidth(text, start, end, mTextPaint);
		if (DBG)
			Log.d(TAG, " start = " + start + ", end = " + end + ", width = " + width + ", len = " + len + ", tw = " + tw);
		if (tw > width) {
			while (tw >= width) {
				end--;
				tw = (int) StaticLayout.getDesiredWidth(text, start, end, mTextPaint);
			}
		} else {
			while (tw < width) {
				if (end < len - 1) {
					end++;
				}
				tw = (int) StaticLayout.getDesiredWidth(text, start, end, mTextPaint);
			}
		}
		return end;
	}

	// private void drawTest(Canvas canvas, View child) {
	// child.getTop();
	// int h = child.getHeight();
	// int w = child.getWidth();
	// Paint p = new Paint();
	// p.setColor(Color.WHITE);
	// canvas.drawRect(new Rect(0, h/2, w, h), p);
	//
	// }

	private void drawScaleableView(Canvas canvas, View child, long drawingTime) {
		/*
		 * int pos = getPositionForView(child); if(!mScalableViewGroup.isEmpty()
		 * && needDrawCoverFlowText(pos)){ View scalable =
		 * mScalableViewGroup.get(pos); if(scalable != null){ int x =
		 * child.getLeft(); int y = child.getHeight() + mPaddingTop +
		 * mScalableViewSpacing; Rect temp = new Rect();
		 * scalable.getDrawingRect(temp); Rect childRect = new Rect();
		 * child.getDrawingRect(childRect); Log.d(TAG,
		 * "drawScaleableView translate x = " + x + ", y = " + y +
		 * ", scalable getDrawingRect = " + temp + ", mScalableViewSpacing = " +
		 * mScalableViewSpacing + ", mPaddingTop = " + mPaddingTop +
		 * ", childRect = " + childRect); canvas.translate(x, y);
		 * scalable.draw(canvas); canvas.translate(-x, -y); //
		 * scalable.draw(canvas); } }
		 */
	}

	private boolean needDrawCoverFlowText(int pos) {
		// need show five position.
		int selPos = getRelSelectedPosition();
		int absDelta = Math.abs(pos - selPos);
		if (DBG)
			Log.d(TAG, "needDrawCoverFlowText absDelta = " + absDelta + ", selPos = " + selPos + ", pos = " + pos);
		return absDelta <= MAX_SHOW_COVER_FLOW_TEXT_COUNT;
	}

	/*
	 * private void drawSelector(Canvas canvas, View child) { int
	 * reflectionHeight = withReflection?(int)(imageHeight *
	 * imageReflectionRatio):0; int l = child.getLeft(); int r =
	 * child.getRight(); int t = child.getTop(); int b = child.getBottom() -
	 * reflectionHeight;
	 * 
	 * Paint paint = new Paint(); paint.setAntiAlias(true);
	 * paint.setColor(0x80FFFFFF); Rect rec = new Rect(l, t, r, b);
	 * canvas.drawRect(rec, paint);
	 * 
	 * mSelector.setBounds(l, t, r, b); mSelector.draw(canvas); }
	 */

	@Override
	protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 设置选择框的drawable
	 * 
	 * @param selector
	 *            选择框的drawable
	 */
	public void setSelector(Drawable selector) {
		mSelector = selector;
	}

	/**
	 * 设置选择框的资源ID，该ID由AUI进行解析
	 * 
	 * @param selectorId
	 *            选择框的资源ID
	 */
	// public void setSelector(int selectorId){
	// mSelector =
	// AuiResourceFetcher.getResources(getContext()).getDrawable(selectorId);
	// }

	/**
	 * 设置分割线的drawable，比如说书架的效果
	 * 
	 * @param drawable
	 *            分割线的drawable
	 */
	public void setDividerDrawable(Drawable drawable) {
		mDividerDrawable = drawable;
		if (mDividerDrawable != null) {
			mDividerHeight = mDividerDrawable.getIntrinsicHeight();
		} else {
			mDividerHeight = 0;
		}
	}

	/**
	 * 设置分割线的资源ID，该ID由AUI进行解析
	 * 
	 * @param drawableId
	 *            分割线的资源ID
	 */
	// public void setDividerDrawable(int drawableId){
	// Drawable d =
	// AuiResourceFetcher.getResources(getContext()).getDrawable(drawableId);
	// setDividerDrawable(d);
	// }

	// @Deprecated
	public void setSelectorBorderWidth(int borderWidth) {
		mSelectorBorderWidth = borderWidth;
	}

	// @Deprecated
	public void setSelectorBorderHeight(int borderHeight) {
		mSelectorBorderHeight = borderHeight;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		drawDivider(canvas);
		if (!mDrawSelectorOnTop) {
			drawSelector(canvas);
		}
		super.dispatchDraw(canvas);
		if (mDrawSelectorOnTop) {
			drawSelector(canvas);
		}
		// if(mScalableView != null){
		// //selected view is Moving , left is not determined
		// //use selector left instead
		// int x = mScalableView.getLeft();
		// if(mSelectorRect!= null && !mSelectorRect.isEmpty()){
		// x = mSelectorRect.left;
		// }
		// int y = mScalableView.getTop();
		// canvas.translate(x, y);
		// mScalableView.draw(canvas);
		// canvas.translate(-x, -y);
		// }
	}

	void drawDivider(Canvas canvas) {
		if (mDividerDrawable != null) {
			final Rect bounds = new Rect();
			bounds.left = getPaddingLeft();
			bounds.right = getRight() - getLeft() - getPaddingRight();
			bounds.top = getBottom() - getTop() - getPaddingBottom();
			bounds.bottom = bounds.top + mDividerHeight;
			// bounds.top = 0;
			// bounds.bottom = mDividerHeight;
			if (DBG)
				Log.d(TAG, "drawDivider bounds = " + bounds);
			drawDivider(canvas, bounds);
		}
	}

	private void drawDivider(Canvas canvas, Rect bounds) {
		// This widget draws the same divider for all children
		final Drawable divider = mDividerDrawable;

		divider.setBounds(bounds);
		divider.draw(canvas);
	}

	/**
	 * 控制是否将选择框画在Item上面.
	 * 
	 * @param onTop
	 *            true, 选择框画在Item上面； false， 选择框画在Item下面.
	 */
	public void setDrawSelectorOnTop(boolean onTop) {
		mDrawSelectorOnTop = onTop;
	}

	/**
	 * @deprecated 设置确切的选择框大小
	 * @param left
	 *            左的坐标值
	 * @param top
	 *            上的坐标值
	 * @param right
	 *            右的坐标值
	 * @param bottom
	 *            下的坐标值
	 */
	public void setExactlyUserSelectedRect(int left, int top, int right, int bottom) {
		mExactlyUserSelectedRect = new Rect(left, top, right, bottom);
	}

	/**
	 * @deprecated 清除确切的选择框的坐标
	 */
	public void clearExactlyUserSelectedRect() {
		mExactlyUserSelectedRect = null;
	}

	@Override
	public void getFocusedRect(Rect r) {
		View view = getSelectedView();
		if (view != null && view.getParent() == this) {
			// the focused rectangle of the selected view offset into the
			// coordinate space of this view.
			view.getFocusedRect(r);
			offsetDescendantRectToMyCoords(view, r);
		} else {
			mSelectorRect.setEmpty();
			// otherwise, just the norm
			// super.getFocusedRect(r);
		}
	}

	/**
	 * 绘制选择框函数
	 * 
	 * @param canvas
	 */
	protected void drawSelector(Canvas canvas) {
		if ((mSelectedPosition != INVALID_POSITION || forceFocus) && !mSelectorRect.isEmpty() && mSelector != null) {
			mSelector.setBounds(mSelectorRect);
			int deltaCenter = Math.abs(getCenterOfView(getSelectedView()) - getCenterOfCoverflow());
			float radio = 1.0f * deltaCenter / (getSelectedView().getWidth() + mSpacing);
			float alpha = SelectorAlphaChange(radio);
			mSelector.setAlpha((int) (alpha * 255));
			mSelector.draw(canvas);
		}
	}

	private float SelectorAlphaInterpolate(float startAlpha, float endAlpha, float interpolateAlpha1, float interpolateTime1,
			float interpolateAlpha2, float interpolateTime2, float value) {
		float alpha = 0.0f;
		if (value < 0.0f) {
			alpha = startAlpha;
		}

		if (value > 1.0f) {
			alpha = endAlpha;
		}

		if (value < interpolateTime1 && value >= 0.0) {
			alpha = startAlpha + (value - 0.0f) * (interpolateAlpha1 - startAlpha) / (interpolateTime1 - 0);
		}

		if (value < interpolateTime2 && value >= interpolateTime1) {
			alpha = interpolateAlpha1 + (value - interpolateTime1) * (interpolateAlpha2 - interpolateAlpha1)
					/ (interpolateTime2 - interpolateTime1);
		}

		if (value > interpolateTime2) {
			alpha = interpolateAlpha2 + (value - interpolateTime2) * (endAlpha - interpolateAlpha2) / (1.0f - interpolateTime2);
		}

		return alpha;
	}

	private float SelectorAlphaChange(float value) {

		float interpolateAlpha1 = HorizontalPosInfo.SELECTOR_TIME_FROM_DARK;
		float interpolateTime1 = HorizontalPosInfo.SELECTOR_ALPHA;

		float interpolateAlpha2 = HorizontalPosInfo.SELECTOR_ALPHA;
		float interpolateTime2 = HorizontalPosInfo.SELECTOR_TIME_FROM_DARK;

		return SelectorAlphaInterpolate(1.0f, 1.0f, interpolateAlpha1, interpolateTime1, interpolateAlpha2, interpolateTime2, value);
	}

	/**
	 * Transform the Image Bitmap by the Angle passed.
	 * 
	 * @param imageView
	 *            ImageView the ImageView whose bitmap we want to rotate
	 * @param t
	 *            transformation
	 * @param rotationAngle
	 *            the Angle by which to rotate the Bitmap
	 */
	void transformImageBitmap(final View child, final Transformation t, final int rotationAngle) {
		final int height = child.getMeasuredHeight();
		final int width = child.getMeasuredWidth();
		final int rotation = Math.abs(rotationAngle);

		mCamera.save();
		// mCamera.setLocation(0, (height / 2.0f / 72.0f), -8);
		final Matrix imageMatrix = t.getMatrix();
		mCamera.translate(0.0f, 0.0f, 120.0f);
		// As the angle of the view gets less, zoom in
		if (rotation < mMaxRotationAngle) {
			final float zoomAmount = (float) (mMaxZoom + rotation * 2);
			mCamera.translate(0.0f, 0.0f, zoomAmount);
		}

		mCamera.rotateY(rotationAngle);
		mCamera.getMatrix(imageMatrix);

		imageMatrix.preTranslate(-(width / 2.0f), -(height / 2.0f));
		imageMatrix.postTranslate((width / 2.0f), (height / 2.0f));

		mCamera.restore();
	}

	/**
	 * Parses the attributes.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	private void parseAttributes(final Context context, final AttributeSet attrs) {
		// AuiResouces.theme(context);
		// final TypedArray a =
		// AuiResourceFetcher.obtainStyledAttributes(context, attrs,
		// yunos.R.styleable.CoverFlow);
		// try {
		// imageWidth =
		// a.getDimensionPixelSize(yunos.R.styleable.CoverFlow_imageWidth,
		// DEFAULT_IMAGE_WIDTH);
		// imageHeight =
		// a.getDimensionPixelSize(yunos.R.styleable.CoverFlow_imageHeight,
		// DEFAULT_IMAGE_HEIGHT);
		// withReflection =
		// a.getBoolean(yunos.R.styleable.CoverFlow_withReflection, false);
		// imageReflectionRatio =
		// a.getFloat(yunos.R.styleable.CoverFlow_imageReflectionRatio,
		// DEFAULT_IMAGE_REFLECTION_RATIO);
		// reflectionGap =
		// a.getDimensionPixelSize(yunos.R.styleable.CoverFlow_reflectionGap,DEFAULT_REFLECTION_GAP);
		setSpacing(DEFAULT_SPACING);
		// setSelector(yunos.R.drawable.tui_bg_focus);
		setSelectorPadding(DEFULT_SELECTOR_PADDING_LEFT, DEFULT_SELECTOR_PADDING_TOP, DEFULT_SELECTOR_PADDING_RIGHT,
				DEFULT_SELECTOR_PADDING_BOTTOM);
		setDrawSelectorOnTop(true);
		setClipChildren(false);
		setClipToPadding(false);
		setCoverFlowTextColor(TUI_TEXT_COLOR_GREY);
		setCoverFlowSelectedTextColor(TUI_TEXT_COLOR_WHITE);
		setCoverFlowTextSize(TUI_TEXT_SIZE_2);
		setCoverFlowTextMaxLine(DEFAULT_MAX_COVERFLOW_TEXT_LINE);
		// } finally {
		// a.recycle();
		// }
	}

	// draw shadow

	private boolean mIsDrawShadow = false;

	private float mShadowRatio = 120.0f / 400.0f;// 180.0f/400.0f;

	private Paint mShadowPaint = new Paint();
	private Bitmap mShadowNextBitmap;
	private Bitmap mShadowPreBitmap;

	private NinePatch mShadowNextNinePatch;
	private NinePatch mShadowPreNinePatch;

	private int mShadowTopPadding = 16;
	private int mShadowBottomPadding = 25;

	/**
	 * 设置shadow的上padding； 该shadow不同于Item的阴影效果，是直接绘制于选中的项相邻的Item上，增加透视感
	 * 
	 * @param padding
	 */
	public void setShadowTopPadding(int padding) {
		mShadowTopPadding = padding;
	}

	/**
	 * 设置shadow的下padding 该shadow不同于Item的阴影效果，是直接绘制于选中的项相邻的Item上，增加透视感
	 * 
	 * @param padding
	 */
	public void setShadowBottomPadding(int padding) {
		mShadowBottomPadding = padding;
	}

	/**
	 * 设置是否要绘制shadow 该shadow不同于Item的阴影效果，是直接绘制于选中的项相邻的Item上，增加透视感
	 * 
	 * @param isDraw
	 *            true 绘制shadow false 不绘制shadow
	 */
	public void setDrawShadowImage(boolean isDraw) {
		mIsDrawShadow = isDraw;
	}

	int getShadowWidth(View v) {
		// int width = 200;//(int)(getItemWidth()*mShadowRatio);

		// Log.v(TAG, "getShadowWidth = " + width);
		return (int) (v.getWidth() * mShadowRatio);
	}

	int getShadowHeight(View v) {
		return (int) (v.getHeight() * mShadowRatio);
	}

	private Rect getShadowDrawUpDownRect(boolean isLeft, Rect rect, View view) {
		Rect drawRect = new Rect(rect);
		int shadowHeight = getShadowHeight(view);
		if (isLeft) {
			drawRect.bottom = drawRect.top + 1;
			drawRect.top = drawRect.bottom - (int) (shadowHeight * view.getScaleY());
		} else {
			drawRect.top = drawRect.bottom - 1;
			drawRect.bottom = drawRect.bottom + (int) (shadowHeight * view.getScaleY());
		}

		return drawRect;
	}

	private Rect getShadowDrawLeftRightRect(boolean isLeft, Rect rect, View view) {
		Rect drawRect = new Rect(rect);
		int shadowWidht = getShadowWidth(view);
		if (isLeft) {
			drawRect.right = drawRect.left + 1;
			drawRect.left = drawRect.left - (int) (shadowWidht * view.getScaleX());
		} else {
			drawRect.left = drawRect.right - 1;
			drawRect.right = drawRect.right + (int) (shadowWidht * view.getScaleX());
		}

		return drawRect;
	}

	private Rect getShadowDrawLeftRect(Rect rect, View view) {
		Rect drawRect = new Rect(rect);
		int shadowWidht = getShadowWidth(view);

		drawRect.right = drawRect.left + 1;
		drawRect.left = drawRect.left - (int) (shadowWidht * view.getScaleX());

		return drawRect;
	}

	private Rect getShadowDrawRightRect(Rect rect, View view) {
		Rect drawRect = new Rect(rect);
		int shadowWidht = getShadowWidth(view);

		drawRect.left = drawRect.right - 1;
		drawRect.right = drawRect.right + (int) (shadowWidht * view.getScaleX());

		return drawRect;
	}

	// private Bitmap getShadowNextBitmap(){
	// if(mShadowNextBitmap == null){
	// mShadowNextBitmap =
	// loadShadowBitmap(R.drawable.tui_pagemask_shadow_right);//tui_pagemask_shadow_right
	// }
	// return mShadowNextBitmap;
	// }
	//
	// private Bitmap getShadowPreBitmap(){
	// if(mShadowPreBitmap == null){
	// mShadowPreBitmap =
	// loadShadowBitmap(R.drawable.tui_pagemask_shadow_left);//tui_pagemask_shadow_right
	// }
	// return mShadowPreBitmap;
	// }

	// private NinePatch getShadowNextNinePatch(){
	// if(mShadowNextNinePatch == null){
	// getShadowNextBitmap();
	// mShadowNextNinePatch = new NinePatch(mShadowNextBitmap,
	// mShadowNextBitmap.getNinePatchChunk(), null);
	// }
	// return mShadowNextNinePatch;
	// }
	//
	// private NinePatch getShadowPreNinePatch(){
	// if(mShadowPreNinePatch == null){
	// getShadowPreBitmap();
	// mShadowPreNinePatch = new NinePatch(mShadowPreBitmap,
	// mShadowPreBitmap.getNinePatchChunk(), null);
	// }
	// return mShadowPreNinePatch;
	// }

	private Bitmap loadShadowBitmap(int id) {
		Resources res = getResources();

		Bitmap bmp = BitmapFactory.decodeResource(res, id);
		return bmp;
	}

	Rect getShadowPreClipRect(int index) {
		int leftIndex = index - 1;

		View vLeft = getChildAt(leftIndex);
		if (vLeft != null) {
			return getRectOfView(vLeft);
		} else {
			return null;
		}
	}

	Rect getShadowNextClipRect(int index) {
		int leftIndex = index + 1;

		View vLeft = getChildAt(leftIndex);
		if (vLeft != null) {
			return getRectOfView(vLeft);
		} else {
			return null;
		}
	}

	Path getShadowPreClipPath(int index) {
		int leftIndex = index - 1;

		final View vLeft = getChildAt(leftIndex);
		if (vLeft != null) {
			return getPathOfView(vLeft);
		} else {
			return null;
		}
	}

	Path getPathOfView(View v) {
		int l = v.getLeft();
		int r = v.getRight();
		int t = v.getTop();
		int b = v.getBottom();

		float xPivot = v.getPivotX();
		float yPivot = v.getPivotY();

		Path path = new Path();

		path.addRect(0 - xPivot, 0 - yPivot + mShadowTopPadding, r - l - xPivot, b - t - yPivot - mShadowBottomPadding, Path.Direction.CCW);
		// path.addRect(l - xPivot, t - yPivot, r - xPivot, b - yPivot,
		// Path.Direction.CCW);

		final Transformation transform = new Transformation();

		getChildStaticTransformation(v, transform);

		Matrix matrixChild = transform.getMatrix();

		// Log.v(TAG, "matrixChild = " + matrixChild);

		Matrix matrixNew = new Matrix(matrixChild);

		matrixNew.postTranslate(xPivot + l, yPivot + t);

		path.transform(matrixNew);

		return path;// pathRect;
	}

	Path getShadowNextClipPath(int index) {
		int leftIndex = index + 1;

		View vRight = getChildAt(leftIndex);
		if (vRight != null) {
			return getPathOfView(vRight);
		} else {
			return null;
		}
	}

	/**
	 * 设置shadow的宽度比 该shadow不同于Item的阴影效果，是直接绘制于选中的项相邻的Item上，增加透视感
	 * 
	 * @param radio
	 *            宽度比
	 */
	public void setImageShadowWidthRadio(float radio) {
		mShadowRatio = radio;
	}

	final static private int mShadowColorStart = 0xE0000000;
	final static private int mShadowColorEnd = 0x00000000;

	private void renderChildShadowPre(Canvas canvas, View child, int index, int alpha, Rect rect) {
		// int index = getPositionForView(child);
		// Rect rect = getRectOfView(child);

		// draw rect
		Rect rectDrawPre = getShadowDrawLeftRect(rect, child);

		// clip Rect
		Path rectClipPre = getShadowPreClipPath(index - mFirstPosition);
		// float [] vectorClipPre = getShadowPreClipVector(index -
		// mFirstPosition);

		// draw bitmap
		if (rectClipPre != null) {
			// NinePatch ninePatchPre = getShadowPreNinePatch();

			canvas.save();

			mShadowPaint.setAlpha(alpha);
			mShadowPaint.setColor(Color.WHITE);

			LinearGradient shader = new LinearGradient(rectDrawPre.left, rectDrawPre.top, rectDrawPre.right, rectDrawPre.top,
					mShadowColorEnd, mShadowColorStart, TileMode.CLAMP);
			mShadowPaint.setShader(shader);
			mShadowPaint.setAntiAlias(true);

			canvas.drawPath(rectClipPre, mShadowPaint);
			// canvas.drawRect(rectDrawPre, paint);

			canvas.restore();
		}
	}

	private void renderChildShadowNext(Canvas canvas, View child, int index, int alpha, Rect rect) {
		// int index = getPositionForView(child);
		// Rect rect = getRectOfView(child);

		// draw rect
		Rect rectDrawNext = getShadowDrawRightRect(rect, child);

		// clip Rect
		Path rectClipNext = getShadowNextClipPath(index - mFirstPosition);

		if (rectClipNext != null) {
			// NinePatch ninePatchNext = getShadowNextNinePatch();

			canvas.save();

			// if(DEBUG_UI_LOG)Log.v(TAG, "rectClipNext = " + rectClipNext);

			mShadowPaint.setAlpha(alpha);

			// canvas.clipRect(rectClipNext);

			mShadowPaint.setAlpha(alpha);
			mShadowPaint.setColor(Color.WHITE);

			LinearGradient shader = new LinearGradient(rectDrawNext.left, rectDrawNext.top, rectDrawNext.right, rectDrawNext.top,
					mShadowColorStart, mShadowColorEnd, TileMode.CLAMP);
			mShadowPaint.setShader(shader);
			mShadowPaint.setAntiAlias(true);

			canvas.drawPath(rectClipNext, mShadowPaint);

			// canvas.drawRect(rectDrawNext, mShadowPaint);

			canvas.restore();
		}
	}

	private Rect getRectOfView(View v) {
		if (v != null) {
			// /////////////////
			Rect rect = new Rect();

			int l = v.getLeft();
			int r = v.getRight();

			int t = v.getTop();
			int b = v.getBottom();

			int xCenter = (l + r) / 2;
			int yCenter = (t + b) / 2;

			// Rect r = new Rect(v.getLeft(), v.getTop(), v.getRight(),
			// v.getBottom());
			int width = r - l;
			int height = b - t;

			// TODO:tmp fix pokerFlow bug
			float xNewCenter = xCenter + v.getTranslationX();
			float yNewCenter = yCenter + v.getTranslationY();

			float newWidth = width * v.getScaleX();
			float newHeight = height * v.getScaleX();

			rect.left = (int) (xNewCenter - newWidth / 2);
			rect.right = (int) (xNewCenter + newWidth / 2);

			rect.top = (int) (yNewCenter - newHeight / 2);
			rect.bottom = (int) (yNewCenter + newHeight / 2);
			return rect;
		}

		return null;
	}

}
