package com.tvlife.imageloader.core.display.drawable;


import com.tvlife.imageloader.core.assist.DrawableScaleType;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * 此显示类主要是针对淘宝的商品特殊定制：
 * 当 mScale 为 false 时：
 * 1： 如果图片的大小[包括 宽 以及高]比View 还大，那么会在图片的中间部分取出一块显示在View上； 并且根据 cornerRadiusPixels 来处理圆角
 * 根据 marginPixels 来处理 图片与View之间的间距
 * 2：如果图片的大小[包括 宽 以及高]都比 VIew小，则不处理圆角，直接在View的中间显示图片 ;
 * 同样， 根据 marginPixels 来处理 图片与View之间的间距
 * 3: 如果图片的大小[宽或者高其中一个]比 View的小 [宽或者高其中一个] 则直接获取图片的中间部分显示在VIEW中，
 * 同样， 根据 marginPixels 来处理 图片与View之间的间距
 * @author yunzhong.qyz
 */

public class RoundedAndReviseSizeDrawable extends Drawable {

    private String TAG = "RoundedAndReviseSizeDrawable";

    protected float cornerRadius;
    protected int margin;

    protected RectF mDisplayRect = new RectF();
    protected RectF mBitmapOriginalRect;
    protected RectF mBitmapClipRect;
    protected RectF mBoundsRect;

    protected BitmapShader bitmapShader;
    protected Paint paint;

    private boolean mClipBitmap_width = true;
    private boolean mClipBitmap_height = true;

    private boolean mLoggingEnabled = false;

    // 外部会进行释放
    private Bitmap mBitmap = null; 

    // 缩放类型
    private DrawableScaleType mDrawableScaleType;

    /***
     * @param bitmap
     * @param cornerRadius
     * @param margin
     * @param loggingEnabled
     */
    public RoundedAndReviseSizeDrawable(Bitmap bitmap, int cornerRadius, int margin, boolean loggingEnabled) {
        this(bitmap, cornerRadius, margin, false, loggingEnabled);
    }

    /**
     * @param bitmap BITMAP 图片
     * @param cornerRadius 圆角值
     * @param margin 留出的边框
     * @param scale 缩放值， 默认为 false: 根据View的大小来进行裁剪图片
     *            true: 只是进行缩放处理，不进行任何的裁剪
     * @param loggingEnabled 是否打印LOG
     */

    public RoundedAndReviseSizeDrawable(Bitmap bitmap, int cornerRadius, int margin, boolean scale,
            boolean loggingEnabled) {
        DrawableScaleType drawableScaleType;
        if (scale) {
            drawableScaleType = DrawableScaleType.FIT_XY;
        } else {
            drawableScaleType = DrawableScaleType.NONE;
        }
        initRoundedAndReviseSizeDrawable(bitmap, cornerRadius, margin, drawableScaleType, loggingEnabled);
    }

    /**
     * 根据缩放类型构造
     * @param bitmap
     * @param cornerRadius
     * @param margin
     * @param drawableScaleType
     * @param loggingEnabled
     */
    public RoundedAndReviseSizeDrawable(Bitmap bitmap, int cornerRadius, int margin,
            DrawableScaleType drawableScaleType, boolean loggingEnabled) {
        initRoundedAndReviseSizeDrawable(bitmap, cornerRadius, margin, drawableScaleType, loggingEnabled);
    }

    /**
     * @param bitmap
     * @param cornerRadius
     * @param margin
     * @param drawableScaleType
     * @param loggingEnabled
     */
    private void initRoundedAndReviseSizeDrawable(Bitmap bitmap, int cornerRadius, int margin,
            DrawableScaleType drawableScaleType, boolean loggingEnabled) {
        this.cornerRadius = cornerRadius;
        this.margin = margin;
        this.mDrawableScaleType = drawableScaleType;
        this.mLoggingEnabled = loggingEnabled;

        mBitmap = bitmap;

        // 由原图生成的纹理
        bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        // 原图区域
        mBitmapOriginalRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());

        // 裁剪区域
        mBitmapClipRect = new RectF();

        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        mBoundsRect = new RectF(bounds);

        if (mLoggingEnabled) {
            Log.i(TAG, "onBoundsChange  --->  bounds = " + bounds + ";   mBitmapOriginalRect = " + mBitmapOriginalRect
                    + "; margin = " + margin);
        }
        // 计算图片显示区域
        mDisplayRect.setEmpty();
        mBitmapClipRect.setEmpty();

        mClipBitmap_width = true;
        mClipBitmap_height = true;

