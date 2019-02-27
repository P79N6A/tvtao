/*
 * Copyright 2011 Alibaba Group.
 */
package com.yunos.tvlife.app.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;

/**
 * 为播放器提供的一种CoverFlow的特殊版本：
 * <p>
 * 中间的三个Item正面显示，靠近边缘处的控件有叠加效果.
 * </p>
 * 
 */
public class CoverFlow extends AbsCoverFlow {
	private static final String TAG = "CoverFlow";
	private static final boolean DEBUG = false;

	private static final int DEFAULT_MID_ITEM_COUNT = 3;

	private int mMidItemCount = DEFAULT_MID_ITEM_COUNT;// default is 5

	public CoverFlow(final Context context) {
		super(context);
	}

	public CoverFlow(final Context context, final AttributeSet attrs) {
		super(context, attrs, android.R.attr.galleryStyle);

	}

	public CoverFlow(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #setStaticTransformationsEnabled(boolean)
	 */
	@Override
	protected boolean getChildStaticTransformation(final View child, final Transformation t) {
		transformChidMatrix(child, t);
		return true;
	}

	// 中间不变形的
	public void setMidItemCount(int num) {
		// check larger than one
		if (num <= 0) {
			num = 1;
		}

		// check this num is odd
		if ((num & 0x1) != 1) {
			num += 1;
		}

		mMidItemCount = num;
	}
	
	public int getMidItemCount(){
		return mMidItemCount;
	}

	private void transformChidMatrixMidItemCount1(View child, Transformation t) {
		final int childCenter = getCenterOfView(child);

		final int childWidth = child.getWidth();

		int rotationAngle = 0;

		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		if (childCenter == mCoveflowCenter) {
			transformImageBitmap(child, t, 0);
		} else {
			/*
			 * rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) /
			 * childWidth) * mMaxRotationAngle);
			 * 
			 * if (Math.abs(rotationAngle) > mMaxRotationAngle) { rotationAngle
			 * = (rotationAngle < 0)?(-mMaxRotationAngle):mMaxRotationAngle; }
			 * transformImageBitmap(child, t, rotationAngle);
			 */

			if (DEBUG)
				Log.d(TAG, "childCenter - mCoveflowCenter = " + (childCenter - mCoveflowCenter) + ", 2*(childWidth + mSpacing) = "
						+ (2 * (childWidth + mSpacing)));

			if (mCoveflowCenter - childCenter > 0) {
				// left
				rotationAngle = (int) ((float) (mCoveflowCenter - childCenter) / childWidth * mMaxRotationAngle / 2);
			} else {
				// right
				rotationAngle = (int) ((float) (mCoveflowCenter - childCenter) / childWidth * mMaxRotationAngle / 2);
			}
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = rotationAngle < 0 ? -mMaxRotationAngle : mMaxRotationAngle;
			}
			transformImageBitmap(child, t, rotationAngle);

			int childDistance = Math.abs(childCenter - mCoveflowCenter);

			float distanceRotate = 1 * (childWidth + mSpacing);
			float distanceShift = 2 * (childWidth + mSpacing);

			float shiftArea = childWidth / 1.6f;

			if (mCoveflowCenter - childCenter > 0) {
				if (childDistance >= distanceRotate && childDistance < distanceShift) {
					Matrix matrix = t.getMatrix();

					float radio = 1.0f * (childDistance - distanceRotate) / ((childWidth + mSpacing));

					matrix.postTranslate(shiftArea * radio, 0);
				} else if (childDistance >= distanceShift) {// simple shift
															// adapt
					/*
					 * Matrix matrix = t.getMatrix();
					 * matrix.postTranslate(shiftArea, 0);
					 */

					Matrix matrix = t.getMatrix();

					float radio = 1.0f * (childDistance - distanceRotate) / ((childWidth + mSpacing));

					matrix.postTranslate(shiftArea * radio, 0);
				}
			} else {
				if (childDistance >= distanceRotate && childDistance <= distanceShift) {
					Matrix matrix = t.getMatrix();
					float radio = 1.0f * (childDistance - distanceRotate) / ((childWidth + mSpacing));

					matrix.postTranslate(-shiftArea * radio, 0);
				} else if (childDistance >= distanceShift) {// simple shift
															// adapt
					/*
					 * Matrix matrix = t.getMatrix();
					 * matrix.postTranslate(-shiftArea, 0);
					 */
					Matrix matrix = t.getMatrix();
					float radio = 1.0f * (childDistance - distanceRotate) / ((childWidth + mSpacing));

					matrix.postTranslate(-shiftArea * radio, 0);
				}
			}
		}
	}

