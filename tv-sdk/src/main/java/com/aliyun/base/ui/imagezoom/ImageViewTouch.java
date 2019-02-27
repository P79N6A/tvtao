package com.aliyun.base.ui.imagezoom;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ViewConfiguration;

import com.aliyun.base.info.MobileInfo;
import com.aliyun.base.ui.imagezoom.GestureDetector.OnGestureListener;

public class ImageViewTouch extends ImageViewTouchBase {
	public static final String TAG = "ImageViewTouch";

	protected ScaleGestureDetector mScaleDetector;
	protected GestureDetector mGestureDetector;
	protected int mTouchSlop;
	protected float mCurrentScaleFactor;
//	protected float mScaleFactor;
	protected int mDoubleTapDirection;
	protected OnGestureListener mGestureListener;
	protected OnScaleGestureListener mScaleListener;
	protected boolean mDoubleTapEnabled = true;
	protected boolean mScaleEnabled = true;
	protected boolean mScrollEnabled = true;
	private TouchInterface mTouchInterface;
	
	public void setTouchInterface(TouchInterface touchInterface) {
		mTouchInterface = touchInterface;
	}

	public ImageViewTouch( Context context) {
		super( context);
	}
	
	public ImageViewTouch( Context context, AttributeSet attrs ) {
		super( context, attrs );
	}

	@Override
	protected void init() {
		super.init();
		
		mTouchSlop = ViewConfiguration.getTouchSlop();
		mGestureListener = getGestureListener();
		mScaleListener = getScaleListener();

		mScaleDetector = new ScaleGestureDetector( getContext(), mScaleListener );
		mGestureDetector = new GestureDetector( getContext(), mGestureListener, null, true );
		mGestureDetector.setIsLongpressEnabled(false);//by leiming

		mCurrentScaleFactor = 1f;
		mDoubleTapDirection = 1;
		
	}

	public void setDoubleTapEnabled( boolean value ) {
		mDoubleTapEnabled = value;
	}

	public void setScaleEnabled( boolean value ) {
		mScaleEnabled = value;
	}

	public void setScrollEnabled( boolean value ) {
		mScrollEnabled = value;
	}

	public boolean getDoubleTapEnabled() {
		return mDoubleTapEnabled;
	}

	protected OnGestureListener getGestureListener() {
		return new GestureListener();
	}

	protected OnScaleGestureListener getScaleListener() {
		return new ScaleListener();
	}

	@Override
	protected void onBitmapChanged( Drawable drawable ) {
		super.onBitmapChanged( drawable );

		float v[] = new float[9];
		mSuppMatrix.getValues( v );
		mCurrentScaleFactor = v[Matrix.MSCALE_X];
	}

