package com.yunos.tv.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.yunos.tv.aliTvSdk.R;

import java.util.ArrayList;

public class SnakeProgressBar extends View{
	
	static final String TAG = "SnakeProgressBar";

	private static final int HOLD = 13;
	private static final int STEP = 15;  //15frame
	private static final int PAUSE = 3; //3frame
	private static final int DELAY = 9; //9frame
	private static final int PERIOD = (STEP + PAUSE)*4;
	
	private static final int MAX_SIZE = 36; //dp;
	private static final int DEFAULT_ALPHA = 38;
	
	private int mMaxSize = MAX_SIZE;
	private long mCurrentFrame = 0;
	private ArrayList<Square> mSquareList = new ArrayList<Square>();
	private Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
	
	private Rect mRectBg = new Rect();
	private Paint mPaintBg = new Paint();

	private int mAlpha = DEFAULT_ALPHA;
	
	int mCenterX;
	int mCenterY;
	boolean mIsIndeterminate = false;
	
	private static boolean ENABLE_BG = false;
	
	public SnakeProgressBar(Context context) {
		super(context, null);
		init(context, null, 0);
	}

	public SnakeProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs); 
		init(context, attrs, 0);
	}

	public SnakeProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}
	
	private void init(Context context, AttributeSet attrs, int defStyle) {
		if(attrs != null){
			try {
				TypedArray types = this.getContext().obtainStyledAttributes(attrs, R.styleable.ProgressBarAttr);
				mAlpha = types.getInteger(R.styleable.ProgressBarAttr_alpha, DEFAULT_ALPHA);
				types.recycle();
			} catch (Exception e) {
				Log.w(TAG, Log.getStackTraceString(e));
			}
		}
		
        mMaxSize = (int) (context.getResources().getDisplayMetrics().density*MAX_SIZE);
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		initSquares(getMeasuredHeight(), getMeasuredWidth());
	}

	public void setIndeterminate(boolean indeterminate) {
		mIsIndeterminate = indeterminate;
	}

	@Override
	public void setVisibility(int visibility) {
		int oldVisibility = getVisibility();
		super.setVisibility(visibility);
		if (oldVisibility != visibility) {
			reset();
		}
	}

	public void reset() {
		mCurrentFrame = 0;
		if (mSquareList != null) {
			for (int i = 0; i < mSquareList.size(); i++) {
				mSquareList.get(i).reset();
			}
		}
	}
	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (mIsIndeterminate) {
			if (visibility == GONE || visibility == INVISIBLE) {
				reset();
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mCurrentFrame++;
		
		if (ENABLE_BG) {
			canvas.save(); 
			canvas.drawRect(mRectBg, mPaintBg);
			canvas.restore();
		}

		for (int i = 0; i < mSquareList.size(); i++) {
			mSquareList.get(i).draw(canvas);
		}
		
		if (visiable()) {
			postInvalidate();
		}
	}
	
	private boolean visiable() {
		return getVisibility() == View.VISIBLE;
	}
    private void initSquares(int width, int height) {
		mSquareList.clear();
		Log.d(TAG, "initSquares width=" + width + " height=" + height + " mMaxSize=" + mMaxSize);		
		
		int squareSize = width;
		if (width != height) {
			squareSize = Math.min(width, height);
		}
		
		int size = squareSize/2;
		if(size > mMaxSize) {
			size = mMaxSize;
		}
		
		mCenterX = width/2;
		mCenterY = height/2;
		
		for (int i = 0; i < 4; i++) {
			Square s = new Square(i, size);
			mSquareList.add(s);
		}
		
		if (ENABLE_BG) {
			mRectBg.left = mCenterX - size;
			mRectBg.right = mCenterY + size;
			mRectBg.top = mCenterX - size;
			mRectBg.bottom = mCenterY + size;
			mPaintBg.setColor(Color.WHITE);
			mPaintBg.setAlpha(mAlpha);
		}
	}
    
	class Square {
		int index;
		int size;
		int delay;
		Rect bound;
		Paint paint;
		int left, top;
		public Square(int i, int s) {
			index = i;
			size = s;
			paint = new Paint();
			bound = new Rect();
			
			reset();
		}
		
		public void reset() {
			paint.setColor(Color.WHITE);
			if (index == 0) {
				delay = 3*DELAY;
				paint.setAlpha(191);
				left = mCenterX;
				top = mCenterY;
			} else if (index == 1) {
				delay = 2*DELAY;
				paint.setAlpha(143);
				left = mCenterX;
				top = mCenterY - size;
			} else if (index == 2) {
				delay = DELAY;
				paint.setAlpha(48);
				left = mCenterX - size;
				top = mCenterY - size;
			} else if(index == 3) {
				delay = 0;
				paint.setAlpha(24);
				left = mCenterX - size;
				top = mCenterY;
			}
			
			bound.set(left, top, left + size, top +size);
		}

		private void caculateNewPos(int d) {
			int l = 0, t = 0;
			d += (4 - index)*size;
			d = d % (4*size);
			if (d >= 0 && d < size) {
				l = -d;
				t = 0;
			} else if (d >= size && d < 2*size) {
				l = -size;
				t = size - d;
			} else if(d >= 2*size && d < 3*size) {
				l = d - 3*size;
				t = -size;
			} else if(d >= 3*size && d <= 4*size) {
				l = 0;
				t = d - 4*size;
			}
			
			left = l + mCenterX;
			top = t + mCenterY;
			
			bound.set(left, top, left + size, top + size);
//			Log.d(TAG, "bound=" + bound);
		}
		
		private int caculateDistance(long t) {
			int d = 0;
			float coef = 0;
			
			if (t <= DELAY*3) {
				t = t - delay;
				if (t > 0) {
					int n = (int) (t /STEP);
					int p = (int) (t % STEP);
					d += n*size;
					coef = mInterpolator.getInterpolation(p*1f/STEP);
					d += (int) (size*coef);
					
//					d = t*size/STEP;
				}
			} else {
				t -= 3*DELAY;
				
				if (index == 1) {
					t += DELAY; 
				} else if(index == 2) {
					t += (2*DELAY + PAUSE);
				}
				
				t = t%PERIOD;
				
				int n = (int) (t /(STEP + PAUSE));
				int p = (int) (t % (STEP + PAUSE));
				d += n*size;
				
				if (p >=0 && p < STEP) {
					coef = mInterpolator.getInterpolation(p*1f/STEP);
					d += (int) (size*coef);
				} else if(p >= STEP && p <= STEP + PAUSE) {
					d += size;
				}
			}
			
			return d;
		}
		
		public void draw(Canvas canvas) {
			canvas.save();
			if (mCurrentFrame < HOLD) {
				canvas.drawRect(bound, paint);
			} else {
				long frame = mCurrentFrame - HOLD;
				if (frame <= DELAY*3) {
					int d = caculateDistance(frame);
					caculateNewPos(d);
					if (index == 3) {
						if(frame < STEP) {
							canvas.drawRect(bound, paint);
						}
					} else {
						canvas.drawRect(bound, paint);
					}
				} else {
					if(index != 3) {
						int d = caculateDistance(frame);
						caculateNewPos(d);
						canvas.drawRect(bound, paint);
					}
				}
			}

			canvas.restore();
		}
	}
}
