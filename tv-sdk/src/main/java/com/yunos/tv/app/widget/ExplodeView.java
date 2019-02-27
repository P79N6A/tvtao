package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.ExplodeAdapter.ExplodeItem;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;

public class ExplodeView extends ViewGroup implements ItemListener {
	static final String TAG = "ExplodeView";
	static final boolean DEBUG = false;

	public ExplodeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setChildrenDrawingOrderEnabled(true);
		// TODO Auto-generated constructor stub
	}

	public ExplodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setChildrenDrawingOrderEnabled(true);
		// TODO Auto-generated constructor stub
	}

	public ExplodeView(Context context) {
		super(context);
		setChildrenDrawingOrderEnabled(true);
		// TODO Auto-generated constructor stub
	}

	int mHeightMeasureSpec;
	int mWidthMeasureSpec;
	ExplodeAdapter mAdapter;
	Rect mCenterRect = new Rect();
	ExplodeInfo mInfo = new ExplodeInfo();
	ExplodeRunnable mRunnable = new ExplodeRunnable();
	boolean mIsExplode = false;
	boolean mIsEnableExpode = true; //默认允许爆米花展开
	View mCenter = null;
	
	public void setEnableExplode(boolean enable) {
		mIsEnableExpode = enable;
	}
	
	public void explode() {
		if (!mIsEnableExpode || mIsExplode) {
			return;
		}
		mIsExplode = true;
		mRunnable.explode();
	}

	public void collipse() {
		if(!mIsEnableExpode) {
			return;
		}
		mIsExplode = false;
		mRunnable.collipse();
	}

	public View getCenterView() {
		return getChildAt(0);
	}

	public ExplodeAdapter getAdapter(){
	    return mAdapter;
	}
	
	public void setAdapter(ExplodeAdapter adapter) {
	    boolean needUpdate = mAdapter != null;
		mAdapter = adapter;

		if(needUpdate && mInfo != null){
		    mInfo.update();
		    invalidate();
	    }
	}

