package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.widget.Gallery;
import android.widget.SpinnerAdapter;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.juhuasuan.adapter.BrandItemAdapter;

/**
 * /** Gallery带上选中的聚焦框
 * @author wb-daishulin
 */

public class SubjectGallery extends Gallery {

    private static NinePatchDrawable mSelectedNineDrawable;
    private Rect rect; // 显示框的区域
    public float mSelectedScale = 1.0f; // 选中的放大系数
    private int mSelectedPadding; // 聚焦框的间隙
    private Display display; // 聚焦框的间隙
    private static int[] data;
    private int galleryTop = 0;
    private String TAG = "zhuilu";
    private GradientDrawable leftShade, rightShade;
    Context mContext;
    BrandItemAdapter mAdapter;

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }

    public SubjectGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        AppDebug.i(TAG, "   SubjectGallery(Context context, AttributeSet attrs)");
        mSelectedNineDrawable = (NinePatchDrawable) context.getResources().getDrawable(
                R.drawable.jhs_brand_detail_focus);
        galleryTop = getResources().getDimensionPixelSize(R.dimen.dp_100);
    }

    /**
     * 设置选中的放大系数
     * @param scale
     */
    public void setSelectedScale(float scale) {
        mSelectedScale = scale;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event != null && event.getAction() == KeyEvent.ACTION_UP) {
            if (null != mAdapter) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    mAdapter.actionMoveLeft = true;
                    mAdapter.actionMoveRight = false;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mAdapter.actionMoveLeft = false;
                    mAdapter.actionMoveRight = true;
                }

            }
        } else if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (null != mAdapter) {
                mAdapter.isScroing = true;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (BrandItemAdapter) adapter;
    }

    /**
     * 设置选中框的间隙
     * @param padding
     */
    public void setSelectedDrawablePadding(int padding) {
        mSelectedPadding = padding;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // AppDebug.i(
        // TAG,
        // "   dispatchDraw = " + canvas + "  "
        // + System.currentTimeMillis());
        if (getData() != null) {
            myinitRect();
            mSelectedNineDrawable.draw(canvas);
        }

    }

    public void fresh(Display screenDisplay) {
        display = screenDisplay;
        this.invalidate();
    }

    /**
     * 初始化显示区域，只计算一次
     */
    private void myinitRect() {
        if (rect == null) {
            rect = new Rect();
            int scale_offset_x = (int) ((mSelectedScale - 1.0f) * Math.abs(getData()[0] - getData()[2]) / 2);
            int scale_offset_y = (int) ((mSelectedScale - 1.0f) * Math.abs(getData()[1] - getData()[3]) / 2);
            AppDebug.i(TAG, "   scale_offset_x=" + scale_offset_x);
            AppDebug.i(TAG, "   scale_offset_y=" + scale_offset_y);
            rect.left = getData()[0] - scale_offset_x - mSelectedPadding;
            rect.top = getData()[1] - scale_offset_y - mSelectedPadding + 20;
            rect.right = getData()[2] + scale_offset_x + mSelectedPadding;
            rect.bottom = getData()[3] + scale_offset_y + mSelectedPadding;
            mSelectedNineDrawable.setBounds(rect);
        }

    }

    /**
     * 分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}