	private void transformChidMatrixMidItemCount3(View child, Transformation t) {
		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		int rotationAngle = 0;
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		int notTransformLen = (childWidth + mSpacing);
		if (Math.abs(childCenter - mCoveflowCenter) <= notTransformLen) {
			transformImageBitmap(child, t, 0);
		} else {
			if (DEBUG)
				Log.d(TAG, "childCenter - mCoveflowCenter = " + (childCenter - mCoveflowCenter) + ", 2*(childWidth + mSpacing) = "
						+ (2 * (childWidth + mSpacing)));

			if (mCoveflowCenter - childCenter > 0) {
				// left
				rotationAngle = (int) ((float) (mCoveflowCenter - childCenter - notTransformLen) / childWidth * mMaxRotationAngle / 2);
			} else {
				// right
				rotationAngle = (int) ((float) (mCoveflowCenter - childCenter + notTransformLen) / childWidth * mMaxRotationAngle / 2);
			}
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = rotationAngle < 0 ? -mMaxRotationAngle : mMaxRotationAngle;
			}
			transformImageBitmap(child, t, rotationAngle);

			int childDistance = Math.abs(childCenter - mCoveflowCenter);

			float distanceRotate = 2 * (childWidth + mSpacing);
			float distanceShift = 3 * (childWidth + mSpacing);

			float shiftArea = childWidth / 1.6f;

			if (mCoveflowCenter - childCenter > 0) {
				if (childDistance >= distanceRotate && childDistance < distanceShift) {
					Matrix matrix = t.getMatrix();

					float radio = 1.0f * (childDistance - distanceRotate) / ((childWidth + mSpacing));

					matrix.postTranslate(shiftArea * radio, 0);
				} else if (childDistance >= distanceShift) {// simple shift
															// adapt
					Matrix matrix = t.getMatrix();
					matrix.postTranslate(shiftArea, 0);
				}
			} else {
				if (childDistance >= distanceRotate && childDistance <= distanceShift) {
					Matrix matrix = t.getMatrix();
					float radio = 1.0f * (childDistance - distanceRotate) / ((childWidth + mSpacing));

					matrix.postTranslate(-shiftArea * radio, 0);
				} else if (childDistance >= distanceShift) {// simple shift
															// adapt
					Matrix matrix = t.getMatrix();
					matrix.postTranslate(-shiftArea, 0);
				}
			}
		}
	}

	private void transformChidMatrixMidItemCount5(View child, Transformation t) {
		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();
		int rotationAngle = 0;
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		int notTransformLen = 2 * (childWidth + mSpacing);

		if (Math.abs(childCenter - mCoveflowCenter) <= notTransformLen) {
			transformImageBitmap(child, t, 0);
		} else {
			if (DEBUG)
				Log.d(TAG, "childCenter - mCoveflowCenter = " + (childCenter - mCoveflowCenter) + ", 2*(childWidth + mSpacing) = "
						+ (2 * (childWidth + mSpacing)));

			if (mCoveflowCenter - childCenter > 0) {
				// left
				rotationAngle = (int) ((float) (mCoveflowCenter - childCenter - notTransformLen) / childWidth * mMaxRotationAngle / 2);
			} else {
				// right
				rotationAngle = (int) ((float) (mCoveflowCenter - childCenter + notTransformLen) / childWidth * mMaxRotationAngle / 2);
			}
			if (Math.abs(rotationAngle) > mMaxRotationAngle) {
				rotationAngle = rotationAngle < 0 ? -mMaxRotationAngle : mMaxRotationAngle;
			}
			transformImageBitmap(child, t, rotationAngle);

			int childDistance = Math.abs(childCenter - mCoveflowCenter);

			float distanceRotate = 3 * (childWidth + mSpacing);
			float distanceShift = 4 * (childWidth + mSpacing);

			float shiftArea = childWidth / 1.6f;

			if (mCoveflowCenter - childCenter > 0) {
				if (childDistance >= distanceRotate && childDistance < distanceShift) {
					Matrix matrix = t.getMatrix();

					float radio = 1.0f * (childDistance - distanceRotate) / ((childWidth + mSpacing));

					matrix.postTranslate(shiftArea * radio, 0);
				} else if (childDistance >= distanceShift) {// simple shift
															// adapt
					Matrix matrix = t.getMatrix();
					matrix.postTranslate(shiftArea, 0);
				}
			} else {
				if (childDistance >= distanceRotate && childDistance <= distanceShift) {
					Matrix matrix = t.getMatrix();
					float radio = 1.0f * (childDistance - distanceRotate) / ((childWidth + mSpacing));

					matrix.postTranslate(-shiftArea * radio, 0);
				} else if (childDistance >= distanceShift) {// simple shift
															// adapt
					Matrix matrix = t.getMatrix();
					matrix.postTranslate(-shiftArea, 0);
				}
			}
		}
	}

	private void transformChidMatrix(View child, Transformation t) {
		if (mMidItemCount == 5) {
			transformChidMatrixMidItemCount5(child, t);
		} else if (mMidItemCount == 3) {
			transformChidMatrixMidItemCount3(child, t);
		} else {
			transformChidMatrixMidItemCount1(child, t);
		}
	}
}
