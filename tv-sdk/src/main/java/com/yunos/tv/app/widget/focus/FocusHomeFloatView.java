package com.yunos.tv.app.widget.focus;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yunos.tv.aliTvSdk.R;
import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.Interpolator.Circ;
import com.yunos.tv.app.widget.Interpolator.TimeInterpolatorHelper;
import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.app.widget.utils.ImageUtils;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FocusHomeFloatView extends FrameLayout implements FocusListener, ItemListener {
    private String TAG = "FocusHomeFloatView";

    protected Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());
    private static final ScaleAlign[] sScaleAlignArray = { ScaleAlign.BOTTOM_CENTER, ScaleAlign.BOTTOM_LEFT, ScaleAlign.BOTTOM_RIGHT };

    protected ScaleAlign mScaleAlign = ScaleAlign.BOTTOM_CENTER;

    protected ScaleImageView mFloatImageView;
    protected ImageView mBackImageView;

    private int mTopspace = 0; // 顶部空出的距离
    protected float mMaxScale = 1.0f; // 最大可以放到值
    private boolean isScale = false;

    private int[] mPosition = new int[2];
    private int mDuraction = 300;
    protected LayoutParams mBackLayoutParams;
    protected LayoutParams mFloatLayoutParams;

    private TweenInterpolator mTweenInterpolator;
    private TimeInterpolatorHelper mTimeInterpolatorHelper;

    private int mFloatMarginLeft = 0;
    private int mFloatMarginTop = 0;
    private int mFloatMarginRight = 0;
    private int mFloatMarginBottom = 0;

    //标记外面是否在滚动中
    private boolean mIsFling = false;

    private boolean mIsForceStop = false;

    public FocusHomeFloatView(Context context) {
        super(context);
        init(context, null);
    }

    public FocusHomeFloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusHomeFloatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mBackImageView = new ImageView(context);
        mFloatImageView = new ScaleImageView(context);

        TypedArray a = getResources().obtainAttributes(attrs, R.styleable.FocusFloatView);

        this.mTopspace = a.getDimensionPixelSize(R.styleable.FocusFloatView_topSpace, 0);
        int scaleType = a.getInt(R.styleable.FocusFloatView_scaleAlign, 0);
        if (scaleType >= 0 && scaleType < sScaleAlignArray.length) {
            mScaleAlign = sScaleAlignArray[scaleType];
        }

        mDuraction = a.getInteger(R.styleable.FocusFloatView_animDuraction, mDuraction);

        int floatWidth = a.getDimensionPixelSize(R.styleable.FocusFloatView_floatWidth, 0);
        int floatHeight = a.getDimensionPixelSize(R.styleable.FocusFloatView_floatHeight, 0);

        int floatPaddingLeft = a.getDimensionPixelSize(R.styleable.FocusFloatView_floatPaddingLeft, 0);
        int floatPaddingTop = a.getDimensionPixelSize(R.styleable.FocusFloatView_floatPaddingTop, 0);
        int floatPaddingRight = a.getDimensionPixelSize(R.styleable.FocusFloatView_floatPaddingRight, 0);
        int floatPaddingBottom = a.getDimensionPixelSize(R.styleable.FocusFloatView_floatPaddingBottom, 0);

        mFloatMarginLeft = a.getDimensionPixelSize(R.styleable.FocusFloatView_floatMarginLeft, 0);
        mFloatMarginTop = a.getDimensionPixelSize(R.styleable.FocusFloatView_floatMarginTop, 0);
        mFloatMarginRight = a.getDimensionPixelSize(R.styleable.FocusFloatView_floatMarginRight, 0);
        mFloatMarginBottom = a.getDimensionPixelSize(R.styleable.FocusFloatView_floatMarginBottom, 0);

        mMaxScale = (float) floatHeight / (float) (floatHeight - floatPaddingTop);

        mBackImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mBackLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mBackLayoutParams.topMargin = mTopspace;

        mFloatImageView.setPadding(floatPaddingLeft, floatPaddingTop, floatPaddingRight, floatPaddingBottom);
        mFloatLayoutParams = new LayoutParams(floatWidth, floatHeight);
        mFloatLayoutParams.setMargins(mFloatMarginLeft, mFloatMarginTop, mFloatMarginRight, mFloatMarginBottom);

        a.recycle();

        // 设置加速器
        mTweenInterpolator = new Circ.easeInOut();
        mTimeInterpolatorHelper = new TimeInterpolatorHelper(mTweenInterpolator);

        mTweenInterpolator.setDuration(mDuraction);
        mTweenInterpolator.setStartAndTarget(0f, 1.0f);

        this.addViewInLayout(mBackImageView, 0, mBackLayoutParams);
        this.addViewInLayout(mFloatImageView, 1, mFloatLayoutParams);
    }

    public void setBackgroundBitmap(Bitmap backBitmap, Bitmap beforeBitmap) {// 设置图片
        if (mBackImageView != null && backBitmap != null) {
            mBackImageView.setImageBitmap(ImageUtils.getScaleBitmap(backBitmap, getLayoutParams().width,
                    (getLayoutParams().height - mTopspace)));
        }
        if (mFloatImageView != null && beforeBitmap != null) {
            mFloatImageView.setImageBitmap(beforeBitmap);
            mFloatImageView.invalidate();
        }
        invalidate();
    }

    @Override
    public void dispatchSetSelected(boolean selected) {
//        Log.d(TAG, "dispatchSetSelected:" + selected);
        super.dispatchSetSelected(selected);
        if (selected) {
            start();
        } else {
            //收起时需要将fling强制置为false,否则isFinished一直为false.
            //因为只要reverse结束后就交给控件用原生的方法画浮层了
            mIsFling = false;
            reverse();
        }
    }

    public void forceStop(boolean isStop){
        mIsForceStop = isStop;
        if(isStop){
            invalidate();
        }
    }
    
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (child == mFloatImageView && (isSelected() || !isFinished()) && !mIsForceStop) {
            return true;
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    public void start() {
        mTimeInterpolatorHelper.start();
    }

    public void reverse() {
        mTimeInterpolatorHelper.reverse();
    }
    
    @Override
    public void drawAfterFocus(Canvas canvas) {
//        Log.d(TAG, "drawAfterFocus");
        //判断是否需要绘制浮动层
        if((isSelected() || !isFinished()) && !mIsForceStop){
            canvas.restore();
            canvas.save();
            getLocationInWindow(mPosition);
            canvas.translate(mPosition[0] + mFloatMarginLeft, mPosition[1] + mFloatMarginTop);
            canvas.restore();
    
            float current = mTimeInterpolatorHelper.getStatusTarget();
            if (mTimeInterpolatorHelper.track()) {
                current = mTimeInterpolatorHelper.getCurrent();
                float scale = (mMaxScale - 1) * current + 1;
                mFloatImageView.setScale(scale);
            }
    
            mFloatImageView.draw(canvas);
        }
        
        //Log.d(TAG, "drawAfterFocus1:" + isSelected() + ",state:" + mTimeInterpolatorHelper.getStatus() + ",isFinished:" + isFinished() + ",mIsFling:" + mIsFling);
        // reverse最后一帧直接画默认的内容
        if (!isSelected() && isFinished()) {
            invalidate();
        }
    }

    @Override
    public void drawBeforeFocus(Canvas canvas) {
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        mBackImageView.getFocusedRect(r);

        offsetDescendantRectToMyCoords(mBackImageView, r);
        return new FocusRectParams(r, 0.5f, 0.5f);
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        return getHeight() - this.getPaddingTop();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public boolean isScale() {
        return isScale;
    }

    @Override
    public boolean canDraw() {
        return true;
    }

    @Override
    public ItemListener getItem() {
        return this;
    }

    @Override
    public Params getParams() {
        return mParams;
    }

    @Override
    public boolean isAnimate() {
        return true;
    }

    @Override
    public boolean isFocusBackground() {
        return false;
    }

    @Override
    public boolean isScrolling() {
        return false;
    }

    @Override
    public void onFocusFinished() {
        
    }

    @Override
    public void onFocusStart() {
        
    }
    
    public void setFling(boolean fling){
        Log.d(TAG,"setFling:" + fling);
        //只有选中时候设置fling才有效,没选中的时候不会有start状态
        if(isSelected()){
            mIsFling = fling;
        }
    }
    
    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent arg1) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_DPAD_CENTER:
        case KeyEvent.KEYCODE_ENTER:
            return true;

        default:
            break;
        }
        return false;
    }

    @Override
    public boolean isFinished() {
//        Log.d(TAG, "isFinished:" + (!isSelected() && !mTimeInterpolatorHelper.isRunning()) + ",isSelected:" + isSelected() + ",isRunning:"
//                + mTimeInterpolatorHelper.isRunning() + ",isactivated:" + isActivated() + ",isShown():" + isShown() + ",hasFocus:" + hasFocus());
        return !mIsFling && !mTimeInterpolatorHelper.isRunning();
    }

    protected class ScaleImageView extends ImageView {
        private Rect mScaleRect = new Rect();
        private float mScale = 1.0f;

        public ScaleImageView(Context context) {
            super(context);
        }

        public ScaleImageView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onDraw(Canvas canvas) {
//            Log.d(TAG, "onDraw");

            Rect r = mScaleRect;
            // 计算默认的矩形
            int mWidth = getWidth();
            int mHeight = getHeight();
            int height = mHeight - getPaddingTop() - getPaddingBottom() - FocusHomeFloatView.this.getPaddingTop() - FocusHomeFloatView.this.getPaddingBottom(); // 高度为原始高度减去paddingTop的高度
            int width = (int) (mWidth / mMaxScale); // 宽度为原始宽度除以最大放大倍数
            int left = (int) ((mMaxScale - 1.0) * mWidth + getPaddingLeft());
            int top = getPaddingTop();
            if (mScaleAlign == ScaleAlign.BOTTOM_RIGHT) { // 居右
                mScaleRect.set(mWidth - width, top, mWidth, top + height);
            } else if (mScaleAlign == ScaleAlign.BOTTOM_LEFT) { // 居左
                mScaleRect.set(0, top, width, top + height);
            } else { // 居中
                mScaleRect.set(left / 2, top, left / 2 + width, top + height);
            }

            // 缩放
            int detlaX = (int) (width * (mScale - 1));
            int detlaY = (int) (height * (mScale - 1));
//            Log.d(TAG, "detla:(" + detlaX + "," + detlaY + ")");

            r.top -= detlaY;
            if (mScaleAlign == ScaleAlign.BOTTOM_RIGHT) {
                r.left -= detlaX;
            } else if (mScaleAlign == ScaleAlign.BOTTOM_LEFT) {
                r.right += detlaX;
            } else {
                r.left -= detlaX / 2;
                r.right += detlaX / 2;
            }
            Drawable mDrawable = getDrawable();
//            Log.d(TAG, "bounds:" + r + ",isFinish:" + isFinished() + ",mDrawable:" + mDrawable 
//                    + ",origin(" + width +","+ height +"),new(" + width +  "," + height + "),left:" + left);

            if (mDrawable != null) {
                mDrawable.setBounds(r);
                mDrawable.draw(canvas);
            }
        }

        public void setScale(float scale) {
            mScale = scale;
        }
    }

    public enum ScaleAlign {
        BOTTOM_CENTER(0), BOTTOM_LEFT(1), BOTTOM_RIGHT(2);

        ScaleAlign(int type) {
            mType = type;
        }

        final int mType;
    }

	@Override
	public Rect getClipFocusRect() {
		// TODO Auto-generated method stub
		return null;
	}
}