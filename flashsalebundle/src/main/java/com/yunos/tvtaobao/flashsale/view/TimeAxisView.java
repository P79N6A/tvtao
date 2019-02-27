/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-28       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.flashsale.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.bo.CategoryItem;
import com.yunos.tvtaobao.flashsale.bo.CategoryList;
import com.yunos.tvtaobao.flashsale.listener.TabSwitchViewListener;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;

import java.util.List;

public class TimeAxisView extends View implements FocusListener, ItemListener,
		TabSwitchViewListener {
	public static final long KEYEVENT_TIMEOYT = 500;
	private static final boolean DEBUG = true;
	public static final String TAG = "TimeAxisView";

	private static final byte ANIM_NODISPLAY = -2;
	private static final byte ANIM_PREV = -1;
	private static final byte ANIM_NORMAL = 0;
	private static final byte ANIM_NEXT = 1;

	public static final int ANIM_FRAME_COUNT = 10;
	public static final int LEVEL_DEFAULT = 3;
	public static final int MAX_COUNT_DEFAULT = 23;

	/** 定义每个级别的显示效果 */
	private DrawTimeAxisParam[] mDrawTimeUnit;
	private int mDrawParamLen;

	/** 进在抢购绘制参数 */
	private DrawTimeAxisParam mBuyingDrawTimeUnit;

	/** 正在抢购的位置 */
	private int mBuyingPos = -1;

	/** 当前选中的位置 */
	private int mSelectPos = -1;

	/** 获取焦点的半径和颜色 */
	private int mFocusRadius;
	private int mFocusColor;
	private int mFocusWidth = 6;

	/** 主线条宽度 */
	private int mLineBgColor;
	private int mLineWidth;

	private int mTextSpace;
	private int mOffsetY;

	/** 定义左右两边最大个数 */
	private byte mAnimMode = ANIM_NODISPLAY;

	private Drawable mFocusDrawable;

	/** 数据内容 */
	private int mDataLen;
	private DrawTimeAxisInfo[] mData;

	/** 显示的节点数 */
	private DrawNodeInfo[] mNodeInfo;
	private int mNodeInfoLen;
	// private Interpolator mInterpolator = new
	// AccelerateDecelerateFrameInterpolator();
	private Interpolator mInterpolator = new LinearInterpolator();

	private Paint mLinePaint = new Paint();
	private Paint mCirclePaint = new Paint();
	private Paint mFocusPaint = new Paint();
	private Paint mStatusPaint = new Paint();
	private Paint mTimePaint = new Paint();
	private Paint mShawdowPaint = new Paint();

	@SuppressWarnings("unused")
	private Paint mDebugPaint;

	private int mCurFrame = 0;

    private Rect mClipFocusRect = new Rect(); // 默认focus框

	public TimeAxisView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		onInit();
	}

	public TimeAxisView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		onInit();
	}

	public TimeAxisView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		onInit();
	}

	protected void onInit() {
		super.setFocusable(true);
		mDrawTimeUnit = new DrawTimeAxisParam[LEVEL_DEFAULT];
		mDrawParamLen = LEVEL_DEFAULT;
		for (int i = 0; i < LEVEL_DEFAULT; i++) {
			mDrawTimeUnit[i] = new DrawTimeAxisParam();
		}
		mDrawTimeUnit[0].set(AppConfig.TIME_AXIS_LEVEL0_RADIUS,
				AppConfig.TIME_AXIS_LEVEL0_BGCOLOR,
				AppConfig.TIME_AXIS_LEVEL0_COLOR,
				AppConfig.TIME_AXIS_LEVEL0_FONTSIZE,
				AppConfig.TIME_AXIS_LEVEL0_STATUS_COLOR,
				AppConfig.TIME_AXIS_LEVEL0_STATUS_FONTSIZE,
				AppConfig.TIME_AXIS_LEVEL0_SPACE,
				AppConfig.TIME_AXIS_SHADOW_COLOR,
				AppConfig.TIME_AXIS_SHADOW_WIDTH);

		mDrawTimeUnit[1].set(AppConfig.TIME_AXIS_LEVEL1_RADIUS,
				AppConfig.TIME_AXIS_LEVEL1_BGCOLOR,
				AppConfig.TIME_AXIS_LEVEL1_COLOR,
				AppConfig.TIME_AXIS_LEVEL1_FONTSIZE,
				AppConfig.TIME_AXIS_LEVEL1_STATUS_COLOR,
				AppConfig.TIME_AXIS_LEVEL1_STATUS_FONTSIZE,
				AppConfig.TIME_AXIS_LEVEL1_SPACE,
				AppConfig.TIME_AXIS_SHADOW_COLOR,
				AppConfig.TIME_AXIS_SHADOW_WIDTH);

		mDrawTimeUnit[2].set(AppConfig.TIME_AXIS_LEVEL2_RADIUS,
				AppConfig.TIME_AXIS_LEVEL2_BGCOLOR, 0, 0, 0, 0,
				AppConfig.TIME_AXIS_LEVEL2_SPACE,
				AppConfig.TIME_AXIS_SHADOW_COLOR_EX,
				AppConfig.TIME_AXIS_SHADOW_WIDTH);

		mBuyingDrawTimeUnit = new DrawTimeAxisParam();
		mBuyingDrawTimeUnit.set(0, AppConfig.TIME_AXIS_BUYING_BGCOLOR,
				AppConfig.TIME_AXIS_BUYING_COLOR, 0,
				AppConfig.TIME_AXIS_BUYING_STATUS_COLOR, 0, 0,
				AppConfig.TIME_AXIS_BUYING_SHADOW_COLOR,
				AppConfig.TIME_AXIS_SHADOW_WIDTH);

		mFocusRadius = AppConfig.TIME_AXIS_SELECT_RADIUS;
		mFocusColor = AppConfig.TIME_AXIS_SELECT_COLOR;
		if (mFocusRadius > mDrawTimeUnit[0].mRadius) {
			mOffsetY = mFocusRadius - mDrawTimeUnit[0].mRadius + 1;
		} else {
			mOffsetY = 1;
		}
		mLineBgColor = AppConfig.TIME_AXIS_LINE_BGCOLOR;
		mLineWidth = AppConfig.TIME_AXIS_LINE_BGCOLOR;

		mTextSpace = AppConfig.TIME_AXIS_TEXT_SPACE;
		mLinePaint.setColor(mLineBgColor);
		mLinePaint.setStrokeWidth(mLineWidth);
		mLinePaint.setStyle(Paint.Style.FILL);
		mLinePaint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);

		mCirclePaint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
		mCirclePaint.setStyle(Paint.Style.FILL);

		mFocusPaint.setColor(mFocusColor);
		mFocusPaint.setStrokeWidth(mFocusWidth);
		mFocusPaint.setStyle(Paint.Style.STROKE);
		// mFocusPaint.setFlags(Paint.FILTER_BITMAP_FLAG |
		// Paint.ANTI_ALIAS_FLAG);
		mFocusPaint.setAntiAlias(true);

		// mTmpPaint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);

		mStatusPaint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
		mStatusPaint.setTextAlign(Align.CENTER);

		mTimePaint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
		mTimePaint.setTextAlign(Align.CENTER);

		mShawdowPaint.setFlags(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
//		mShawdowPaint.setStyle(Paint.Style.STROKE);
//		BlurMaskFilter blurFilter = new BlurMaskFilter(20, BlurMaskFilter.Blur.OUTER);
//		mShawdowPaint.setMaskFilter(blurFilter);
		
		mFocusDrawable = super.getResources().getDrawable(
				R.drawable.fs_time_focus);

		// if (DEBUG) {
		// mDebugPaint = new Paint();
		// mDebugPaint.setFlags(Paint.FILTER_BITMAP_FLAG
		// | Paint.ANTI_ALIAS_FLAG);
		// mDebugPaint.setColor(0xFF112233);
		// mDebugPaint.setStyle(Paint.Style.STROKE);
		// }
		// super.setBackgroundColor(android.R.color.transparent);
		mNodeInfoLen = MAX_COUNT_DEFAULT;
		mNodeInfo = new DrawNodeInfo[mNodeInfoLen];
		for (int index = 0; index < mNodeInfoLen; index++) {
			mNodeInfo[index] = new DrawNodeInfo();
		}
	}

	/** 上边缘 离中心基准圆心距离 */
	private int getDistance(int position) {
		int distance = mDrawTimeUnit[0].mRadius;
		int size = mDrawTimeUnit.length;

		if (position < 0) {
			position = -position;
		}
		for (int i = 1; i <= position; i++) {
			if (i > size) {
				distance += 2 * mDrawTimeUnit[size - 1].mRadius
						+ mDrawTimeUnit[size - 1].mSpace;
			} else if (i == size) {
				distance += 2 * mDrawTimeUnit[size - 1].mRadius
						+ mDrawTimeUnit[i - 1].mSpace;
			} else {
				distance += 2 * mDrawTimeUnit[i].mRadius
						+ mDrawTimeUnit[i - 1].mSpace;
			}
		}
		return distance;
	}

	/**
	 * caculate offset position param offst 1:表示往下 -1：表示往上 0：表示不做任何偏移
	 * 
	 */
	private int getSelectPosition(int offset) {
		int select = mSelectPos + offset;

		if (select < 0) {
			return 0;
		} else if (select >= mDataLen) {
			return mDataLen - 1;
		}
		return select;
	}

	// private int getRadius(int pos, boolean isAbsolute) {
	// DrawTimeAxisParam param = getDrawParam(pos, isAbsolute);
	//
	// if (null != param) {
	// return param.mRadius;
	// }
	//
	// return 0;
	// }

	private DrawTimeAxisParam getDrawParam(int pos, boolean isAbsolute) {
		if (mDrawParamLen <= 0) {
			return null;
		}
		if (!isAbsolute) {
			pos += mSelectPos;
		}
		if (pos < 0) {
			pos = -pos;
		}
		if (pos >= mDrawParamLen) {
			return mDrawTimeUnit[mDrawParamLen - 1];
		}
		return mDrawTimeUnit[pos];
	}

	private DrawTimeAxisInfo getDrawInfo(int pos, boolean isAbsolute) {
		if (mDataLen <= 0) {
			return null;
		}
		if (!isAbsolute) {
			pos += mSelectPos;
		}
		if (pos >= 0 && pos < mDataLen) {
			return mData[pos];
		}
		return null;
	}

	// private int getPosition(String tabKey) {
	// DrawTimeAxisInfo tmp;
	//
	// for (int index = 0; index < mDataLen; index++) {
	// tmp = mData[index];
	// if (TextUtils.equals(tmp.mKey, tabKey)) {
	// return index;
	// }
	// }
	// return -1;
	// }

	@SuppressLint("Override")
    public int getMinimumWidth() {
		int radius = 0;
		DrawTimeAxisParam drawParam;

		for (int i = 0; i < mDrawParamLen; i++) {
			drawParam = mDrawTimeUnit[i];
			if (null != drawParam) {
				if (drawParam.mRadius > radius) {
					radius = drawParam.mRadius;
				}
			}
		}

		if (radius < mFocusRadius) {
			radius = mFocusRadius;
		}

		mRealWidth = 2 * radius + 10;
		if (null != mFocusDrawable) {
			if (mRealWidth < mFocusDrawable.getMinimumWidth()) {
				mRealWidth = mFocusDrawable.getMinimumWidth();
			}
		}

		return mRealWidth;
	}

	private int mRealHeight;
	private int mRealWidth;

	@SuppressLint("Override")
    public int getMinimumHeight() {
		mRealHeight = 2 * getDistance(mNodeInfoLen / 2);

		return mRealHeight;
	}

	@Override
	public void setMinimumHeight(int minHeight) {
		super.setMinimumHeight(getMinimumHeight());
	}

	@Override
	public void setMinimumWidth(int minWidth) {
		super.setMinimumWidth(getMinimumWidth());
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		if (mAnimMode != ANIM_NORMAL) {
			return true;
		}
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			return (mSelectPos - 1) >= 0;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return (mSelectPos + 1) < mDataLen;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			/** 如果在定时，则不让其切换数据 */

			if (mHasDelayOpr) {
					AppDebug.i(TAG, "mHasDelayOpr: " + mHasDelayOpr);
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	public boolean hasNavigationDirection(int keyCode) {
		int curSelectPos = -1;

		if (mAnimMode != ANIM_NORMAL) {
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			curSelectPos = mSelectPos + 1;
			if (curSelectPos < mDataLen) {
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			curSelectPos = mSelectPos - 1;

			if (curSelectPos >= 0) {
				return true;
			}
		}
		return false;
	}

	public boolean navigationDirection(int keyCode) {
		int curSelectPos = -1;

		if (mAnimMode != ANIM_NORMAL) {
			return true;
		}
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			curSelectPos = mSelectPos + 1;
			if (curSelectPos < mDataLen) {
				mAnimMode = ANIM_NEXT;
			}
		} else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			curSelectPos = mSelectPos - 1;

			if (curSelectPos >= 0) {
				mAnimMode = ANIM_PREV;
			}
		}
		if (mAnimMode != ANIM_NORMAL) {
			cancelKeyEvent();
			/** 需要重新计算 */
			// drawParamSwap();
			switchToSelectPos(curSelectPos);
			return true;
		}
		return false;
	}

	private float caculateLastNodeSpace(int selectPos) {
		DrawTimeAxisParam modifyParam = mDrawTimeUnit[mDrawParamLen - 1];

			AppDebug.i(TAG, "selectPos: " + selectPos + " mDrawParamLen: "
					+ mDrawParamLen + "mNodeInfoLen: " + mNodeInfoLen);
		if (selectPos + mDrawParamLen > mNodeInfoLen) {
			selectPos = mNodeInfoLen - selectPos - 1;
		}
		if (selectPos < mDrawParamLen) {
			int size = 2 * (mDrawParamLen - 1) - 1;
			int lastNodeCount = mNodeInfoLen - size;
			int curHeight = getDistance(2 * (mDrawParamLen - 1) - selectPos)
					+ getDistance(selectPos);
			int srcHeight = 2 * getDistance(mDrawParamLen - 1);

			/** 需要调整间距 */
			float offset = srcHeight - curHeight;

				AppDebug.i(TAG, "curHeight: " + curHeight + " srcHeight: "
						+ srcHeight + "offset: " + offset / lastNodeCount);
			return offset / lastNodeCount + modifyParam.mSpace;

		}
		return modifyParam.mSpace;

	}

	// private void drawParamSwap(){
	// DrawNodeInfo nodeInfo = null;
	//
	// for(int index = 0; index < mNodeInfoLen; index++){
	//
	// }
	// }
	private int mPrevCenterOffsetPos = -1;
	private int mCurCenterOffsetPos = -1;

	private void switchToSelectPos(int selectPos) {
		mCurFrame = 0;
		int nodeStart, nodeEnd;
		int half = mNodeInfoLen / 2;
		if (selectPos < half) {
			/** 大圆在中心点的上半部分 */
			nodeStart = 0;
			nodeEnd = mNodeInfoLen - 1;
		} else if ((selectPos + half) >= mDataLen) {
			/** 大圆在中心点下半部分 */
			nodeEnd = mDataLen - 1;
			nodeStart = nodeEnd - mNodeInfoLen + 1;
		} else {
			/** 大圆在居中位置 */
			nodeStart = selectPos - half;
			nodeEnd = selectPos + half;
		}
		// if (DEBUG) {
		// LogUtil.i(TAG, "start node: " + nodeStart + " end node:" + nodeEnd
		// + " mNodeInfoLen:" + mNodeInfoLen + " data len: "
		// + mDataLen + " slect pos:" + selectPos);
		// }

		/** 调整下间隔，并记录 */
		float prevSpace, curSpace = caculateLastNodeSpace(selectPos - nodeStart);
		DrawTimeAxisParam modifyParam = mDrawTimeUnit[mDrawParamLen - 1];
		prevSpace = modifyParam.mSpace;
		modifyParam.mSpace = curSpace;

		DrawTimeAxisParam param = null;
		int startY = 0;
		DrawNodeInfo nodeInfo = null;
		int pos, index;

		byte spaceType = -1; // 1: 表示当前节点间隙 2：表示前一个节点

		if (nodeStart == selectPos) {
			spaceType = 1;
		}
		for (index = nodeStart; index <= nodeEnd; index++) {

			pos = index - selectPos;
			param = getDrawParam(pos, true);
			if (param == mDrawTimeUnit[0]) {
				/** 大圆在整个节点的位置 */
				// ssd
				mCurCenterOffsetPos = index - nodeStart;
				AppDebug.i(TAG, "cur center offset pos: "
							+ mCurCenterOffsetPos);
			}
			if (spaceType >= 2 && pos <= 0) {
				startY += param.mSpace;
			}
			startY += param.mRadius;
			nodeInfo = mNodeInfo[index - nodeStart];
			/** 切换绘制前后的数据 */
			nodeInfo.swap();
			nodeInfo.mCurCenterY = startY;
			nodeInfo.mCurInfo = mData[index];
			nodeInfo.mCurParam = param;

			if (mBuyingPos == index) {
				nodeInfo.mCurBuyingParam = mBuyingDrawTimeUnit;
			} else {
				nodeInfo.mCurBuyingParam = null;
			}
			if (spaceType >= 1 && pos >= 0) {
				startY += param.mSpace;
			}
			startY += param.mRadius;
			spaceType = 2;

			// if (DEBUG) {
			// LogUtil.i(TAG, "index: " + (index - nodeStart) + "centerY: "
			// + nodeInfo.mCurCenterY + " time: "
			// + nodeInfo.mCurInfo.mTime + " status:"
			// + nodeInfo.mCurInfo.mStatus + " id:"
			// + nodeInfo.mCurInfo.mKey + " param is null: "
			// + (nodeInfo.mCurParam == null));
			// }
		}
		/** 恢复最小节点的间距 */
		modifyParam.mSpace = prevSpace;
		if ((mAnimMode == ANIM_PREV || (mAnimMode == ANIM_NEXT))
				&& mPrevCenterOffsetPos == mCurCenterOffsetPos
				&& mPrevCenterOffsetPos == half) {
			/** 不需要动画 */
			for (index = 0; index < mNodeInfoLen; index++) {
				mNodeInfo[index].setNeedAnim(false);
			}
			nodeStart = half - (mDrawParamLen - 1);
			nodeEnd = half + (mDrawParamLen - 1);
			if (mAnimMode == ANIM_NEXT) {
				for (index = nodeStart; index <= nodeEnd; index++) {
					swapNodeInfo(index + 1, index);
				}
			} else {
				for (index = nodeEnd; index >= nodeStart; index--) {
					swapNodeInfo(index - 1, index);
				}
			}

			AppDebug.i(TAG, "start node: " + nodeStart + " end node:"
						+ nodeEnd + " mAnimMode:" + mAnimMode);
		}
		AppDebug.i(TAG, "mAnimMode: " + mAnimMode + "mPrevCenterOffsetPos: "
					+ mPrevCenterOffsetPos + " mCurCenterOffsetPos: "
					+ mCurCenterOffsetPos + " half:" + half);
		mPrevCenterOffsetPos = mCurCenterOffsetPos;
		invalidate();
	}

	private void swapNodeInfo(int srcIndex, int dstIndex) {
		if (srcIndex < 0 || srcIndex >= mNodeInfoLen) {
			return;
		}
		if (dstIndex < 0 || dstIndex >= mNodeInfoLen) {
			return;
		}
		DrawNodeInfo nodeInfo = mNodeInfo[dstIndex];

		nodeInfo.swap(mNodeInfo[srcIndex]);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (navigationDirection(keyCode)) {
			return true;
		}
		if (mHasDelayOpr) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (mAnimMode == ANIM_NORMAL) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
					|| keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				generateKeyEvent();
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	private boolean mHasDelayOpr = false;

	private void cancelKeyEvent() {
		mHasDelayOpr = false;
		super.removeCallbacks(mKeyEventRunnable);
	}

	private void generateKeyEvent() {
		mHasDelayOpr = true;
		super.postDelayed(mKeyEventRunnable, KEYEVENT_TIMEOYT);
	}

	private String mCurKey;
	private AbstractTabLayout.TabSwitchListener mTabSwitchListener;

	private void onTabSwitch(String tabKey, int position, String preTtabKey) {
		AbstractTabLayout.TabSwitchListener l = mTabSwitchListener;
		if (null != l) {
			l.onSwitchTab(tabKey, preTtabKey);
		}

	}

	private Runnable mKeyEventRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mIsDestroy) {
				return;
			}
			mHasDelayOpr = false;
			DrawTimeAxisInfo info = getDrawInfo(mSelectPos, true);
			if (null != info && !TextUtils.equals(mCurKey, info.mKey)) {
				onTabSwitch(info.mKey, mSelectPos, mCurKey);
				mCurKey = info.mKey;
			}
		}
	};
