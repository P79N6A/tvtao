package com.yunos.tvtaobao.biz.listener;

import android.view.ViewGroup;


/**
 * 行数改变时的Listener
 * @author yunzhong.qyz
 *
 */
public interface VerticalItemHandleListener {
    boolean onGetview(ViewGroup container, String tabkeyword, int position);
}