        if (bounds != null) {

            /**
             * 计算宽
             */
            float visible_bounds_width = bounds.width() - 2 * margin;

            float visible_bounds_width_Temp = visible_bounds_width;

            // 减去圆角区域
            if (margin == 0) {
                visible_bounds_width_Temp -= cornerRadius;
            }

            // 如果是宽相等，但需要作圆角
            if (visible_bounds_width_Temp == mBitmapOriginalRect.width() && (cornerRadius != 0)) {
                visible_bounds_width_Temp--;
            }

            if (visible_bounds_width_Temp < mBitmapOriginalRect.width()) {

                // 显示区域的宽度 比 原图 还小，那么计算  X 方向裁剪的起始点
                float width_dissimilarity_2 = (mBitmapOriginalRect.width() - visible_bounds_width) / 2;

                mBitmapClipRect.left = width_dissimilarity_2;
                mBitmapClipRect.right = mBitmapClipRect.left + visible_bounds_width;

                mDisplayRect.left = 0;
                mDisplayRect.right = mDisplayRect.left + visible_bounds_width;

            } else {

                // 否则不需要裁剪[针对宽]
                mClipBitmap_width = false;

                mBitmapClipRect.left = 0;
                mBitmapClipRect.right = mBitmapOriginalRect.width();

                mDisplayRect.left = (visible_bounds_width - mBitmapOriginalRect.width()) / 2;
                mDisplayRect.right = mDisplayRect.left + mBitmapOriginalRect.width();
            }

            /**
             * 计算高
             */

            float visible_bounds_height = bounds.height() - 2 * margin;

            float visible_bounds_height_Temp = visible_bounds_height;

            // 减去圆角区域
            if (margin == 0) {
                visible_bounds_height_Temp -= cornerRadius;
            }

            // 如果是高相等，但需要作圆角
            if (visible_bounds_height_Temp == mBitmapOriginalRect.height() && (cornerRadius != 0)) {
                visible_bounds_height_Temp--;
            }

            if (visible_bounds_height_Temp < mBitmapOriginalRect.height()) {

                // 显示区域的高度 比 原图 还小，那么计算  Y 方向裁剪的起始点
                float height_dissimilarity_2 = (mBitmapOriginalRect.height() - visible_bounds_height) / 2;

                mBitmapClipRect.top = height_dissimilarity_2;
                mBitmapClipRect.bottom = mBitmapClipRect.top + visible_bounds_height;

                mDisplayRect.top = 0;
                mDisplayRect.bottom = mDisplayRect.top + visible_bounds_height;

            } else {

                // 否则不需要裁剪[针对高]
                mClipBitmap_height = false;

                mBitmapClipRect.top = 0;
                mBitmapClipRect.bottom = mBitmapOriginalRect.height();

                mDisplayRect.top = (visible_bounds_height - mBitmapOriginalRect.height()) / 2;
                mDisplayRect.bottom = mDisplayRect.top + mBitmapOriginalRect.height();
            }
        }

        Matrix shaderMatrix = new Matrix();
        shaderMatrix.reset();

