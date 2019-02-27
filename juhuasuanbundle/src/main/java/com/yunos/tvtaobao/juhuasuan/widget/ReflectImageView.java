package com.yunos.tvtaobao.juhuasuan.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ImageView;

import com.yunos.tv.app.widget.utils.ImageUtils;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.ItemInterface;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.AccelerateFrameInterpolator;


public class ReflectImageView extends ImageView implements ItemInterface {

    public static final int SHOW_TOP = 1;
    public static final int SHOW_BOTTOM = 2;
    public static final int SHOW_CENTER = 3;

    private String TAG = "ReflectImageView";
    private static boolean DEBUG = true;

    private int mReflectHight = 0;
    public int reflectionGap = 2;
    public int filmPostion = 0;
    public ImageView imageView = null;
    public GridView gridView = null;
    public String name = "";
    public int txtShowType = 0;
    private String text = "";
    private int textSize = 24;
    public boolean isShow = false;
    private int type = 0;
    public String defaultIcon = "";

    private int mIndex = -1;
    private Bitmap mBitmap = null;
    private Paint mPaint;

    // by leiming.yanlm
    public boolean isBitmapLoaded = false; // Global.getXXImageLoader()加载的default_image,error部分只执行一次

    public ReflectImageView(Context context) {
        super(context);
        init();
    }

