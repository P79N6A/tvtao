package com.yunos.tvtaobao.biz.focus_impl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.yunos.tv.core.common.AppDebug;

/**
 * Created by GuoLiDong on 2018/10/15.
 * 焦点区域，绑定了一个焦点节点
 */

public class FocusArea extends FrameLayout implements FocusNode.Binder {
    private FocusNode focusNode = new FocusNode(this);

    public FocusArea(@NonNull Context context) {
        this(context, null);
    }

    public FocusArea(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusArea(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != VISIBLE) {
            focusNode.setNodeFocusable(false);
        } else {
            focusNode.setNodeFocusable(true);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    Paint paint = null;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        focusNode.onFocusDraw(canvas);

        if (paint == null) {
            paint = new Paint();
            paint.setColor(Color.RED);
        }
//        if (getHeight() > 0 && getWidth() > 0) {
//            canvas.drawText(""+focusNode.getFocusDrawCount(),getWidth()/2,getHeight()/2,paint);
//        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            AppDebug.d(this.getClass().getSimpleName(), "@" + this.hashCode() + " dispatchKeyEvent " + event.getKeyCode());
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void invalidate(Rect dirty) {
        super.invalidate(dirty);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public FocusNode getNode() {
        return focusNode;
    }

    public void setFocusConsumer(FocusConsumer focusConsumer) {
        focusNode.setFocusConsumer(focusConsumer);
    }
}
