/*
 * Copyright 2013 Alibaba Group.
 */
package com.yunos.tvlife.app.widget;

import android.R.color;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.widget.Adapter;

/**
 * 提供给{@link AbsCoverFlow}使用的适配器, 带阴影的图片适配器
 * <p>实现比较简单，只适用于资源数量较少的场景；</p>
 * <p>资源较多的场景建议使用自己的{@link Adapter},做好缓存处理，避免oom；</p>
 */
public class ReflectingImageAdapter extends AbsCoverFlowAdapter {

    /** The linked adapter. */
    private final AbsCoverFlowAdapter linkedAdapter;
    /**
     * Gap between the image and its reflection.
     */
    private float reflectionGap;

    /** The image reflection ratio. */
    private float imageReflectionRatio;

    /**
     *  设置Item的宽度比.
     * @param imageReflectionRatio
     *            Item的宽度比
     */
    public void setWidthRatio(final float imageReflectionRatio) {
        this.imageReflectionRatio = imageReflectionRatio;
    }

    /**
     * 将非阴影的适配器转换为带阴影的适配器.
     * 
     * @param linkedAdapter
     *            创建带阴影的适配器
     */
    public ReflectingImageAdapter(final AbsCoverFlowAdapter linkedAdapter) {
        super();
        this.linkedAdapter = linkedAdapter;
    }

    /**
     * 设置Item和阴影区域的间隔.
     * 
     * @param reflectionGap
     *            Item和阴影区域的间隔
     */
    public void setReflectionGap(final float reflectionGap) {
        this.reflectionGap = reflectionGap;
    }

    /**
     * 返回Item和阴影区域的间隔.
     * 
     * @return Item和阴影区域的间隔.
     */
    public float getReflectionGap() {
        return reflectionGap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pl.polidea.coverflow.AbstractCoverFlowImageAdapter#createBitmap(int)
     */
    @Override
    protected Bitmap createBitmap(final int position) {
        return createReflectedImages(linkedAdapter.getItem(position));
    }
    
    

    /**
     * 创建带阴影的Bitmap.
     * 
     * @param originalImage
     *            原始图片的bitmap
     * @return true
     */
    public Bitmap createReflectedImages(final Bitmap originalImage) {
        final int width = originalImage.getWidth();
        final int height = originalImage.getHeight();
        final Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        final Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, (int) (height * imageReflectionRatio),
                width, (int) (height - height * imageReflectionRatio), matrix, false);
        final Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (int) (height + height * imageReflectionRatio),
                Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(originalImage, 0, 0, null);
        final Paint deafaultPaint = new Paint();
        deafaultPaint.setColor(color.transparent);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        final Paint paint = new Paint();
        final LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return linkedAdapter.getCount();
    }

}
