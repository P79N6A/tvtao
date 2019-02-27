package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.detailbundle.R;

public class DetailEvaluatecItemView extends LinearLayout {

    private String TAG = "DetailEvaluatecItemView";

    private Paint mDividerPaint;
    private boolean mDrawDivider;
    private int mDividerLRspace;

    private Paint mBackgroudPaint;
    private Rect mBackgroudRect;

    private boolean mDrawBackgroud;

    private Paint mMarkPaint; // 画笔
    private boolean mShowMark;
    private Rect mMarkRect;

    public DetailEvaluatecItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDetailEvaluatecItemView(context);
    }

    public DetailEvaluatecItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDetailEvaluatecItemView(context);
    }

    public DetailEvaluatecItemView(Context context) {
        super(context);
        initDetailEvaluatecItemView(context);
    }

    private void initDetailEvaluatecItemView(Context context) {
        mDrawDivider = false;
        mMarkPaint = new Paint();
        int color = context.getResources().getColor(R.color.ytsdk_detail_evaluate_mark_color);
        mMarkPaint.setColor(Color.BLACK);
        mMarkPaint.setAlpha((int) (255 * 0.2f));
        mMarkPaint.setStyle(Paint.Style.FILL);
        mShowMark = true;

        mMarkRect = new Rect();
        mMarkRect.setEmpty();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        // 画背景
        if (mDrawBackgroud && mBackgroudPaint != null && mBackgroudRect != null) {
            if (mBackgroudRect.bottom < 0) {
                mBackgroudRect.bottom = getHeight();
            }
            if (mBackgroudRect.right < 0) {
                mBackgroudRect.right = getWidth();
            }
            canvas.drawRect(mBackgroudRect, mBackgroudPaint);
        }

        super.dispatchDraw(canvas);

        // 画分割线
        if (mDividerPaint != null && mDrawDivider) {
            int top = getHeight();
            int left = mDividerLRspace;
            int right = mDividerLRspace;
            canvas.drawLine(left, top, getWidth() - right, getHeight(), mDividerPaint);
        }

        if (mMarkPaint != null && mShowMark) {
            getFocusedRect(mMarkRect);
            offsetDescendantRectToMyCoords(this, mMarkRect);
            canvas.drawRect(mMarkRect, mMarkPaint);
        }
    }

    public void setDividerResId(int colorResId, int dividerhight) {
        setDividerDrawable(getResources().getColor(colorResId), dividerhight);
    }

    /**
     * 设置分割线
     */
    public void setDividerDrawable(int dividercolor, int dividerhight) {
        if (mDividerPaint == null) {
            // 创建Paint
            mDividerPaint = new Paint();
            // 设置画笔风格 
            mDividerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mDividerPaint.setStrokeJoin(Paint.Join.ROUND);
            // 设置画笔方形
            mDividerPaint.setStrokeCap(Paint.Cap.SQUARE);
            mDividerPaint.setDither(true);
            // 设置使用抗锯齿功能
            mDividerPaint.setAntiAlias(true);
        }
        mDividerPaint.setColor(dividercolor);
        mDividerPaint.setStrokeWidth(dividerhight);
        mDrawDivider = true;
        invalidate();
    }

    /**
     * 设置Divider的左右空隙
     * @param space
     */
    public void setDividerLeftRightSpace(int space) {
        mDividerLRspace = space;
    }

    public void setBackgroudColorResId(int colorResId, Rect rt) {
        setBackgroudColor(getResources().getColor(colorResId), rt);
    }

    /**
     * 画背景
     * @param color
     * @param rt
     */
    public void setBackgroudColor(int color, Rect rt) {
        AppDebug.i(TAG, TAG + ".setBackgroudColor.rt = " + rt);
        if (mBackgroudPaint == null) {
            // 创建Paint
            mBackgroudPaint = new Paint();
            // 设置画笔风格 
            mBackgroudPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mBackgroudPaint.setStrokeJoin(Paint.Join.ROUND);
            // 设置画笔方形
            mBackgroudPaint.setStrokeCap(Paint.Cap.SQUARE);
            mBackgroudPaint.setDither(true);
            // 设置使用抗锯齿功能
            mBackgroudPaint.setAntiAlias(true);
        }
        mBackgroudPaint.setColor(color);
        mBackgroudRect = rt;
        invalidate();
    }

    /**
     * 显示背景
     * @param drawBackgroud
     */
    public void setShowDrawBackgroud(boolean drawBackgroud) {
        mDrawBackgroud = drawBackgroud;
        invalidate();
    }

    public boolean getShowDrawBackgroud() {
        return mDrawBackgroud;
    }

    public void showMark(boolean show) {
        mShowMark = show;
        invalidate();
    }

    public boolean getShowMark() {
        return mShowMark;
    }
}
