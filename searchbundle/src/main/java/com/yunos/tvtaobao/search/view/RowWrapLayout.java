package com.yunos.tvtaobao.search.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusRelativeLayout;
import com.yunos.tvtaobao.search.R;

public class RowWrapLayout extends FocusRelativeLayout {

    private int verticalSpacing = 10;
    private int horizontalSpacing = 10;
    private int mMaxLines = -1; // 最大行数

    public RowWrapLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RowWrapLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        verticalSpacing = getContext().getResources().getDimensionPixelSize(R.dimen.dp_6_66);
        horizontalSpacing = getContext().getResources().getDimensionPixelSize(R.dimen.dp_6_66);
    }

    public void setMaxLines(int line) {
        mMaxLines = line;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
         * for (int i = 0; i < getChildCount(); i++) { final View child =
         * getChildAt(i); child.measure(MeasureSpec.UNSPECIFIED,
         * MeasureSpec.UNSPECIFIED); } super.onMeasure(widthMeasureSpec,
         * heightMeasureSpec);
         */
        int count = getChildCount();
        int totalHeight = 0;
        int r = getRight();
        int l = getLeft();
        int cr = l;
        int cb = 0;
        int lines = 0;
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            cr += width;
            if (i == 0) {
                cb += height;
            }

            if (cr > r && i != 0) {
                if (mMaxLines > 0) {//热词 控制显示行数
                    lines++;
                    if (lines == mMaxLines) {
                        break;
                    }
                }

                cr = l + width;
                cb += verticalSpacing + height;
            }
            cr += horizontalSpacing;
        }
        totalHeight = cb + verticalSpacing;
        // 设置容器所需的宽度和高度
        setMeasuredDimension(widthMeasureSpec, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        final int count = getChildCount();
        int cr = l;
        int cb = 0;
        int lines = 0;
        for (int i = 0; i < count; i++) {
            final View child = this.getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            cr += width;
            if (i == 0) {
                cb += height;
            }

            if (cr > r && i != 0) {
                if (mMaxLines > 0) {//热词 控制显示行数
                    lines++;
                    if (lines == mMaxLines) {
                        break;
                    }
                }
                cr = l + width;
                cb += verticalSpacing + height;
            }
            // AppDebug.i("xxxx","top:" + (cb-height));
            child.layout(cr - width, cb - height, cr, cb);
            cr += horizontalSpacing;
        }
    }
}
