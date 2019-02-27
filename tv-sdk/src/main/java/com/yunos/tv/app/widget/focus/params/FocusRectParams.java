package com.yunos.tv.app.widget.focus.params;

import android.graphics.Rect;

public class FocusRectParams {

    final public static int CENTER_X = 0x00000001;
    final public static int CENTER_X_FOCUS = 0x00000004;
    final public static int CENTER_Y = 0x00000010;
    final public static int CENTER_Y_FOCUS = 0x00000040;

    Rect focusRect = new Rect();
    float coefX;
    float coefY;
    int mCenterMode = CENTER_X_FOCUS | CENTER_Y;

    public FocusRectParams() {

    }

    public FocusRectParams(FocusRectParams p) {
        focusRect.set(p.focusRect());
        coefX = p.coefX();
        coefY = p.coefY();
        mCenterMode = p.centerMode();
    }

    public FocusRectParams(Rect r, float x, float y, int c) {
        focusRect.set(r);
        coefX = x;
        coefY = y;
        mCenterMode = c;
    }

    public FocusRectParams(Rect r, float x, float y) {
        focusRect.set(r);
        coefX = x;
        coefY = y;
    }

    public void set(Rect r, float x, float y, int c) {
        focusRect.set(r);
        coefX = x;
        coefY = y;
        mCenterMode = c;
    }

    public void set(Rect r, float x, float y) {
        focusRect.set(r);
        coefX = x;
        coefY = y;
    }

    public void set(FocusRectParams p) {
        if (p != null) {
            focusRect.set(p.focusRect());
            coefX = p.coefX();
            coefY = p.coefY();
            mCenterMode = p.centerMode();
        }
    }

    public Rect focusRect() {
        return focusRect;
    }

    public float coefX() {
        return coefX;
    }

    public float coefY() {
        return coefY;
    }

    public void setCenterMode(int mode) {
        mCenterMode = mode;
    }

    public int centerMode() {
        return mCenterMode;
    }
}
