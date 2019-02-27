package com.yunos.tvtaobao.biz.focus_impl;

import android.graphics.Canvas;

/**
 * Created by GuoLiDong on 2018/10/24.
 * 基本实现，方便使用，编码
 */
public abstract class ConsumerB implements FocusConsumer {
    @Override
    public boolean onFocusEnter() {
        return false;
    }

    @Override
    public boolean onFocusLeave() {
        return false;
    }

    @Override
    public boolean onFocusClick() {
        return false;
    }

    @Override
    public boolean onFocusDraw(Canvas canvas) {
        return false;
    }
}