//	@Override
//	protected void onDraw(Canvas canvas) {
//		mInfo.draw(canvas);
//		super.onDraw(canvas);
//	}
	
	@Override
	protected void dispatchDraw(Canvas canvas) {
		if(mIsEnableExpode) {
			mInfo.draw(canvas);
		}
	    super.dispatchDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);

		mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
		mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if(DEBUG){
			Log.d(TAG, "onLayout");
		}
		removeAllViewsInLayout();
		makeCenterView();
		makeExtentViews();
	}

	private void makeCenterView() {
		if(mCenter == null){
			mCenter = mAdapter.getCenterView();
		}
		
		if (mCenter == null) {
			throw new IllegalArgumentException("ExplodeView.makeCenterView center view is null");
		}

		LayoutParams lp = mCenter.getLayoutParams();
		if (lp == null) {
			lp = generateDefaultLayoutParams();
		}

		addViewInLayout(mCenter, 0, lp);

		int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec, getPaddingLeft() + getPaddingRight(), lp.width);
		int childHeightSpec = ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, getPaddingTop() + getPaddingBottom(), lp.height);

		mCenter.measure(childWidthSpec, childHeightSpec);

		int diff = (getMeasuredWidth() - mCenter.getMeasuredWidth() - getPaddingLeft() - getPaddingRight()) / 2;
		mCenterRect.left = diff + getPaddingLeft();
		mCenterRect.right = getMeasuredWidth() - diff - getPaddingRight();
		mCenterRect.top = getMeasuredHeight() - mCenter.getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
		mCenterRect.bottom = getMeasuredHeight() - getPaddingBottom();

		mCenter.layout(mCenterRect.left, mCenterRect.top, mCenterRect.right, mCenterRect.bottom);
	}

	private void makeExtentViews() {
		int count = mAdapter.getExplodeCount();
		mInfo.list = new Info[count];

		for (int index = 0; index < count; index++) {
			makeExtentView(index);
		}
	}

	private void makeExtentView(int index) {
		ExplodeItem item = mAdapter.getExplodeItem(index);
		Position newSt = convertXY(item.getStaticPosition());
		Position newDy = convertXY(item.getDynamicPosition());
		Position newFa = convertXY(item.getFinalPosition());

		mInfo.list[item.getOrder()] = new Info(item.getExplode(), item.getBack(), item.getBackgroundPadding(), newSt, newDy, newFa, item.getDyncmicDuration(), item.getFinalDuration(), 300, item.getFrame(), item.getWidth(),
				item.getHeight(),item.getExpandAlpha(),item.getCollipseAlpha());

		// View explode = item.getExplode();
		// Position st = item.getStaticPosition();
		//
		// LayoutParams lp = explode.getLayoutParams();
		// if (lp == null) {
		// lp = generateDefaultLayoutParams();
		// }
		// explode.setVisibility(View.INVISIBLE);
		// addViewInLayout(explode, index + 1, lp);
		// int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec,
		// getPaddingLeft() + getPaddingRight(), lp.width);
		// int childHeightSpec =
		// ViewGroup.getChildMeasureSpec(mHeightMeasureSpec, getPaddingTop() +
		// getPaddingBottom(), lp.height);
		// explode.measure(childWidthSpec, childHeightSpec);
		//
		// int xDiff = explode.getMeasuredWidth() / 2;
		// int yDiff = explode.getMeasuredHeight() / 2;
		// int left = newSt.x() - xDiff;
		// int right = newSt.x() + xDiff;
		// int top = newSt.y() - yDiff;
		// int bottom = newSt.y() + yDiff;
		//
		// explode.layout(left, top, right, bottom);
		//
		// mInfo.map.put(index, new Info(explode, newSt, newDy, newFa,
		// item.getDyncmicDuration(), item.getFinalDuration(), 300,
		// item.getFrame()));
	}

	private Position convertXY(Position p) {
		int x = p.x();
		int y = p.y();

		int xDiff = getMeasuredWidth() / 2;
		int yDiff = mCenterRect.height() / 2;
		int newX = x + xDiff;
		int newY = yDiff - y + mCenterRect.top;

		return new Position(newX, newY);
	}

	private boolean trackExplode() {
		if (DEBUG) {
			Log.d(TAG, "trackExplode");
		}
		int count = mInfo.list.length;
		boolean isContinue = false;
		for (int index = 0; index < count; index++) {
			Info info = mInfo.list[index];
			if (!info.isExplodeStart()) {
				if (info.frame == mInfo.frame) {
					info.startExplode();
				}
				isContinue = true;
			} else {
				if (info.computeExplodeOffset()) {
					isContinue = true;
				}
			}
		}

		mInfo.frame++;
		return isContinue;
	}

	private void trackExplodeEnd() {
		int count = mInfo.list.length;
		boolean isContinue = false;
		for (int index = 0; index < count; index++) {
			Info info = mInfo.list[index];
			info.endExplode();
		}
	}

	private void trackCollipseEnd() {
		int count = mInfo.list.length;
		boolean isContinue = false;
		for (int index = 0; index < count; index++) {
			Info info = mInfo.list[index];
			info.endCollipse();
		}
	}

	private boolean trackCollipse() {
		int count = mInfo.list.length;
		boolean isContinue = false;
		for (int index = 0; index < count; index++) {
			Info info = mInfo.list[index];
			if (info.computeCollipseOffset()) {
				isContinue = true;
			}
		}

		return isContinue;
	}

	private void startCollipse() {
		int count = mInfo.list.length;
		boolean isContinue = false;
		for (int index = 0; index < count; index++) {
			Info info = mInfo.list[index];
			info.startCollipse();
		}
	}

	class ExplodeRunnable implements Runnable {
		static final int MODE_EXPAND = 1;
		static final int MODE_COLLIPSE = 2;
		int mode = 0;

		boolean isCollipseStart = false;

		public void explode() {
			if (DEBUG) {
				Log.d(TAG, "ExplodeRunnable: explode");
			}
			mode = MODE_EXPAND;
			post(this);
		}

		public void collipse() {
			if (DEBUG) {
				Log.d(TAG, "ExplodeRunnable: collipse");
			}
			mode = MODE_COLLIPSE;
			startCollipse();
			post(this);
		}

		public void end() {
			if (mode == MODE_EXPAND) {
				trackExplodeEnd();
			} else if (mode == MODE_COLLIPSE) {
				trackCollipseEnd();
				isCollipseStart = false;
			}
			mode = 0;
			mInfo.frame = 0;
		}

		@Override
		public void run() {
			if (mode == MODE_EXPAND) {
				if (trackExplode()) {
					postDelayed(this, 0);
					postInvalidateDelayed(0);
				} else {
					end();
				}
			} else if (mode == MODE_COLLIPSE) {
				if (trackCollipse()) {
					postDelayed(this, 0);
					postInvalidateDelayed(0);
				} else {
					end();
				}
			}

		}

	}

	class ExplodeInfo {
		public ExplodeView.Info[] list;
		public int frame = 0;

		public void draw(Canvas canvas) {
			int count = list.length;
			for (int index = 0; index < count; index++) {
				Info info = list[index];
				if (info != null) {
					info.draw(canvas);
				}
			}
		}
		
		public void update(){
		    for(int i = 0; i < mAdapter.getExplodeCount(); i++){
//		    for(int i = 0; i < list.length; i++){
		        ExplodeItem item = mAdapter.getExplodeItem(i);
		        if(item != null && item.getExplode() != null) {
			        Info info =list[item.getOrder()];
			        if(info != null){
			            info.update(item.getExplode());
			        }
		        }
		    }
		}
	}

	class PositionInfo {
		Position position = new Position();
		Position offset = new Position();

		public int getOffsetX() {
			return offset.x();
		}

		public int getOffsetY() {
			return offset.y();
		}
	}

	class ExplodePositioninfo extends PositionInfo {
		Position st;
		Position dy;
		Position fa;

		int dyDuration;
		int faDuration;
		int coDuration;

		Scroller scrDy = new Scroller(getContext(), new AccelerateInterpolator());
		Scroller scrFa = new Scroller(getContext(), new DecelerateInterpolator(1.25f));
		Scroller scrCo = new Scroller(getContext(), new DecelerateInterpolator());

		boolean faStart = false;
		boolean dyStart = false;

		public ExplodePositioninfo(Position s, Position d, Position f, int exD1, int exD2, int coD) {
			st = s;
			dy = d;
			fa = f;

			dyDuration = exD1;
			faDuration = exD2;
			coDuration = coD;

			position.set(s);
		}

		public void startExplode() {
			if (DEBUG) {
				Log.d(TAG, "startExplode");
			}
			position.set(st);
			dyStart = true;
			scrDy.startScroll(st.x(), st.y(), dy.x() - st.x(), dy.y() - st.y(), dyDuration);
			faStart = false;
		}

		public boolean computeOffset() {
			if (scrDy.computeScrollOffset()) {
				int x = scrDy.getCurrX();
				int y = scrDy.getCurrY();
				int offsetX = x - position.x();
				int offsetY = y - position.y();
				position.set(x, y);
				offset.set(offsetX, offsetY);
				return true;
			} else if (!faStart) {
				position.set(dy);
				faStart = true;
				scrFa.startScroll(dy.x(), dy.y(), fa.x() - dy.x(), fa.y() - dy.y(), faDuration);
				offset.set(0, 0);
				return true;
			} else if (scrFa.computeScrollOffset()) {
				int x = scrFa.getCurrX();
				int y = scrFa.getCurrY();
				int offsetX = x - position.x();
				int offsetY = y - position.y();
				position.set(x, y);

				offset.set(offsetX, offsetY);
				return true;
			} else {
				return false;
			}
		}

		public Position finalPosition() {
			return fa;
		}

		public Position startPosition() {
			return st;
		}

		public int duration() {
			return dyDuration + faDuration;
		}

		public Position position() {
			return position;
		}

		public void end() {
			dyStart = false;
			faStart = false;
		}

		public boolean isExplodeStart() {
			return dyStart || faStart;
		}

		public void forcedFinish() {
			scrDy.forceFinished(true);
			scrFa.forceFinished(true);
			scrCo.forceFinished(true);

			end();
		}
	}

	class CollipsePositioninfo extends PositionInfo {
		Position st = new Position();
		Position fa = new Position();

		Scroller scro = new Scroller(getContext(), new AccelerateInterpolator());

		public CollipsePositioninfo() {

		}

		public void startCollipse(Position s, Position f, int d) {
			st.set(s);
			fa.set(f);

			position.set(s);
			offset.set(0, 0);
			scro.startScroll(st.x(), st.y(), fa.x() - st.x(), fa.y() - st.y(), d);
		}

		public boolean computeOffset() {
			if (scro.computeScrollOffset()) {
				int x = scro.getCurrX();
				int y = scro.getCurrY();
				int offsetX = x - position.x();
				int offsetY = y - position.y();
				position.set(x, y);

				offset.set(offsetX, offsetY);

				return true;
			}

			return false;
		}

		public void end() {

		}
	}

	class AlphaInfo {
		int st;
		int fa;

		int cu;
		Scroller scro = new Scroller(getContext(), new AccelerateInterpolator());

		public void startAlpha(int s, int f, int d) {
			cu = s;
			st = s;
			fa = f;

			scro.startScroll(st, 0, fa - st, 0, d);
		}

		public boolean computeOffset() {
			if (scro.computeScrollOffset()) {
				cu = scro.getCurrX();

				return true;
			}

			return false;
		}

		public int alpha() {
			return cu;
		}

		public boolean isFinised() {
			return scro.isFinished();
		}

		public void end() {

		}

		public void forcedFinish() {
			scro.forceFinished(true);
		}
	}

	class Info {
		Drawable explode;
		Drawable back;
		Rect backPadding = new Rect();
		ExplodePositioninfo explodeInfo;
		CollipsePositioninfo collipseInfo;
		AlphaInfo alphaInfo;
		int frame = 0;
		int expendAlpha = 230; //0.9 * 255
		int collipseAlpha = 102;  //0.4 * 255
		
		int width;
		int height;

		Position position = new Position();
		int mAlpha;

		boolean mVisible = false;

		//
		public Info(Drawable e, Drawable b, Rect bgPadding, Position s, Position d, Position f, int exD1, int exD2, int coD, int r, int w, int h,int eAlpha,int cAlpha) {
			explode = e;
			back = b;
			
			if(bgPadding != null){
				backPadding = bgPadding;
			} else if (back != null) {
				back.getPadding(backPadding);
			}

			frame = r;
			explodeInfo = new ExplodePositioninfo(s, d, f, exD1, exD2, coD);
			collipseInfo = new CollipsePositioninfo();
			alphaInfo = new AlphaInfo();

			width = w;
			height = h;

		    expendAlpha = eAlpha;
		    collipseAlpha = cAlpha;
			
			position.set(s);
			position.offset(-width / 2, -height / 2);
		}

		public void setVisible(boolean visible){
			mVisible = visible;
		}
		
		public void update(Drawable e){
		    explode = e;
		}
		
		public void draw(Canvas canvas) {
			if(!mVisible){
				return;
			}
			
			int left = position.x();
			int top = position.y();
			int right = position.x() + width;
			int bottom = position.y() + height;

			if (back != null) {
				back.setBounds(left, top, right, bottom);
				back.setAlpha(mAlpha);
				back.draw(canvas);
			}
			
			if(explode != null){
    			explode.setBounds(left + backPadding.left, top + backPadding.top, right - backPadding.right, bottom - backPadding.bottom);
    			explode.setAlpha(mAlpha);
    			explode.draw(canvas);
			}
		}

		public void startExplode() {
			explodeInfo.startExplode();
			alphaInfo.startAlpha(collipseAlpha, expendAlpha, explodeInfo.duration());
			mAlpha = collipseAlpha;
			setVisible(true);
		}

		public void startCollipse() {
			if (explodeInfo.isExplodeStart()) {
				explodeInfo.forcedFinish();
				alphaInfo.forcedFinish();
			}

			position.set(explodeInfo.position());
			position.offset(-width / 2, -height / 2);
			collipseInfo.startCollipse(explodeInfo.position(), explodeInfo.startPosition(), 300);
			alphaInfo.startAlpha(alphaInfo.cu, expendAlpha, 300);
		}

		private void startAlpha(int s, int f, int d) {
			alphaInfo.startAlpha(s, f, d);
		}

		private void offsetPosition(int x, int y) {
			position.offset(x, y);
		}

		public boolean computeExplodeOffset() {
			if (DEBUG) {
				Log.d(TAG, "ExplodeRunnable: Info computeExplodeOffset");
			}
			boolean hr = explodeInfo.computeOffset();
			if (hr) {
				offsetPosition(explodeInfo.getOffsetX(), explodeInfo.getOffsetY());
			}

			if (!alphaInfo.isFinised() && alphaInfo.computeOffset()) {
				mAlpha = alphaInfo.alpha();
				// expView.setAlpha((float) alphaInfo.alpha() / 100);
			}
			return hr;
		}

		public boolean computeCollipseOffset() {

			boolean hr = collipseInfo.computeOffset();
			if (hr) {
				offsetPosition(collipseInfo.getOffsetX(), collipseInfo.getOffsetY());
				explodeInfo.position().offset(collipseInfo.getOffsetX(), collipseInfo.getOffsetY());
			}

			if (!alphaInfo.isFinised() && alphaInfo.computeOffset()) {
				mAlpha = alphaInfo.alpha();
				// expView.setAlpha((float) alphaInfo.alpha() / 100);
			}
			return hr;
		}

		public boolean isExplodeStart() {
			return explodeInfo.isExplodeStart();
		}

		public void endExplode() {
			explodeInfo.end();
			alphaInfo.end();
		}

		public void endCollipse() {
			setVisible(false);
			collipseInfo.end();
			alphaInfo.end();
		}

	}

	@Override
	public boolean isScale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public FocusRectParams getFocusParams() {
		Rect r = new Rect();
		View center = getChildAt(0);
		center.getFocusedRect(r);
		offsetDescendantRectToMyCoords(center, r);

		return new FocusRectParams(r, 0.5f, 0.5f);
	}

	@Override
	public int getItemWidth() {
		// TODO Auto-generated method stub
		return getChildAt(0).getWidth();
	}

	@Override
	public int getItemHeight() {
		// TODO Auto-generated method stub
		return getChildAt(0).getHeight();
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
		return true;
	}
}
