package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

import com.yunos.tvtaobao.juhuasuan.R;


public class SizingGallery extends Gallery implements ViewScroller.ScrollListener {

    private int selectedBackgroudRes = R.drawable.jhs_4th_move_list_focus;
    private int mBottom;
    private int mPadding;
    private NinePatchDrawable bg;
    private NinePatchDrawable bg2;
    public int getSelectedBackgroudRes() {
        return selectedBackgroudRes;
    }

    
    public void setSelectedBackgroudRes(int selectedBackgroudRes) {
        this.selectedBackgroudRes = selectedBackgroudRes;
    }

    public SizingGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBottom = context.getResources().getDimensionPixelSize(R.dimen.dp_20);
        mPadding = context.getResources().getDimensionPixelSize(R.dimen.dp_8);
        bg = (NinePatchDrawable) getResources().getDrawable(selectedBackgroudRes);
        bg2 = (NinePatchDrawable) getResources().getDrawable(R.drawable.jhs_tv_gou_list_bar_bg);
    }

    @Override
    public void dispatchSetSelected(boolean selected) {
        // TODO Auto-generated method stub
        super.dispatchSetSelected(selected);
    }

    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawRect(canvas);
    }

    public void drawRect(Canvas canvas) {
        View view = getSelectedView();
        if (view == null)
            return;
        Rect selfrect = new Rect();
        getGlobalVisibleRect(selfrect);
        int w = (int) (view.getWidth() + mPadding);
        int h = (int) (view.getHeight() + mPadding);
        int ww = selfrect.right - selfrect.left;
        int hh = selfrect.bottom - selfrect.top;
        int left = (ww - w) / 2;
        int top = (hh - h) / 2;

        bg.setBounds(left, top, left + w, top + h  -mBottom);
        bg.draw(canvas);
    }
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        // TODO Auto-generated method stub
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

//    @Override
//    protected boolean getChildStaticTransformation(View child, Transformation t) {
//        t.clear();
//        View selectedChild = getSelectedView();
//        if (child == selectedChild) {
//            Matrix matrix = t.getMatrix();
//            int w2 = child.getWidth() / 2;
//            int h2 = child.getHeight() / 2;
//            //        	matrix.postScale(2f, 1.5f, w2, h2);
//            matrix.postScale(1.5f, 1.5f, w2, h2);
//        }
//        return true;
//    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX(); 
    } 
 
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        int keyCode; 
        if (isScrollingLeft(e1, e2)) { 
            keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
        } else { 
            keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
        } 
        onKeyDown(keyCode, null); 
        return true; 
    } 

    @Override
    public void startScroll() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void scrolling() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void endScroll() {
        // TODO Auto-generated method stub
        
    }
    
}