//	private void drawShawdow(Canvas canvas, int offsetX, int offsetY,
//			int radius, int shawdowColor, int shawdowWidth) {
//		/**绘制圆弧*/
//		mShawdowPaint.setColor(shawdowColor);
//		mShawdowPaint.setStrokeWidth(shawdowWidth);
//		canvas.drawCircle(offsetX, offsetY, radius, mShawdowPaint);
//	}
	private void drawFocusCicle(Canvas canvas, int radius, int centerX,
                                int centerY, int alpha) {
		
//		drawShawdow(canvas, centerX, centerY, radius, 0xff1435, CommUtil.dip2px(getContext(), 4));

		mFocusDrawable.setAlpha(alpha);
		mFocusDrawable.setBounds(centerX - radius, centerY - radius, centerX
				+ radius, centerY + radius);
		mFocusDrawable.draw(canvas);
		
//		/**绘制内环*/
//		int innerCircle = radius - mFocusWidth;
//		//绘制内环
////		mFocusPaint.setColor(mFocusColor)
//		mFocusPaint.setStyle(Style.STROKE);
//		mFocusPaint.setColor(0xFFFF0000);
////		mFocusPaint.setARGB(155, 167, 190, 206);  
//		mFocusPaint.setAntiAlias(true);
//		mFocusPaint.setStrokeWidth(2);
//		canvas.drawCircle(centerX, centerY, innerCircle, mFocusPaint);
//		
//		//圆环
////		mFocusPaint.setARGB(255, 212 ,225, 233);
//		mFocusPaint.setStrokeWidth(mFocusWidth);  
//		canvas.drawCircle(centerX, centerY, innerCircle +  1  + mFocusWidth/ 2 ,  mFocusPaint); 
//		
////		mFocusPaint.setARGB(155, 167, 190, 206);
//		mFocusPaint.setStrokeWidth( 2 ); 
//		canvas.drawCircle(centerX, centerY, radius,  mFocusPaint);       	
		
	}

	private void drawCircle(Canvas canvas, int radius, int centerX,
                            int centerY, int color) {
		mCirclePaint.setColor(color);
		canvas.drawCircle(centerX, centerY, radius, mCirclePaint);

	}

	// private int getFontHeight(Paint p) {
	// FontMetrics fm = p.getFontMetrics();
	//
	// return (int) (Math.ceil(fm.descent - fm.ascent) + 2);
	// }

	private void drawTimeAxisInfo(Canvas canvas, DrawTimeAxisInfo info,
                                  int radius, int centerX, int centerY, int space, int color,
                                  int fontSize, int statusColor, int statusFontSize) {
		int h1, h2, baseLine;

		if (fontSize <= 0 || statusFontSize <= 0) {
			/** 不需要绘制文本信息和内容 */
			// if (DEBUG) {
			// mDebugPaint.setColor(0XFF0000FF);
			// canvas.drawLine(0, centerY, super.getWidth(), centerY,
			// mDebugPaint);
			// }
			return;
		}
		mTimePaint.setColor(color);
		mTimePaint.setTextSize(fontSize);

		mStatusPaint.setColor(statusColor);
		mStatusPaint.setTextSize(statusFontSize);

		FontMetrics timeFm = mTimePaint.getFontMetrics();
		FontMetrics statusFm = mStatusPaint.getFontMetrics();

		h1 = (int) -timeFm.ascent;
		h2 = (int) -statusFm.ascent;

		baseLine = centerY - (h2 + space) / 2;
		// if (DEBUG) {
		// mDebugPaint.setColor(0XFFFF00FF);
		// canvas.drawLine(0, baseLine, super.getWidth(), baseLine,
		// mDebugPaint);
		//
		// }
		baseLine += (int) (-timeFm.descent + (timeFm.bottom - timeFm.top) / 2);

		// if( DEBUG){
		// mDebugPaint.setColor(0XFF0000FF);
		// canvas.drawLine(0, baseLine, super.getWidth(), baseLine,
		// mDebugPaint);
		// canvas.drawRect(0, baseLine + timeFm.ascent, super.getWidth(),
		// baseLine + timeFm.descent, mDebugPaint);
		// }
		canvas.drawText(info.mTime, centerX, baseLine, mTimePaint);

		baseLine = (int) (centerY + (h1 + space) / 2);
		// if (DEBUG) {
		// mDebugPaint.setColor(0XFFFF00FF);
		// canvas.drawLine(0, baseLine, super.getWidth(), baseLine,
		// mDebugPaint);
		// mDebugPaint.setColor(0XFF0000FF);
		// canvas.drawRect(0, baseLine + timeFm.ascent, super.getWidth(),
		// baseLine + timeFm.descent, mDebugPaint);
		// }
		baseLine += (int) (-statusFm.descent + (statusFm.bottom - statusFm.top) / 2);
		canvas.drawText(info.mStatus, centerX, baseLine, mStatusPaint);

		// if (DEBUG) {
		// mDebugPaint.setColor(0XFF11ccFF);
		// canvas.drawLine(0, centerY, super.getWidth(), centerY, mDebugPaint);
		// }
	}

	public void drawAnim(Canvas canvas, boolean background, int offsetX,
                         int offsetY) {
		if (background) {
			canvas.drawColor(0x00FFFFFF, Mode.CLEAR);
		}
		offsetX += super.getWidth() / 2;

		
//		int endY = offsetY + mRealHeight - mDrawTimeUnit[mDrawParamLen - 1].mRadius;
		DrawNodeInfo nodeInfo = mNodeInfo[mNodeInfoLen - 1];
		int endY = offsetY + nodeInfo.mCurCenterY;
		canvas.drawLine(offsetX, offsetY
				+ mDrawTimeUnit[mDrawParamLen - 1].mRadius, offsetX, endY,
				mLinePaint);

		float input = 2;
		if (ANIM_NORMAL != mAnimMode) {
			input = mInterpolator.getInterpolation(((float) mCurFrame)
					/ ANIM_FRAME_COUNT);
		}
		if (ANIM_NORMAL != mAnimMode && (input >= 0.0 && input < 1.0)) {
			if (ANIM_NEXT == mAnimMode) {
				for (int index = 0; index < mNodeInfoLen; index++) {
					mNodeInfo[index].draw(canvas, offsetX, offsetY, input);
				}
			} else {
				for (int index = mNodeInfoLen - 1; index >= 0; index--) {
					mNodeInfo[index].draw(canvas, offsetX, offsetY, input);
				}
			}

		} else {
			for (int index = 0; index < mNodeInfoLen; index++) {
				mNodeInfo[index].draw(canvas, offsetX, offsetY);
			}
		}

		/** 绘制背景颜色 , 需要做偏移，防止覆盖不完增 */
		// if (DEBUG) {
		// mDebugPaint.setColor(0xFF0000FF);
		// canvas.drawLine(offsetX, offsetY
		// + mDrawTimeUnit[mDrawParamLen - 1].mRadius, offsetX,
		// offsetY + mRealHeight
		// - mDrawTimeUnit[mDrawParamLen - 1].mRadius,
		// mDebugPaint);
		// }

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (mSelectPos < 0 || mSelectPos >= mDataLen
				|| null == mBuyingDrawTimeUnit || mNodeInfoLen <= 0
				|| ANIM_NODISPLAY == mAnimMode || mIsDestroy) {
				AppDebug.d(TAG,"onDraw: param error, select pos: " + mSelectPos
						+ " data len: " + mDataLen + " buying param: "
						+ mBuyingDrawTimeUnit + " node len:" + mNodeInfoLen
						+ "anim mode: " + mAnimMode + "mIsDestroy" + mIsDestroy);
			return;
		}
		if (ANIM_NORMAL != mAnimMode) {
			mCurFrame++;
			if (mCurFrame >= ANIM_FRAME_COUNT) {
				mSelectPos = getSelectPosition(mAnimMode);
				mAnimMode = ANIM_NORMAL;
				post(mKeyEventRunnable);
			}
		}
		drawAnim(canvas, false, 0, mOffsetY);

		if (ANIM_NORMAL != mAnimMode) {
			// super.postInvalidateDelayed(300);
			invalidate();
		}
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		if (widthMode == MeasureSpec.AT_MOST) {
			widthSpecSize = getMinimumWidth();
		}

		if (heightMode == MeasureSpec.AT_MOST) {
			heightSpecSize = getMinimumHeight();
		}

		setMeasuredDimension(widthSpecSize, heightSpecSize);
	}

	public void setTimeAxisKey(String key) {
		if (mDataLen > 0 && !mIsDestroy) {
			DrawTimeAxisInfo info;
			Resources res = super.getResources();
			int prevBuyingPos = mBuyingPos;

			DrawTimeAxisInfo prevInfo = null, curInfo = null;

			mBuyingPos = -1;
				AppDebug.i(TAG, "setTimeAxisKey prevBuyingPos: " + prevBuyingPos
						+ " mDataLen" + mDataLen + "thread: "
						+ Thread.currentThread().getName());

			if (prevBuyingPos >= 0 && prevBuyingPos < mDataLen) {
				prevInfo = mData[prevBuyingPos];
			}
			int index;
			for (index = 0; index < mDataLen; index++) {
				info = mData[index];
				if (info.mStatusType == CategoryItem.STATUS_TYPE_CURRENT) {
					info.mStatusType = CategoryItem.STATUS_TYPE_PAST;
					info.mStatus = res.getString(R.string.str_panic_buying);
				}
				if (TextUtils.equals(key, info.mKey)) {
					mBuyingPos = index;
					curInfo = mData[index];
					info.mStatus = res.getString(R.string.str_in_work);
					info.mStatusType = CategoryItem.STATUS_TYPE_CURRENT;
				}
			}
			DrawNodeInfo nodeInfo;
			for (index = 0; index < mNodeInfoLen; index++) {
				nodeInfo = mNodeInfo[index];
				if (nodeInfo.mCurInfo == prevInfo) {
					nodeInfo.mCurBuyingParam = null;
				}
				if (nodeInfo.mCurInfo == curInfo) {
					nodeInfo.mCurBuyingParam = mBuyingDrawTimeUnit;
				}
			}
			invalidate();
		}
	}

	public void setObject(Object obj) {
		if (null == obj || mIsDestroy) {
			return;
		}
		if (!(obj instanceof CategoryList)) {
			throw new IllegalArgumentException(
					"setObject must CategoryList type ");
		}
		CategoryList category = (CategoryList) obj;
		List<CategoryItem> batchs = category.getItems();
		int size = (null != batchs) ? batchs.size() : 0;
		int selectPos = mSelectPos;

		if (size > 0) {
			DrawTimeAxisInfo tmp;
			CategoryItem item;
			String status;
			Resources res = super.getResources();

			mDataLen = size;
			mData = new DrawTimeAxisInfo[mDataLen];

			for (int index = 0; index < mDataLen; index++) {
				item = batchs.get(index);
				tmp = new DrawTimeAxisInfo();
				status = item.getStatus();

				tmp.mKey = item.getId();
				tmp.mTime = DateUtils.getTime(item.getStartTime());

				/** 读取状态 */
				if (CategoryItem.STATUS_CURRENT.equals(status)) {
					tmp.mStatus = res.getString(R.string.str_in_work);
					mBuyingPos = index;
					mSelectPos = index;
					tmp.mStatusType = CategoryItem.STATUS_TYPE_CURRENT;
				} else if (CategoryItem.STATUS_FUTRUE.equals(status)) {
					tmp.mStatus = res.getString(R.string.str_buying_futrue);
					tmp.mStatusType = CategoryItem.STATUS_TYPE_FUTRUE;
				} else {
					tmp.mStatus = res.getString(R.string.str_panic_buying);
					tmp.mStatusType = CategoryItem.STATUS_TYPE_PAST;
				}

				mData[index] = tmp;
			}
			mAnimMode = ANIM_NORMAL;
			/** 设置最小宽和高 , 触发一次 */
			if (mDataLen < MAX_COUNT_DEFAULT) {
				/** 保证奇数 */
				mNodeInfoLen = mDataLen;
				if (mNodeInfoLen % 2 == 0) {
					mNodeInfoLen--;
				}
			} else {
				mNodeInfoLen = MAX_COUNT_DEFAULT;
			}
			/** 需要刷新tab */
			if (mSelectPos < 0) {
				mSelectPos = 0;
			}
			/** 计算高度和节点个数 */
			switchToSelectPos(mSelectPos);

			setMinimumHeight(0);
			setMinimumWidth(0);
			super.requestLayout();

			if (selectPos != mSelectPos) {
				/** 需要切换 */
				mKeyEventRunnable.run();
			}

		}
	}

	@Override
	public void setTabSwitchListener(AbstractTabLayout.TabSwitchListener l) {
		// TODO Auto-generated method stub
		mTabSwitchListener = l;
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return this;
	}

	private boolean mIsDestroy = false;

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mIsDestroy = true;
		mDrawTimeUnit = null;
		mData = null;
		cancelKeyEvent();
	}

	// @Override
	// public void setCurrentKey(String key) {
	// // TODO Auto-generated method stub
	// int index = getPosition(key);
	// if( index >= 0){
	// mSelectPos = index;
	// mAnimMode = ANIM_NORMAL;
	// invalidate();
	// mKeyEventRunnable.run();
	// }
	// }

	private Params mParams = new Params(1.0f, 1.0f, 1, null, false, 1,
			new AccelerateDecelerateFrameInterpolator());
	private FocusRectParams mFocusRectparams = new FocusRectParams();

	@Override
	public boolean isScale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getItemWidth() {
		// TODO Auto-generated method stub
		return getWidth();
	}

	@Override
	public int getItemHeight() {
		// TODO Auto-generated method stub
		return getHeight();
	}

	@Override
	public Rect getManualPadding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void drawBeforeFocus(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawAfterFocus(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isFinished() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public FocusRectParams getFocusParams() {
		return mFocusRectparams;
	}

	@Override
	public boolean canDraw() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAnimate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemListener getItem() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public boolean isScrolling() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Params getParams() {
		// TODO Auto-generated method stub
		return mParams;
	}

	@Override
	public boolean isFocusBackground() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onFocusStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFocusFinished() {
		// TODO Auto-generated method stub

	}

	private class DrawTimeAxisInfo {
		private String mKey;
		private String mStatus;
		private String mTime;
		private byte mStatusType;
	}

	public class DrawTimeAxisParam {
		/** 圆形半径 */
		public int mRadius;
		/** 圆背景色 */
		public int mCircleColor;

		/** 时间文本显示的字体和颜色 */
		public int mColor;
		public int mFontSize;

		/** 抢购状态显示的字体和颜色 */
		public int mStatusColor;
		public int mStatusFontSize;

		/** 投影颜色和宽度 */
		public int mShadowColor;
		public int mShadowWidth;

		/** 与前后或者左右之间的间距 */
		public float mSpace;

		public void set(int radius, int circleColor, int color, int fontSize,
				int statusColor, int statusFontSize, int space,
				int shadowColor, int shadowWidth) {
			mRadius = radius;
			mCircleColor = circleColor;
			mColor = color;
			mFontSize = fontSize;
			mStatusColor = statusColor;
			mStatusFontSize = statusFontSize;
			mSpace = space;
			mShadowColor = shadowColor;
			mShadowWidth = shadowWidth;
		}
	}

	
	private int getChangeColor(int srcColor, float input, boolean isDispear) {
		int alpha = (srcColor >> 24) & 0xff;

		if (isDispear) {
			input = alpha * (1 - input);
		} else {
			input = alpha * input;
		}
		alpha = ((int) input) & 0xff;
		srcColor &= 0xFFFFFF;
		srcColor |= (alpha << 24);

		return srcColor;
	}

	
	private class DrawNodeInfo {
		/** 当前帧信息 */
		public DrawTimeAxisParam mCurParam; // 当前显示的参数
		public DrawTimeAxisInfo mCurInfo; // 当前显示的信息
		public DrawTimeAxisParam mCurBuyingParam; // 是否是正在抢购中
		public int mCurCenterY; // 当前的中心点位置

		/** 前一帧的信息 */
		public DrawTimeAxisParam mPrevParam; // 前一帧显示的参数
		public DrawTimeAxisInfo mPrevInfo; // 前一帧显示的参数
		public DrawTimeAxisParam mPrevBuyingParam; // 前一帧是否正在抢购中
		public int mPrevCenterY; // 前一帧显示的参数

		public boolean mNeedAnim;

		public void swap() {
			mPrevParam = mCurParam;
			mPrevInfo = mCurInfo;
			mPrevBuyingParam = mCurBuyingParam;
			mPrevCenterY = mCurCenterY;
			mNeedAnim = true;
		}

		public void swap(DrawNodeInfo src) {
			if (null != src) {
				mPrevParam = src.mPrevParam;
				mPrevInfo = src.mPrevInfo;
				mPrevBuyingParam = src.mPrevBuyingParam;
				mPrevCenterY = src.mPrevCenterY;
				mNeedAnim = true;
			}

		}

		public void setNeedAnim(boolean needAnim) {
			mNeedAnim = needAnim;
		}

		public void draw(Canvas canvas, int offsetX, int offsetY) {
			int circleColr;
			int color, statusColor;
			int shawdowWidth, shawdowColor;
			offsetY += mCurCenterY;

			if (null == mCurParam || null == mCurInfo) {
					AppDebug.i(TAG, "draw item unexception param: " + mCurParam
							+ " cur info:" + mCurInfo);
				return;
			}
			/** 绘制圆圈 */
			if (null != mCurBuyingParam) {
				circleColr = mCurBuyingParam.mCircleColor;
				color = mCurBuyingParam.mColor;
				statusColor = mCurBuyingParam.mStatusColor;
				shawdowColor = mCurBuyingParam.mShadowColor;
				shawdowWidth = mCurBuyingParam.mShadowWidth;
			} else {
				circleColr = mCurParam.mCircleColor;
				color = mCurParam.mColor;
				statusColor = mCurParam.mStatusColor;
				shawdowColor = mCurParam.mShadowColor;
				shawdowWidth = mCurParam.mShadowWidth;
			}
			if (mCurParam == mDrawTimeUnit[0] && TimeAxisView.this.isFocused()) {
				/** 绘制大圆，需要判断是否有焦点 */
				drawCircle(canvas, mCurParam.mRadius, offsetX, offsetY, circleColr);
				drawFocusCicle(canvas, mFocusRadius, offsetX, offsetY, 0xff);
			} else {
//				drawCircle(canvas, mCurParam.mRadius, offsetX, offsetY, circleColr);
				mShawdowPaint.setColor(circleColr);
				mShawdowPaint.setShadowLayer(shawdowWidth, 0, 0, shawdowColor);		
				canvas.drawCircle(offsetX, offsetY, mCurParam.mRadius, mShawdowPaint);
				
				/** 绘制边框 */
//				drawShawdow(canvas, offsetX, offsetY, mCurParam.mRadius, shawdowColor, shawdowWidth);
			}
			if (mCurParam.mFontSize > 0) {
				drawTimeAxisInfo(canvas, mCurInfo, mCurParam.mRadius, offsetX,
						offsetY, mTextSpace, color, mCurParam.mFontSize,
						statusColor, mCurParam.mStatusFontSize);
			}

		}
		
		public void draw(Canvas canvas, int offsetX, int offsetY, float input) {
			if (!mNeedAnim) {
				draw(canvas, offsetX, offsetY);
				return;
			}
			if (null == mCurParam || null == mCurInfo || null == mPrevParam) {
					AppDebug.i(TAG, "draw anim item unexception param: "
							+ mCurParam + " cur info:" + mCurInfo);
				return;
			}
			/** 动画处理 */
			int curCircleColor, curColor, curStatusColor;
			int prevCircleColor, prevColor, prevStatusColor;
//			int curShawdowColor, prevShawdowColor;
			
			/** 绘制圆圈 */
			if (null != mCurBuyingParam) {
				curCircleColor = mCurBuyingParam.mCircleColor;
				curColor = mCurBuyingParam.mColor;
				curStatusColor = mCurBuyingParam.mStatusColor;
//				curShawdowColor = mCurBuyingParam.mShadowColor;
			} else {
				curCircleColor = mCurParam.mCircleColor;
				curColor = mCurParam.mColor;
				curStatusColor = mCurParam.mStatusColor;
//				curShawdowColor = mCurParam.mShadowColor;
			}
			if (null != mPrevBuyingParam) {
				prevCircleColor = mPrevBuyingParam.mCircleColor;
				prevColor = mPrevBuyingParam.mColor;
				prevStatusColor = mPrevBuyingParam.mStatusColor;
//				prevShawdowColor = mPrevBuyingParam.mShadowColor;
			} else {
				prevCircleColor = mPrevParam.mCircleColor;
				prevColor = mPrevParam.mColor;
				prevStatusColor = mPrevParam.mStatusColor;
//				prevShawdowColor = mPrevParam.mShadowColor;
			}
			curCircleColor = (int) ((curCircleColor - prevCircleColor) * input + prevCircleColor);
			float centerY = (mCurCenterY - mPrevCenterY) * input + mPrevCenterY;
			float radius = (mCurParam.mRadius - mPrevParam.mRadius) * input
					+ mPrevParam.mRadius;
			offsetY += centerY;
			drawCircle(canvas, (int) radius, offsetX, offsetY, curCircleColor);
			
			/**绘制shawdow边框*/
//			drawShawdow(canvas, offsetX, offsetY, radius, shawdowColor, shawdowWidth);

			float fontSize = (mCurParam.mFontSize - mPrevParam.mFontSize)
					* input + mPrevParam.mFontSize;
			float statusFontSize = (mCurParam.mStatusFontSize - mPrevParam.mStatusFontSize)
					* input + mPrevParam.mStatusFontSize;

			if (fontSize > 0) {
				int space = (int) (mTextSpace * input);
				/** 文字的颜色不能变化 */
				if (input < 0.5) {
					/** 对alpha值进行变换 */
					input *= 2;
					curColor = getChangeColor(prevColor, input, true);
					curStatusColor = getChangeColor(prevStatusColor, input,
							true);

					drawTimeAxisInfo(canvas, mPrevInfo, (int) radius, offsetX,
							offsetY, space, prevColor, (int) fontSize,
							prevStatusColor, (int) statusFontSize);
				} else {
					input = (float) ((input - 0.5f) * 2);
					curColor = getChangeColor(curColor, input, false);
					curStatusColor = getChangeColor(curStatusColor, input,
							false);

					drawTimeAxisInfo(canvas, mCurInfo, (int) radius, offsetX,
							offsetY, space, curColor, (int) fontSize,
							curStatusColor, (int) statusFontSize);
				}

			}
			if (TimeAxisView.this.isFocused()) {
				int alpha = 0;
				float focusRadius = 0;

				// if (mPrevParam == mDrawTimeUnit[0]) {
				// if (input < 0.5) {
				// focusRadius = mFocusRadius * (1 - input);
				// alpha = (int) (255 * (1 - 2 * input));
				// }
				// }
				if (mCurParam == mDrawTimeUnit[0]) {
					/** 绘制大圆，需要判断是否有焦点 */
					focusRadius = radius + input
							* (mFocusRadius - mDrawTimeUnit[0].mRadius);
					alpha = (int) (255 * input);
				}

				if (focusRadius > 2) {
					drawFocusCicle(canvas, (int) focusRadius, offsetX, offsetY,
							alpha);
				}
			}

		}
	}

    /*
     * (non-Javadoc)
     * @see com.yunos.tv.app.widget.focus.listener.FocusListener#getClipFocusRect()
     */
    @Override
    public Rect getClipFocusRect() {
        //TODO Auto-generated method stub
        if (mClipFocusRect != null) {
            return mClipFocusRect;
        }
        return new Rect();
    }
}
