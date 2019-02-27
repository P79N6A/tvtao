package com.yunos.tvtaobao.juhuasuan.view;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;

/**
 * 生成右边有圆角的矩形背景
 * @author Administrator
 */
public class RoundShapeDrawable extends Drawable {

    private static final int DEFAULT_WIDHT    = 0;
    private static final int DEFAULT_HEIGHT   = 0;
    private static final int DEFAULT_EDHWIDTH = 5;
    private Paint p;
    private Paint innerP;
    private int              width            = DEFAULT_WIDHT;    //背景宽度   
    private int              height           = DEFAULT_HEIGHT;   //背景高度  
    private int              r;                                   //圆角半径  
    private boolean          isEdging         = false;            //是否带边框  
    private int              edgWidth         = DEFAULT_EDHWIDTH; //边框厚度   

    public RoundShapeDrawable(View view, int radius, boolean isEdg, int color) {
        initPaint(color);
        getViewWH(view);
        r = radius;
        isEdging = isEdg;
    }

    private void initPaint(int color) {
        p = new Paint();
        p.setColor(color);
        p.setStrokeJoin(Join.ROUND);
        p.setStrokeCap(Cap.ROUND);
        p.setStrokeWidth(3);
        p.setAntiAlias(true);
        innerP = new Paint();
        innerP.setColor(Color.WHITE);
        innerP.setStrokeJoin(Join.ROUND);
        innerP.setStrokeCap(Cap.ROUND);
        innerP.setStrokeWidth(3);
        innerP.setAntiAlias(true);
    }

    boolean hasMeasured = false;

    private void getViewWH(final View v) {
        if (null == v) {
            return;
        }
        v.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                if (!hasMeasured) {
                    width = v.getMeasuredWidth();
                    height = v.getMeasuredHeight();
                    hasMeasured = true;
                }
                return true;
            }
        });
    }

    public RoundShapeDrawable setRadius(int radius) {
        this.r = radius;
        invalidateSelf();
        return this;
    }

    public RoundShapeDrawable setEdgWidth(int width) {
        this.edgWidth = width;
        if (isEdging) {
            invalidateSelf();
        }
        return this;
    }

    public RoundShapeDrawable setEdgColor(int color) {
        p.setColor(color);
        invalidateSelf();
        return this;
    }

    public RoundShapeDrawable setInnerColor(int color) {
        innerP.setColor(color);
        invalidateSelf();
        return this;
    }

    private void drawRect(Canvas canvas) {
        // 消除锯齿 
        RectF Roundrect = new RectF(0, 0, width, height);
        RectF rect = new RectF(0, 0, width-r, height);
        canvas.drawRect(rect, p);
        canvas.drawRoundRect(Roundrect, r, r, p);
    }

    private void drawEdg(Canvas canvas) {
        RectF rf = new RectF(edgWidth, edgWidth, width - edgWidth, height - edgWidth);
        canvas.drawRoundRect(rf, r, r, innerP);
    }

    @Override
    public void draw(Canvas canvas) {
        drawRect(canvas);
        if (isEdging) {
            drawEdg(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        // TODO Auto-generated method stub  
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // TODO Auto-generated method stub   
    }

    @Override
    public int getOpacity() {
        // TODO Auto-generated method stub  
        return 0;
    }

}
