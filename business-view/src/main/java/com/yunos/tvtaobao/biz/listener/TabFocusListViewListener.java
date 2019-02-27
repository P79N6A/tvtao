package com.yunos.tvtaobao.biz.listener;


import android.view.View;

import com.yunos.tv.app.widget.AdapterView;

/**
 * 左侧TAB的Listener
 * @author yunzhong.qyz
 */
public interface TabFocusListViewListener {

    /**
     * 焦点变化处理
     * @param v
     * @param hasFocus
     */
    public void onFocusChange(View v, boolean hasFocus);

    /**
     * 选中变化的处理
     * @param select
     * @param position
     * @param isSelect
     * @param fatherView
     */
    public void onItemSelected(View select, int position, boolean isSelect, View fatherView);

    /**
     * 点击Item的处理
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id);

}
