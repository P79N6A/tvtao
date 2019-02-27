package com.yunos.tv.app.widget.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;

import java.util.ArrayList;

public class LinesPainter extends BasePainter {

	public static final String TAG = "LinesPainter";

	public LinesPainter(Context context) {
		super(context);
	}

	ArrayList<LinePoint> mList = new ArrayList<LinePoint>(20);
	int mTotalLength = 0;

	public void addLine(Point fromPoint, Point dstPoint) {
		LinePoint line = new LinePoint(fromPoint, dstPoint);
		mList.add(line);
		mTotalLength += line.length();
	}
	
	public void clearLines(){
		if(mList != null){
			mList.clear();
		}
	}

	public void show() {
		mScroller.startScroll(0, 0, mTotalLength, 0, DEFAULT_ANIMATE_DURATION);
		paintInvalidate();
	}

	public void show(int duration) {
		mScroller.startScroll(0, 0, mTotalLength, 0, duration);
		paintInvalidate();
	}
	@Override
	public boolean draw(Canvas canvas) {
		if (!super.draw(canvas)) {
			return false;
		}
		drawLines(canvas);
		paintInvalidate();
		return true;
	}

	void drawLines(Canvas canvas) {
		mScroller.computeScrollOffset();
		int dstLength = mDirection == LOCK_WISE ? mScroller.getCurrX() : mTotalLength - mScroller.getCurrX();
		int length = 0;
		int size = mList.size();
		for (int index = 0; index < size; index++) {
			LinePoint line = mList.get(index);
			if (dstLength - length >= line.length()) {
				line.drawLine(canvas);
			} else {
				line.drawLineByLength(canvas, dstLength - length);
				break;
			}

			length += line.length();
		}
	}

	public class LinePoint {
		int mLength;
		Point mFromPoint;
		Point mDstPoint;

		public LinePoint(Point fromPoint, Point dstPoint) {
			mFromPoint = new Point(fromPoint);
			mDstPoint = new Point(dstPoint);
			mLength = getDistance();
		}

		private int getDistance() {
			return (int) Math.sqrt((mDstPoint.x - mFromPoint.x) * (mDstPoint.x - mFromPoint.x) + (mDstPoint.y - mFromPoint.y) * (mDstPoint.y - mFromPoint.y));
		}

		public int length() {
			return mLength;
		}

		public void drawLine(Canvas canvas) {
			canvas.drawLine(mFromPoint.x, mFromPoint.y, mDstPoint.x, mDstPoint.y, getPaint());
		}

		public void drawLineByLength(Canvas canvas, int length) {
			int x = (int) (mDstPoint.x - ((double) (length() - length) / length()) * (mDstPoint.x - mFromPoint.x));
			int y = (int) (((double) length / length()) * (mDstPoint.y - mFromPoint.y) + mFromPoint.y);
			canvas.drawLine(mFromPoint.x, mFromPoint.y, x, y, getPaint());
		}
	}
}
