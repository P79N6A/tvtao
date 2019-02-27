/**
 * 
 */
package com.tvlife.imageloader.core.display;


import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.tvlife.imageloader.core.assist.LoadedFrom;
import com.tvlife.imageloader.core.display.drawable.RoundedAndReviseSizeDrawable;
import com.tvlife.imageloader.core.imageaware.ImageAware;
import com.tvlife.imageloader.core.imageaware.ImageViewAware;
import com.tvlife.imageloader.utils.L;

/**
 * 此显示类主要是针对淘宝的商品特殊定制：
 * 1： 如果图片的大小[包括 宽 以及高]比View 还大，那么会在图片的中间部分取出一块显示在View上； 并且根据 cornerRadiusPixels 来处理圆角
 * 根据 marginPixels 来处理 图片与View之间的间距
 * 2：如果图片的大小[包括 宽 以及高]都比 VIew小，则不处理圆角，直接在View的中间显示图片 ;
 * 同样， 根据 marginPixels 来处理 图片与View之间的间距
 * 3: 如果图片的大小[宽或者高其中一个]比 View的小 [宽或者高其中一个] 则直接获取图片的中间部分显示在VIEW中，
 * 同样， 根据 marginPixels 来处理 图片与View之间的间距
 * @author yunzhong.qyz
 */
public class RoundedAndReviseSizeBitmapDisplayer implements BitmapDisplayer {

    private final String      TAG             = "RoundedAndReviseSizeBitmapDisplayer";

    /**
     * 
     */
    protected final int cornerRadius;
    protected final int margin;
    private boolean     mLoggingEnabled = false;
    private boolean     mScale          = false;

    /**
     * 
     * @param cornerRadiusPixels   圆角值
     */
    public RoundedAndReviseSizeBitmapDisplayer(int cornerRadiusPixels) {
        this(cornerRadiusPixels, 0,  false);
    }

    
    /**
     * 
     * @param cornerRadiusPixels  圆角值
     * @param marginPixels        留出的边框
     */
    public RoundedAndReviseSizeBitmapDisplayer(int cornerRadiusPixels, int marginPixels) {  
        this(cornerRadiusPixels, marginPixels,  false);
    }

    
    /**
     * 
     * @param cornerRadiusPixels   圆角值
     * @param scale                缩放值， 默认为 false: 根据View的大小来进行裁剪图片
     *                                           true:  只是进行缩放处理，不进行任何的裁剪
     */
    public RoundedAndReviseSizeBitmapDisplayer(int cornerRadiusPixels, boolean scale) { 
        this(cornerRadiusPixels, 0,  scale);
    }

    
    /**
     * 
     * @param cornerRadiusPixels    圆角值
     * @param marginPixels          留出的边框
     * @param scale                 缩放值， 默认为 false: 根据View的大小来进行裁剪图片
     *                                            true:  只是进行缩放处理，不进行任何的裁剪
     */
    public RoundedAndReviseSizeBitmapDisplayer(int cornerRadiusPixels, int marginPixels, boolean scale) {
        this.cornerRadius = cornerRadiusPixels;
        this.margin = marginPixels;
        mScale = scale;
    }

  

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom, boolean loggingEnabled) {

        mLoggingEnabled = loggingEnabled;

        if (mLoggingEnabled) {
            Log.i(TAG, "display  --->  bitmap = " + bitmap + ";   imageAware = " + imageAware + "; loadedFrom = " + loadedFrom);
        }

        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }

        if ((bitmap != null) && (!bitmap.isRecycled())) { 
            imageAware.setImageDrawable(new RoundedAndReviseSizeDrawable(bitmap, cornerRadius, margin, mScale, loggingEnabled));
        }
    }
}
