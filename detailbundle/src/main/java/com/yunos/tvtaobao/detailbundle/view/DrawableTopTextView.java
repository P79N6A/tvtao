package com.yunos.tvtaobao.detailbundle.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.yunos.tv.app.widget.focus.FocusTextView;

public class DrawableTopTextView extends FocusTextView {

    // 分割线的画笔 
    private Paint mDividerPaint;
    // 是否画分割线
    private boolean mDrawDivider;

    // 分割线的位置
    private boolean mLeftDivider;
    private boolean mRightDivider;

    private float mDividerhight;

    // 顶部的Drawable
    private Drawable mTopDrawable;
    // Drawable 与 文字 的间距
    private int mDrawablePadding;
    // 文字
    private StringBuilder mText;

    // 文字的画笔
    private TextPaint mTextPaint;

    // 文字的大小
    private Rect mBounds;

    public DrawableTopTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initDispatchDraw(context, attrs);
    }

    public DrawableTopTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDispatchDraw(context, attrs);
    }

    public DrawableTopTextView(Context context) {
        super(context);
        initDispatchDraw(context, null);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    private void initDispatchDraw(Context context, AttributeSet attrs) {
        mText = new StringBuilder();
        mText.delete(0, mText.length());

        mBounds = new Rect();
        mBounds.setEmpty();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 获取drawable
        Drawable[] drawables = this.getCompoundDrawables();
        if (drawables != null && drawables[1] != null) {
            if (mTopDrawable != null) {
                mTopDrawable.setCallback(null);
                mTopDrawable = null;
            }
            ConstantState constantState = drawables[1].getConstantState();
            if (constantState != null) {
                mTopDrawable = constantState.newDrawable();
            }
            setCompoundDrawables(null, null, null, null);
        }

        // 获取drawable 和文字的间隔
        mDrawablePadding = getCompoundDrawablePadding();

        // 获取文字
        CharSequence charSequence = getText();
        if (charSequence != null && charSequence.length() > 0) {
            mText.delete(0, mText.length());
            mText.append(charSequence);
            setText(null);

            // 获取画笔
            mTextPaint = getPaint();
        }

        super.onDraw(canvas);

        if (mDividerPaint != null && mDrawDivider) {

            float width = (float) getWidth();
            float height = (float) getHeight();

            if (mLeftDivider) {
                canvas.drawLine(0, 0, 1, getHeight(), mDividerPaint);
            }
            if (mRightDivider) {
                canvas.drawLine(getWidth(), 0, getWidth() - 1, getHeight(), mDividerPaint);
            }
        }

        int drawable_w = 0;
        int drawable_h = 0;
        if (mTopDrawable != null) {
            drawable_w = mTopDrawable.getIntrinsicWidth();
            drawable_h = mTopDrawable.getIntrinsicHeight();
        }
        int text_w = 0;
        int text_h = 0;
        if (mText.length() > 0 && mTextPaint != null) {
            mBounds.setEmpty();
            mTextPaint.getTextBounds(mText.toString(), 0, mText.toString().length(), mBounds);
            text_w = mBounds.width();
            text_h = mBounds.height();
        }
        int vspace_h = drawable_h + mDrawablePadding + text_h;
        float drawable_y = 0;
        if (mTopDrawable != null) {
            float drawable_x = (getWidth() - drawable_w) / 2.0f;
            drawable_y = (getHeight() - vspace_h) / 2.0f;
            canvas.save();
            canvas.translate(drawable_x, drawable_y);
            mTopDrawable.setBounds(0, 0, mTopDrawable.getIntrinsicWidth(), mTopDrawable.getIntrinsicHeight());
            mTopDrawable.draw(canvas);
            canvas.restore();
        }

        if (mText.length() > 0 && mTextPaint != null) {
            float text_x = (getWidth() - text_w) / 2.0f;
            float text_y = drawable_y + drawable_h + text_h + mDrawablePadding;
            canvas.drawText(mText.toString(), text_x, text_y, mTextPaint);
        }
    }

    /**
     * 设置分割线
     */
    public void setDividerDrawable(int dividercolor) {
        if (mDividerPaint == null) {
            // 创建Paint
            mDividerPaint = new Paint();
            // 设置画笔风格 
            mDividerPaint.setStyle(Paint.Style.STROKE);
            mDividerPaint.setStrokeJoin(Paint.Join.ROUND);
            // 设置画笔方形
            mDividerPaint.setStrokeCap(Paint.Cap.SQUARE);
            mDividerPaint.setDither(true);
            // 设置使用抗锯齿功能
            mDividerPaint.setAntiAlias(true);
        }
        mDrawDivider = true;
        mDividerPaint.setColor(dividercolor);
        mDividerPaint.setStrokeWidth(mDividerhight);
    }

    /**
     * 是否把线画在左边
     * @param leftDivider
     */
    public void setDivider(boolean leftDivider, boolean rightDivider) {
        mLeftDivider = leftDivider;
        mRightDivider = rightDivider;
        invalidate();
    }

}
