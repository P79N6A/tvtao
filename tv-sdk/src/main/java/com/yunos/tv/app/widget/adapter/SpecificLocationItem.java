package com.yunos.tv.app.widget.adapter;

import android.graphics.Rect;

/**
 * Created by quanqing.hqq on 2014/12/5.
 */
public class SpecificLocationItem implements LocationItem {
    private int type = 0;
    private Rect mRect = new Rect();

    private SpecificLocationItem() {

    }

    public SpecificLocationItem(int x, int y, int w, int h) {
        this();
        updateRect(x, y, w, h);
    }

    public void updateRect(int x, int y, int w, int h) {
        mRect.setEmpty();
        mRect.set(x, y, x + w, y + h);
    }

    public Rect getLocation() {
        return mRect;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public int getLeft() {
        return mRect.left;
    }

    @Override
    public int getTop() {
        return mRect.top;
    }

    @Override
    public int getWidth() {
        return mRect.width();
    }

    @Override
    public int getHeight() {
        return mRect.height();
    }
}