    public ReflectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReflectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setShadowLayer(1, 2, 2, Color.BLACK);
        mPaint.setTextSize(textSize);
    }

    static int DEFAULT_FOCUS_STYLE = 0;
    int mFocusFromLeft = DEFAULT_FOCUS_STYLE;
    int mFocusFromRight = DEFAULT_FOCUS_STYLE;
    int mFocusFromUp = DEFAULT_FOCUS_STYLE;
    int mFocusFromDown = DEFAULT_FOCUS_STYLE;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int hight = 0;
        int width = 0;
        int textW = (int) mPaint.measureText(getText());

        if (type == SHOW_BOTTOM) {
            width = (this.getWidth() - textW) / 2;
            hight = this.getHeight() - mReflectHight - textSize;
        } else if (type == SHOW_TOP) {
            width = this.getWidth() - textW - 20;
            hight = 20 + textSize;
        } else {
            width = this.getWidth() - textW - 15;
            hight = (this.getHeight() - mReflectHight + textSize) / 2;
        }
        canvas.drawText(getText(), width, hight, mPaint);
    }

    public void setText(String text, int type) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    @Override
    public void setImageResource(int resId) {
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), resId);
        int imageViewHight = this.getLayoutParams().height;
        int imageViewWidth = this.getLayoutParams().width;
        if (imageViewHight > originalBitmap.getHeight() || imageViewWidth > originalBitmap.getWidth()) {
            this.setImageBitmap(ImageUtils.getScaleBitmap(originalBitmap, imageViewWidth, imageViewHight));
        } else {
            super.setImageResource(resId);
        }
    }

    public void setImageResource(int resId, int reflectHight) {
        this.mReflectHight = 80;
        Bitmap originalImage = BitmapFactory.decodeResource(getResources(), resId);
        CreateReflectBitmap(originalImage, reflectHight);
    }

    public void setImageBitmap(Bitmap bitmap, int reflectHight) {
        this.mReflectHight = 80;
        CreateReflectBitmap(bitmap, reflectHight);
    }

    private void CreateReflectBitmap(Bitmap originalImage, int reflectHight) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        final Matrix matrix = new Matrix();
        matrix.preScale(1, -1);
        final Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, reflectHight, width,
                (int) (height - reflectHight), matrix, false);
        final Bitmap bitmap4Reflection = Bitmap.createBitmap(width, (int) (height + reflectHight), Config.ARGB_8888);
        final Canvas canvasRef = new Canvas(bitmap4Reflection);
        canvasRef.drawBitmap(originalImage, 0, 0, null);
        final Paint deafaultPaint = new Paint();
        deafaultPaint.setColor(Color.TRANSPARENT);
        canvasRef.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
        canvasRef.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
        reflectionImage.recycle();
        final Paint paint = new Paint();
        final LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmap4Reflection.getHeight()
                + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        canvasRef.drawRect(0, height, width, bitmap4Reflection.getHeight() + reflectionGap, paint);

        this.setImageBitmap(bitmap4Reflection);
    }

    // @Override
    // public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
    // if (mReflectHight > 0 ) {
    // Rect firstRect = new Rect();
    // super.getGlobalVisibleRect(firstRect, globalOffset);
    // r.set(0, 0, this.getWidth(), this.getHeight() - mReflectHight -
    // reflectionGap);
    // if (globalOffset != null) {
    // globalOffset.set(-computeHorizontalScrollOffset(),
    // -computeVerticalScrollOffset());
    // }
    // return getParent().getChildVisibleRect(this, r, globalOffset);
    // } else {
    // return super.getGlobalVisibleRect(r, globalOffset);
    // }
    // }

    @Override
    public boolean getIfScale() {
        return true;
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        return getHeight() - mReflectHight - reflectionGap;
    }

    @Override
    public Rect getOriginalRect() {
        Rect rect = new Rect();
        // int[] location = new int[2];
        // getLocationOnScreen(location);
        // rect.left = location[0];
        // rect.right = location[0] + getWidth();
        // rect.top = location[1];
        // rect.bottom = location[1] + getHeight();
        rect.left = getLeft();
        rect.right = getRight();
        rect.top = getTop();
        rect.bottom = getBottom();

        //		rect.bottom -= (mReflectHight + reflectionGap);

        return rect;
    }

    @Override
    public Rect getItemScaledRect(float scaledX, float scaledY) {
        Rect rect = new Rect();
        // int[] location = new int[2];
        // getLocationOnScreen(location);

        int imgW = getWidth();
        int imgH = getHeight();

        rect.left = (int) (getLeft() - (scaledX - 1.0f) * imgW / 2 + 0.5f);
        rect.right = (int) (rect.left + imgW * scaledX - 0.5f);
        rect.top = (int) (getTop() - (scaledY - 1.0f) * imgH / 2 + 0.5f);
        rect.bottom = (int) (rect.top + imgH * scaledY - 0.5f);

        //		int imgReflectH = (int) (mReflectHight * scaledY + reflectionGap * scaledY + 0.5f);
        //		rect.bottom -= imgReflectH;

        return rect;
    }

    AccelerateFrameInterpolator mScaleInterpolator = new AccelerateFrameInterpolator();
    AccelerateFrameInterpolator mFocusInterpolator = new AccelerateFrameInterpolator(0.5f);

    @Override
    public FocusedBasePositionManager.FrameInterpolator getFrameScaleInterpolator() {
        // TODO Auto-generated method stub
        return mScaleInterpolator;
    }

    @Override
    public FocusedBasePositionManager.FrameInterpolator getFrameFocusInterpolator() {
        // TODO Auto-generated method stub
        return mFocusInterpolator;
    }

    @Override
    public Rect getFocusPadding(Rect originalRect, Rect lastOriginalRect, int direction, int focusFrameRate) {
        //		return FocusPaddingUtil.getFocusPadding(originalRect, lastOriginalRect, direction, focusFrameRate, mFocusInterpolator, this.mFocusFromLeft,
        //				this.mFocusFromRight, this.mFocusFromUp, this.mFocusFromDown);

        return null;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public int getIndex() {
        return mIndex;
    }

    public void onSetDisplayImageBitmap(Bitmap bm) {

        //        int imageViewHight = this.getLayoutParams().height;
        //        int imageViewWidth = this.getLayoutParams().width;
        //        if (imageViewHight > bm.getHeight() || imageViewWidth > bm.getWidth()) {
        ////            this.setImageBitmap(ImageUtils.getScaleBitmap(bm, imageViewWidth, imageViewHight));
        //            
        //            bm = ImageUtils.getScaleBitmap(bm, imageViewWidth, imageViewHight);
        //            
        //        }  

        mBitmap = bm;
        setImageBitmap(mBitmap);
    }

    public void onRecycleDisplayBitmap(Bitmap defaultBm) {
        setImageBitmap(defaultBm);
        if ((mBitmap != null) && (!mBitmap.isRecycled())) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

}