	@Override
	protected void _setImageDrawable( final Drawable drawable, final boolean reset, final Matrix initial_matrix, final float maxZoom ) {
		super._setImageDrawable( drawable, reset, initial_matrix, maxZoom );
//		mScaleFactor = getMaxZoom() / 3;
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		mScaleDetector.onTouchEvent( event );
		int action = event.getAction();
		boolean b_scroll = true;
		if ( !mScaleDetector.isInProgress() ) { // 正在两点进行缩放
			b_scroll = mGestureDetector.onTouchEvent( event );
		}
		
		int count = event.getPointerCount();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
//			b_scroll = true;
			Log.i(TAG, b_scroll + "----onTouchEvent-----pointer down" + count);
			break;
		case MotionEvent.ACTION_POINTER_UP:
//			b_scroll = true;
			Log.i(TAG, b_scroll + "----onTouchEvent-----pointer up" + count);
			break;
		case MotionEvent.ACTION_DOWN:
//			b_scroll = true;
			Log.i(TAG, b_scroll + "--touch--onTouchEvent-----down" + count);
			break;
		case MotionEvent.ACTION_UP:
//			b_scroll = true;
			if ( (int) (getScale() * 10000) < (int) (mFitScreenScale * 10000)  ) {
				zoomTo( mFitScreenScale, 50 );
			}
			Log.i(TAG, b_scroll + "----onTouchEvent-----up" + count);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mTouchInterface != null) {
				mTouchInterface.setTouch(!b_scroll);
			}
			if (b_scroll) {
				Log.i(TAG, b_scroll + "----onTouchEvent------move" + count);
			} else {
				Log.d(TAG, b_scroll + "----onTouchEvent------move" + count);
			}
			break;
		}
		
		return true;
	}

	@Override
	protected void onZoom( float scale ) {
		super.onZoom( scale );
		if ( !mScaleDetector.isInProgress() ) mCurrentScaleFactor = scale;
	}
	
	public void fitToScreen() {
		float scale = getScale();
		if ((int)(scale * 10000) != (int)(mFitScreenScale * 10000)) {//当前是适屏状态
			zoomTo( mFitScreenScale, MobileInfo.getScreenWidthPx(getContext())/2, MobileInfo.getScreenHeightPx(getContext())/2, 200 );
			invalidate();
		}
	}

	protected float onDoubleTapPost( float scale, float maxZoom ) {
		if ( mDoubleTapDirection == 1 ) { //放大
//			if ( ( scale + ( mScaleFactor * 2 ) ) <= maxZoom ) { // 分两次放大
//				return scale + mScaleFactor;
//			} else {
//				mDoubleTapDirection = -1;
//				return maxZoom;
//			}
			mDoubleTapDirection = -1;
			return maxZoom;
		} else {
			mDoubleTapDirection = 1;//下一次放大
			return mFitScreenScale;
		}
	}
	
	public class GestureListener extends GestureDetector.SimpleOnGestureListener {
		
//		@Override
//		public boolean onSingleTapConfirmed(MotionEvent e) {
//			Log.i("aabb", "---------onSingleTapConfirmed---------------");
//			return super.onSingleTapConfirmed(e);
//		}
//		
//		@Override
//		public boolean onSingleTapUp(MotionEvent e) {
//			Log.i("aabb", "-------------onSingleTapUp-----------");
//			return super.onSingleTapUp(e);
//		}
		
		@Override
		public boolean onDoubleTap( MotionEvent e ) {
//			Log.i( TAG, "onDoubleTap. double tap enabled? " + mDoubleTapEnabled );
			if ( mDoubleTapEnabled ) {
				float scale = getScale();
				float targetScale = scale;
				targetScale = onDoubleTapPost( scale, getMaxZoom() );
				targetScale = Math.min( mMaxZoom, Math.max( targetScale, mMinZoom ) );
				mCurrentScaleFactor = targetScale;
//				zoomTo( targetScale, e.getX(), e.getY(), 200 );
				zoomTo( targetScale, MobileInfo.getScreenWidthPx(getContext())/2, MobileInfo.getScreenHeightPx(getContext())/2, 200 );
				invalidate();
			}
			return super.onDoubleTap( e );
		}

		@Override
		public void onLongPress( MotionEvent e ) {
			if ( isLongClickable() ) {
//				Log.d(TAG, "----------------onLongPress-----------------------	");
				if ( !mScaleDetector.isInProgress() ) {
					setPressed( true );
					performLongClick();
				}
			}
		}

		@Override
		public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY ) {
			if ( !mScrollEnabled ) {
//				Log.d(TAG, "-----1----GestureListener-------onScroll-------false");
				return false;
			}

			if ( e1 == null || e2 == null ) {
//				Log.d(TAG, "------2---GestureListener-------onScroll-------false");
				return false;
			}
			if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 ) {
//				Log.d(TAG, "-----3----GestureListener-------onScroll-------false");
				return false;
			}
			if ( mScaleDetector.isInProgress() ) {
//				Log.d(TAG, "-----4----GestureListener-------onScroll-------false");
				return false;
			}
			if ( getScale() == 1f ) {
//				Log.d(TAG, "----5-----GestureListener-------onScroll-------false");
				return false;
			}
			
			boolean b = scrollBy( -distanceX, -distanceY );
			invalidate();
			Log.d(TAG, "----6-----GestureListener-------onScroll------====" + b + ",distanceX=" + distanceX);
			return b;
		}

		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY ) {
			if ( !mScrollEnabled ) return false;

			if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 ) return false;
			if ( mScaleDetector.isInProgress() ) return false;

			float diffX = e2.getX() - e1.getX();
			float diffY = e2.getY() - e1.getY();

			if ( Math.abs( velocityX ) > 800 || Math.abs( velocityY ) > 800 ) {
				scrollBy( diffX / 2, diffY / 2, 300 );
				invalidate();
			}
			return super.onFling( e1, e2, velocityX, velocityY );
		}
	}

	public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

		@SuppressWarnings("unused")
		@Override
		public boolean onScale( ScaleGestureDetector detector ) {
//			float span = detector.getCurrentSpan() - detector.getPreviousSpan();
			float targetScale = mCurrentScaleFactor * detector.getScaleFactor();
			if ( mScaleEnabled ) {
				targetScale = Math.min( getMaxZoom(), Math.max( targetScale, mMinZoom ) );
				zoomTo( targetScale, detector.getFocusX(), detector.getFocusY() );
				//MobileInfo.getScreenWidthPx() / 2, MobileInfo.getScreenHeightPx() / 2
				mCurrentScaleFactor = Math.min( getMaxZoom(), Math.max( targetScale, mMinZoom ) );
				if ((int)(targetScale * 10000) == (int)(mFitScreenScale * 10000)) {//当前是适屏状态
					mDoubleTapDirection = 1;
				} else {
					mDoubleTapDirection = -1;
				}
				invalidate();
				return true;
			}
			return false;
		}
	}
}
