package com.yunos.tvtaobao.juhuasuan.activity.Category;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.yunos.tvtaobao.juhuasuan.R;


/**
 * 分类fragment的自定义布局
 * 主要是手动画左右两边页面的指示图片信息
 * @author tim
 */
public class CategoryFragmentLayout extends LinearLayout {

    private BitmapDrawable mLeftHintBitmapDrawable;//左页有效指示图片
    private BitmapDrawable mRightHintBitmapDrawable;//右页有效指示图片
    private BitmapDrawable mLeftDisableBitmapDrawable;//右页无效指示图片
    private BitmapDrawable mRightDisableBitmapDrawable;//右页无效指示图片
    private boolean mEnableShowHint;//是否显示左右两边指示图片
    private boolean mLeftHintEnable;//左边指示是否有效
    private boolean mRightHintEnable;//右边指示是否有效
    private int mPageHintPadding;//指示图片的空隙

    public CategoryFragmentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLeftHintBitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(
                R.drawable.jhs_category_page_left_hint);
        mRightHintBitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(
                R.drawable.jhs_category_page_right_hint);
        mLeftDisableBitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(
                R.drawable.jhs_category_page_left_hint_disable);
        mRightDisableBitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(
                R.drawable.jhs_category_page_right_hint_disable);
        mPageHintPadding = context.getResources().getDimensionPixelSize(R.dimen.jhs_page_hint_padding);
    }

    /**
     * 设置是否显示左右两边的指示图片
     * @param enable
     */
    public void setShowHintEnable(boolean enable) {
        mEnableShowHint = enable;
    }

    /**
     * 左页指示是否有效
     * @param enable
     */
    public void setLeftEnableHint(boolean enable) {
        mLeftHintEnable = enable;
    }

    /**
     * 右边指示是否有效
     * @param enable
     */
    public void setRightEnableHint(boolean enable) {
        mRightHintEnable = enable;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        //显示指示信息
        if (mEnableShowHint) {
            int leftHintX = mPageHintPadding;
            int leftHintY = getTop() + getHeight() / 2;
            int rightHintX = getWidth() - mRightHintBitmapDrawable.getBitmap().getWidth() - mPageHintPadding;
            int rightHintY = leftHintY;
            if (mLeftHintEnable) {
                canvas.drawBitmap(mLeftHintBitmapDrawable.getBitmap(), leftHintX, leftHintY, null);
            } else {
                canvas.drawBitmap(mLeftDisableBitmapDrawable.getBitmap(), leftHintX, leftHintY, null);
            }

            if (mRightHintEnable) {
                canvas.drawBitmap(mRightHintBitmapDrawable.getBitmap(), rightHintX, rightHintY, null);
            } else {
                canvas.drawBitmap(mRightDisableBitmapDrawable.getBitmap(), rightHintX, rightHintY, null);
            }
        }
    }
}
