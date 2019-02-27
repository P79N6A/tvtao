package com.yunos.tvtaobao.biz.focus_impl;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by GuoLiDong on 2018/10/16.
 * 定义焦点消费者应有的功能、进入、离开、点击、绘制
 */

public interface FocusConsumer {

    public boolean onFocusEnter();

    public boolean onFocusLeave();

    public boolean onFocusClick();

    public boolean onFocusDraw(Canvas canvas);
}
