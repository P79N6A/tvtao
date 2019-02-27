package com.yunos.tvtaobao.takeoutbundle.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.yunos.tv.app.widget.FrameLayout;
import com.yunos.tvtaobao.takeoutbundle.R;

import java.lang.ref.WeakReference;

/**
 * Created by haoxiang on 2017/12/29.
 * 圆角viewGroup
 * 用于解决api21 以下 v7 包中 cardView 适配缺陷
 * 设置背景 CardView_backgroundColor
 */

public class CardView extends FrameLayout {
    // todo 用于缓冲 bitmap 对象 减少内存使用
    // private static SparseArray<WeakReference<Bitmap>> sparseArray = new SparseArray<>();

    private Canvas mapCanvas;
    private final Paint mBitmapPaint = new Paint();
    private Paint mBackgroundPaint = new Paint();
    private Bitmap mBitMap;

    private RectF mImageRectF = new RectF();
    private int mRadio = 0;

    public CardView(Context context) {
        this(context, null, 0);

    }

    public CardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardView, defStyle, 0);
        mRadio = a.getDimensionPixelSize(R.styleable.CardView_cv_radius, 0);
        if (mRadio == 0) {
            mRadio = context.getResources().getDimensionPixelOffset(R.dimen.dp_12);
        }

        int background = a.getColor(R.styleable.CardView_backgroundColor, 0xffffffff);
        mBackgroundPaint.setColor(background);
        mBackgroundPaint.setAntiAlias(true);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setColor(0xffffffff);
        a.recycle();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        if (mapCanvas == null) {
            super.dispatchDraw(canvas);
            canvas.restore();
            return;
        }

        mapCanvas.drawRoundRect(mImageRectF, mRadio, mRadio, mBackgroundPaint);
        super.dispatchDraw(mapCanvas);
        canvas.drawRoundRect(mImageRectF, mRadio, mRadio, mBitmapPaint);
        canvas.restore();
    }

    public void setRadio(int radio) {
        this.mRadio = radio;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int width = right - left;
        int height = bottom - top - paddingTop;

        mImageRectF.left = paddingLeft;
        mImageRectF.right = width - paddingRight;
        mImageRectF.top = paddingTop;
        mImageRectF.bottom = height - paddingBottom;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

//        int id = w << 16 + h;
//        if (sparseArray.get(id) == null || sparseArray.get(id) == null || sparseArray.get(id).get() == null) {
//            mBitMap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
//            sparseArray.put(id, new WeakReference<>(mBitMap));
//        }
//
//        if (mBitMap != sparseArray.get(id).get() || mapCanvas == null) {
//            mBitMap = sparseArray.get(id).get();
//            mapCanvas = new Canvas(mBitMap);
//            BitmapShader mBitmapShader = new BitmapShader(mBitMap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//            mBitmapPaint.setAntiAlias(true);
//            mBitmapPaint.setShader(mBitmapShader);
//            mBitmapPaint.setColor(0xffffffff);
//        }


        int id = w << 16 + h;
        if (mBitMap == null || w != mBitMap.getWidth() || h != mBitMap.getHeight()) {
            mBitMap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
//            sparseArray.put(id, new WeakReference<>(mBitMap));
            mapCanvas = new Canvas(mBitMap);
            BitmapShader mBitmapShader = new BitmapShader(mBitMap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mBitmapPaint.setShader(mBitmapShader);
        }

    }

    @Override
    public boolean isInEditMode() {
        return true;
    }
}
