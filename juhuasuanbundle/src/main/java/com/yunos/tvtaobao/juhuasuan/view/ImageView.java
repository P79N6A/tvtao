package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.yunos.tv.core.common.AppDebug;

/**
 * 增加Bitmap被回收的判断
 * @author hanqi
 */
public class ImageView extends android.widget.ImageView {

    private Bitmap mBitmap;

    public ImageView(Context context) {
        super(context);
    }

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @deprecated use #setImageDrawable(Drawable, Bitmap) instead
     */
    @Deprecated
    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    public void setImageDrawable(Drawable drawable, Bitmap bm) {
        mBitmap = bm;
        setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mBitmap = bm;
        super.setImageBitmap(bm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null != mBitmap && mBitmap.isRecycled()) {
            AppDebug.i("ImageView", "ImageView.onDraw mBitmap is isRecycled!mBitmap=" + mBitmap);
            return;
        }
        super.onDraw(canvas);
    }

    public void release() {
        if (null != mBitmap) {
            AppDebug.i("ImageView", "ImageView.release mBitmap is recycleing!mBitmap=" + mBitmap);
            mBitmap.recycle();
        }
    }
}
