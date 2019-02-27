/**
 * Copyright (C) 2015 The ALI OS Project
 * <p>
 * Version     Date            Author
 * <p>
 * 2015-4-19       lizhi.ywp
 */
package com.yunos.tvtaobao.flashsale.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;


public class ContentContainerView extends FrameLayout {
    public final static boolean DEBUG = false;

    public ContentContainerView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        onInit();
    }

    public ContentContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        onInit();
    }

    public ContentContainerView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        onInit();
    }

    protected void onInit() {
        super.setPadding(AppConfig.ARROWBAR_WIDTH, 0, AppConfig.ARROWBAR_WIDTH, 0);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        AppDebug.d("FocusPositionManager", "dispatchDraw:" + this.getClass().toString());
        super.dispatchDraw(canvas);
        if (DEBUG) {
            Paint p = new Paint();

            p.setColor(0xFFFFFF00);
            p.setTextAlign(Align.CENTER);
            p.setTextSize(20);
            canvas.drawLine(0, super.getHeight() / 2, super.getWidth(), super.getHeight() / 2, p);
            canvas.drawLine(super.getWidth() / 2, 0, super.getWidth() / 2, super.getHeight(), p);

            canvas.drawText("(" + super.getWidth() + "," + super.getHeight() + ")", super.getWidth() / 2, super.getHeight() / 2, p);
        }

    }

}
