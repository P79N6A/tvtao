package com.yunos.tvtaobao.biz.focus_impl;

import android.content.Context;
import android.view.Window;

/**
 * Created by GuoLiDong on 2018/10/16.
 * 焦点上下文
 * 可应用在一颗界面树上，所以应有Window；
 * 可能需要访问上下文，所以应有关联的上下文
 * 必须得有焦点管理器
 */

public interface FocusContext {
    FocusManager getFocusManager();

    Window getAttachedWindow();

    Context getAttachedContext();
}
