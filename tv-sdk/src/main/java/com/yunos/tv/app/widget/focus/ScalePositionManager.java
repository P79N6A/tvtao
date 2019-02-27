package com.yunos.tv.app.widget.focus;

import android.graphics.Rect;
import android.graphics.RectF;

public class ScalePositionManager {
	private static ScalePositionManager manager = null;

	public static ScalePositionManager instance() {
		if (manager == null) {
			manager = new ScalePositionManager();
		}

		return manager;
	}

	public Rect getScaledRect(Rect r, float scaleX, float scaleY) {
		Rect rScaled = new Rect();
		int imgW = r.width();
		int imgH = r.height();

		rScaled.left = (int) (r.left - (scaleX - 1.0f) * imgW / 2);
		rScaled.right = (int) (r.left + imgW * scaleX);
		rScaled.top = (int) (r.top - (scaleY - 1.0f) * imgH / 2);
		rScaled.bottom = (int) (r.top + imgH * scaleY);

		return rScaled;
	}

	public Rect getScaledRect(Rect r, float scaleX, float scaleY, float coefX, float coefY) {
		int width = r.width();
		int height = r.height();

		float diffScaleX = scaleX - 1.0f;
		float diffScaleY = scaleY - 1.0f;
		r.left -= width * coefX * diffScaleX;
		r.right += width * (1.0f - coefX) * diffScaleX;
		r.top -= height * coefY * diffScaleY;
		r.bottom += height * (1.0f - coefY) * diffScaleY;

		return r;
	}

	public void getScaledRect(Rect src, Rect dst, float scaleX, float scaleY, float coefX, float coefY) {
		int width = src.width();
		int height = src.height();

		float diffScaleX = scaleX - 1.0f;
		float diffScaleY = scaleY - 1.0f;
		dst.left = (int) (src.left - width * coefX * diffScaleX);
		dst.right = (int) (src.right + width * (1.0f - coefX) * diffScaleX);
		dst.top = (int) (src.left - height * coefY * diffScaleY);
		dst.bottom = (int) (src.bottom + height * (1.0f - coefY) * diffScaleY);
	}

	public void getScaledRect(RectF src, RectF dst, float scaleX, float scaleY, float coefX, float coefY) {
		float width = src.right - src.left;
		float height = src.bottom - src.top;

		float diffScaleX = scaleX - 1.0f;
		float diffScaleY = scaleY - 1.0f;
		dst.left = src.left - width * coefX * diffScaleX;
		dst.right = src.right + width * (1.0f - coefX) * diffScaleX;
		dst.top = src.left - height * coefY * diffScaleY;
		dst.bottom = src.bottom + height * (1.0f - coefY) * diffScaleY;
	}

	public void getScaledRectNoReturn(Rect r, float scaleX, float scaleY) {
		int imgW = r.width();
		int imgH = r.height();

		r.left = (int) (r.left - (scaleX - 1.0f) * imgW / 2);
		r.right = (int) (r.left + imgW * scaleX);
		r.top = (int) (r.top - (scaleY - 1.0f) * imgH / 2);
		r.bottom = (int) (r.top + imgH * scaleY);
	}
}
