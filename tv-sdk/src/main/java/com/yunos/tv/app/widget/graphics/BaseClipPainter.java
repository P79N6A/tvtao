package com.yunos.tv.app.widget.graphics;

import android.content.Context;
import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class BaseClipPainter extends BasePainter {

	private final int S_MIN_POINT_NUM = 3;// 裁剪区域必需3个以上的点才能组成一个封闭区域
	private Path mClipPath = new Path();
	private List<Point> mClipPointList = new ArrayList<Point>();

	public BaseClipPainter(Context context) {
		super(context);
	}

	public void removeClipPoint(Point point) {
		int size = mClipPointList == null ? 0 : mClipPointList.size();
		if (size <= 0) {
			return;
		}

		Point pointTemp = null;
		for (int i = 0; i < size; i++) {
			pointTemp = mClipPointList.get(i);
			if (point.x == pointTemp.x && point.y == pointTemp.y) {
				mClipPointList.remove(pointTemp);
				size = mClipPointList.size();
			}
		}
	}

	public void removeAllClipPoints() {
		if (mClipPointList == null) {
			return;
		}

		mClipPointList.clear();
	}

	public void addClipPoint(Point point) {
		if (mClipPointList == null) {
			return;
		}

		mClipPointList.add(point);
	}

	public void addClipPoint(int x, int y) {
		if (mClipPointList == null) {
			return;
		}

		mClipPointList.add(new Point(x, y));
	}

	public Path getClipPath() {
		if (mClipPointList == null || mClipPointList.isEmpty()) {
			return mClipPath;
		}
		int size = mClipPointList.size();
		if (size < S_MIN_POINT_NUM) {
			throw new IllegalArgumentException("clip point size must >= 3");
		}

		Path path = mClipPath;
		path.reset();
		Point point = mClipPointList.get(0);
		path.moveTo(point.x, point.y);
		for (int i = 1; i < size; i++) {
			point = mClipPointList.get(i);
			path.lineTo(point.x, point.y);
		}
		path.close();

		return path;
	}
	
	/**
	 * 相对于点x,y的path
	 * @param x
	 * @param y
	 * @return
	 */
	public Path getRelativeClipPath(int x, int y) {
		Path path = new Path();
		if (mClipPointList == null || mClipPointList.isEmpty()) {
			return path;
		}
		int size = mClipPointList.size();
		if (size < S_MIN_POINT_NUM) {
			throw new IllegalArgumentException("clip point size must >= 3");
		}
		Point point = mClipPointList.get(0);
		path.moveTo(point.x - x, point.y - y);
		for (int i = 1; i < size; i++) {
			point = mClipPointList.get(i);
			path.lineTo(point.x - x, point.y - y);
		}
		path.close();

		return path;
	}

	public boolean isNeedClip() {
		int size = mClipPointList == null ? 0 : mClipPointList.size();
		return size >= S_MIN_POINT_NUM;
	}

}
