package com.yunos.tvtaobao.juhuasuan.widget;

import android.graphics.Rect;
import android.view.View;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.FrameInterpolator;



public class FocusPaddingUtil {

	static Rect mRect = new Rect();

	static public Rect getFocusPadding(Rect originalRect, Rect lastOriginalRect, int direction, int focusFrameRate,
									   FrameInterpolator interpolator, int fromLeft, int formRight, int formUp, int fromDown) {

		if (direction == View.FOCUS_RIGHT) {
			switch (fromLeft) {
			case FocusStyle.FROM_LEFT_SCALE:
				return computeScale(originalRect, lastOriginalRect, direction, focusFrameRate, interpolator);
			case FocusStyle.FROM_LEFT_CATCH:
				return computeCatch(originalRect, lastOriginalRect, direction, focusFrameRate, interpolator);
			default:
				break;
			}
		}

		if (direction == View.FOCUS_LEFT) {
			switch (formRight) {
			case FocusStyle.FROM_RIGHT_SCALE:
				return computeScale(originalRect, lastOriginalRect, direction, focusFrameRate, interpolator);
			case FocusStyle.FROM_RIGHT_CATCH:
				return computeCatch(originalRect, lastOriginalRect, direction, focusFrameRate, interpolator);
			default:
				break;
			}
		}

		if (direction == View.FOCUS_DOWN) {
			switch (formUp) {
			case FocusStyle.FROM_UP_SCALE:
				return computeScale(originalRect, lastOriginalRect, direction, focusFrameRate, interpolator);
			case FocusStyle.FROM_UP_CATCH:
				return computeCatch(originalRect, lastOriginalRect, direction, focusFrameRate, interpolator);
			default:
				break;
			}
		}

		if (direction == View.FOCUS_UP) {
			switch (fromDown) {
			case FocusStyle.FROM_DOWN_SCALE:
				return computeScale(originalRect, lastOriginalRect, direction, focusFrameRate, interpolator);
			case FocusStyle.FROM_DOWN_CATCH:
				return computeCatch(originalRect, lastOriginalRect, direction, focusFrameRate, interpolator);
			default:
				break;
			}

		}
		return null;
	}

	static Rect computeCatch(Rect originalRect, Rect lastOriginalRect, int direction, int focusFrameRate, FrameInterpolator interpolator) {
		int leftPadding = 0;
		int rightpadding = 0;
		int topPadding = 0;
		int bottomPadding = 0;

		int diffLeft = Math.abs(originalRect.left - lastOriginalRect.left);
		int diffRight = Math.abs(originalRect.right - lastOriginalRect.right);
		int diffTop = Math.abs(originalRect.top - lastOriginalRect.top);
		int diffBottom = Math.abs(originalRect.bottom - lastOriginalRect.bottom);

		float scale = (float) (focusFrameRate - 1) / focusFrameRate;
		if (scale == 0) {
			return null;
		}

		scale = interpolator.getInterpolation(scale);
		if (diffLeft > diffRight) {
			rightpadding = (int) (diffLeft * (1.0f - scale));
			leftPadding = -rightpadding;
		} else if (diffLeft != diffRight) {
			leftPadding = (int) (diffRight * (1.0f - scale));
			if (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
				leftPadding = -leftPadding;
			}
		}

		if (diffTop > diffBottom) {
			bottomPadding = (int) (diffTop * (1.0f - scale));
			topPadding = -bottomPadding;
		} else {
			if (originalRect.bottom < lastOriginalRect.bottom) {
				topPadding = -((int) (diffBottom * (1.0f - scale)));
			} else {
				topPadding = 0;//((int) (diffBottom * (1.0f - scale)));
			}
			if (direction == View.FOCUS_UP || direction == View.FOCUS_DOWN) {
				topPadding = -topPadding;
			}
		}

		mRect.set(leftPadding, topPadding, rightpadding, bottomPadding);

		return mRect;
	}

	static float forcedScaled = 0.1f;

	static Rect computeScale(Rect originalRect, Rect lastOriginalRect, int direction, int focusFrameRate, FocusedBasePositionManager.FrameInterpolator interpolator) {
		int leftPadding = 0;
		int rightpadding = 0;
		int topPadding = 0;
		int bottomPadding = 0;

		int diffLeft = Math.abs(originalRect.left - lastOriginalRect.left);
		int diffRight = Math.abs(originalRect.right - lastOriginalRect.right);
		int diffTop = Math.abs(originalRect.top - lastOriginalRect.top);
		int diffBottom = Math.abs(originalRect.bottom - lastOriginalRect.bottom);

		float scale = (float) (focusFrameRate - 1) / focusFrameRate;
		if (scale == 0) {
			return null;
		}

		scale = interpolator.getInterpolation(scale);
		if (diffLeft > diffRight) {
			leftPadding = (int) (diffLeft * (1.0f - scale));
			rightpadding = -leftPadding;
		} else if (diffLeft != diffRight) {
			rightpadding = -((int) (diffRight * (1.0f - scale)));
			if (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
				leftPadding = -rightpadding;
			} else {
				leftPadding = -rightpadding;
			}
		} else {
			if (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) {
				leftPadding = (int) (originalRect.width() * forcedScaled);
				rightpadding = -leftPadding;
			}
		}

		if (diffTop > diffBottom) {
			topPadding = (int) (diffTop * (1.0f - scale));
			bottomPadding = -topPadding;
		} else if (diffTop != diffBottom) {
			bottomPadding = -((int) (diffBottom * (1.0f - scale)));
			if (direction == View.FOCUS_UP || direction == View.FOCUS_DOWN) {
				topPadding = -bottomPadding;
			} else {
				topPadding = -bottomPadding;
			}
		}

		mRect.set(leftPadding, topPadding, rightpadding, bottomPadding);

		return mRect;
	}
}