        if (mDrawableScaleType == DrawableScaleType.NONE || mDrawableScaleType == DrawableScaleType.FIT_NONE_ROUND_LT_RT) {
            
            shaderMatrix.setTranslate(-mBitmapClipRect.left, -mBitmapClipRect.top);
            
        } else if (mDrawableScaleType == DrawableScaleType.FIT_XY) {
            //  缩放  
            float sx = (float) mBoundsRect.width() / (float) mBitmapOriginalRect.width();
            float sy = (float) mBoundsRect.height() / (float) mBitmapOriginalRect.height();

            shaderMatrix.setTranslate(0, 0);
            shaderMatrix.setScale(sx, sy);

            //  如果是要进行缩放的，那么当作 裁剪处理
            mClipBitmap_width = true;
            mClipBitmap_height = true;

            if ((mClipBitmap_width) && (mClipBitmap_height)) {
                mDisplayRect.left = 0;
                mDisplayRect.top = 0;
                mDisplayRect.right = mBoundsRect.width();
                mDisplayRect.bottom = mBoundsRect.height();
            }

            if (mLoggingEnabled) {
                Log.i(TAG, "onBoundsChange  --->  mDisplayRect = " + mDisplayRect + "; sx = " + sx + ";  sy = " + sy
                        + "; mBoundsRect = " + mBoundsRect + ";  mBitmapOriginalRect = " + mBitmapOriginalRect);
            }
        } else if (mDrawableScaleType == DrawableScaleType.FIT_SUITABLE) {

            //            float sx = (float) mBoundsRect.width() / (float) mBitmapOriginalRect.width();
            //            float sy = (float) mBoundsRect.height() / (float) mBitmapOriginalRect.height();
            //
            //            float scale = 1.0f; 
            //            
            //            // 放大，取最小值
            //            if ((sx > 1.0f) && (sy > 1.0f)) {
            //                scale = sx < sy ? sx : sy;
            //            }
            //            
            //           // 缩小，取最大值
            //            if ((sx < 1.0f) && (sy < 1.0f)) {
            //                scale = sx > sy ? sx : sy;
            //            }
            //
            //            shaderMatrix.setTranslate(0, 0);
            //            shaderMatrix.setScale(scale, scale);
            //
            //            //  如果是要进行缩放的，那么当作 裁剪处理
            //            mClipBitmap_width = true;
            //            mClipBitmap_height = true;
            //
            //            if ((mClipBitmap_width) && (mClipBitmap_height)) {
            //                mDisplayRect.left = 0;
            //                mDisplayRect.top = 0;
            //                mDisplayRect.right = mBoundsRect.width();
            //                mDisplayRect.bottom = mBoundsRect.height();
            //            }
            //
            //            if (mLoggingEnabled) {
            //                Log.i(TAG, "onBoundsChange  --->  mDisplayRect = " + mDisplayRect + "; sx = " + sx + ";  sy = " + sy
            //                        + "; mBoundsRect = " + mBoundsRect + ";  mBitmapOriginalRect = " + mBitmapOriginalRect);
            //            }
        }

        bitmapShader.setLocalMatrix(shaderMatrix);
        paint.setShader(bitmapShader);

        if (mLoggingEnabled) {
            Log.i(TAG, "onBoundsChange  --->  mDrawableScaleType = " + mDrawableScaleType + ";  mBitmapClipRect = "
                    + mBitmapClipRect + "; mDisplayRect = " + mDisplayRect + ";  mClipBitmap_width = "
                    + mClipBitmap_width + "; mClipBitmap_height = " + mClipBitmap_height);
        }

        // 显示区域 加上 margin 
        mDisplayRect.left += margin;
        mDisplayRect.top += margin;
        mDisplayRect.right -= margin;
        mDisplayRect.bottom -= margin;

    }

    @Override
    public void draw(Canvas canvas) {
        if ((mClipBitmap_width) && (mClipBitmap_height)) {
            //  宽 和高都是裁剪的显示 作圆角处理 
            if (mDrawableScaleType == DrawableScaleType.NONE) { 
                // 如果四个角都作圆角处理
                canvas.drawRoundRect(mDisplayRect, cornerRadius, cornerRadius, paint); 
            } else if(mDrawableScaleType == DrawableScaleType.FIT_NONE_ROUND_LT_RT){ 
                // 只是左上角和右上角作圆角处理
                float[] radii = {cornerRadius, cornerRadius, cornerRadius, cornerRadius, 0, 0, 0, 0};
                Path  path = new Path(); 
                path.addRoundRect(mDisplayRect, radii, Path.Direction.CW);
                canvas.drawPath(path, paint);
             }
        } else if ((!mClipBitmap_width) && (!mClipBitmap_height)) {
            //  宽 和高都没有裁剪的显示， 不作圆角处理
            if ((mBitmap != null) && (!mBitmap.isRecycled())) {
                canvas.drawBitmap(mBitmap, mDisplayRect.left, mDisplayRect.top, null);
            }
        } else if ((!mClipBitmap_width) || (!mClipBitmap_height)) {
            //  宽 与 高 只需裁剪其中一个的显示
            if ((mBitmap != null) && (!mBitmap.isRecycled())) {
                Rect src = new Rect();
                src.left = (int) mBitmapClipRect.left;
                src.top = (int) mBitmapClipRect.top;
                src.right = (int) mBitmapClipRect.right;
                src.bottom = (int) mBitmapClipRect.bottom;
                canvas.drawBitmap(mBitmap, src, mDisplayRect, null);
            }
        }
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    protected void finalize() throws Throwable {

        super.finalize();

        // 回收之前做清理
        if (mLoggingEnabled) {
            Log.i(TAG, "finalize  ---> this = " + this);
        }

        mDisplayRect = null;
        mBitmapOriginalRect = null;
        mBitmapClipRect = null;
        bitmapShader = null;
        paint = null;
        mBitmap = null;
    }

}