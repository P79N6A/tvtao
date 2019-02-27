package com.yunos.tvtaobao.goodlist.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tvtaobao.biz.adapter.LifeUiBaseAdapter;
import com.yunos.tvtaobao.goodlist.R;
import com.yunos.tvtaobao.goodlist.view.GoodListMenuFocusFrameLayout;


public class GoodListGirdMenuAdapter extends LifeUiBaseAdapter {

    private int[] mMenuLogo;
    private int[] mMenuLogo_opaque;
    private String[] mTableList;

    private boolean mFirstGetView = true;
    private GoodListMenuFocusFrameLayout mFirstGoodListMenuFocusFrameLayout = null;
    private int mNotSelect = Color.argb(90, 255, 255, 255);

    public GoodListGirdMenuAdapter(Context context) {
        super(context);
    }

    @Override
    public int getCount() {

        if (mTableList != null) {
            return mTableList.length;
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getLayoutID() {
        return R.layout.ytsdk_goodslist_activity_menu;
    }

    @Override
    public void fillView(int position, View convertView, boolean newConvertView, ViewGroup parent) {

        if (mTableList == null) {
            return;
        }

        int tableListSize = mTableList.length;
        int position_Temp = position + 1;
        if (position_Temp > tableListSize) {
            return;
        }
        GoodListMenuFocusFrameLayout goodListMenuFocusFrameLayout = (GoodListMenuFocusFrameLayout) convertView;
        goodListMenuFocusFrameLayout.setMenuLogoRes(mMenuLogo[position]);
        goodListMenuFocusFrameLayout.setMenuLogoOpaqueRes(mMenuLogo_opaque[position]);
        goodListMenuFocusFrameLayout.setMenuTextColorNotSelect(mNotSelect);

        TextView menu = (TextView) convertView.findViewById(R.id.goodlist_menu_logo_text);
        menu.setText(mTableList[position]);
        menu.setTextColor(mNotSelect);

        ImageView logo = (ImageView) convertView.findViewById(R.id.goodlist_menu_logo_imageview);
        logo.setImageResource(mMenuLogo[position]);

        if (mFirstGetView) {
            mFirstGoodListMenuFocusFrameLayout = goodListMenuFocusFrameLayout;
            mFirstGetView = false;

            goodListMenuFocusFrameLayout.setMenuChangeOfSelectState(true);
        }
    }

    /**
     * 设置logo资源
     * @param menulogo
     */
    public void setMenuLogoRes(int[] menulogo) {
        mMenuLogo = menulogo;
    }

    /**
     * @param menulogoopaque
     */
    public void setMenuLogoOpaqueRes(int[] menulogoopaque) {
        mMenuLogo_opaque = menulogoopaque;
    }

    /**
     * 设置菜单文字
     * @param tablelist
     */
    public void setTableList(String[] tablelist) {
        mTableList = tablelist;
    }

    /**
     * 获取第一个焦点菜单
     * @return
     */
    public GoodListMenuFocusFrameLayout getFirstGoodListMenuFocusFrameLayout() {
        return mFirstGoodListMenuFocusFrameLayout;
    }

    /**
     *  
     */
    public void onDestroy() {
    }

}
