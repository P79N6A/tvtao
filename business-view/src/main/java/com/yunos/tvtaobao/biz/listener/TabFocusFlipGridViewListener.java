package com.yunos.tvtaobao.biz.listener;


import android.view.View;

import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;

/**
 * GridView 的 Listener
 * @author yunzhong.qyz
 */
public interface TabFocusFlipGridViewListener {

    public void onFocusChange(FocusFlipGridView focusFlipGridView, String tabkey, View v, boolean hasFocus);

    public void onLayoutDone(FocusFlipGridView focusFlipGridView, String tabkey, boolean isFirst);

    public void onItemSelected(FocusFlipGridView focusFlipGridView, String tabkey, View selectview, int position,
                               boolean isSelect, View parent);

    public boolean onGetview(FocusFlipGridView focusFlipGridView, String tabkey, int position, int currentTabPosition);

    public boolean onItemClick(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0, View arg1,
                               int position, long arg3);

    public void onFinished(FocusFlipGridView focusFlipGridView, String tabkey);

    public void onStart(FocusFlipGridView focusFlipGridView, String tabkey);

}
