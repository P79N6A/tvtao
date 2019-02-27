package com.yunos.tvtaobao.goodlist.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.core.util.ColorHandleUtil;
import com.yunos.tvtaobao.goodlist.R;

public class GoodListMenuFocusFrameLayout extends FrameLayout implements ItemListener {

    private int adjust_buttom = -1;

    private int mMenuLogo = R.drawable.ytsdk_ui2_goodlist_menulogo_default;
    private int mMenuLogo_opaque = R.drawable.ytsdk_ui2_goodlist_menulogo_default_opaque;
    private int mNotSelect = ColorHandleUtil.ColorTransparency(Color.WHITE, 0.5f);

    public GoodListMenuFocusFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onInitGoodListMenuFocusFrameLayout(context);
    }

    public GoodListMenuFocusFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitGoodListMenuFocusFrameLayout(context);
    }

    public GoodListMenuFocusFrameLayout(Context context) {
        super(context);
        onInitGoodListMenuFocusFrameLayout(context);
    }

    private void onInitGoodListMenuFocusFrameLayout(Context context) {
        adjust_buttom = context.getResources().getDimensionPixelSize(R.dimen.dp_10);
    }

    @Override
    public boolean isScale() {
        return false;
    }

    public Rect getFocusedRect() {
        Rect r = new Rect();
        getFocusedRect(r);
        return r;
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public View getRootView() {
        View parent = this;
        return parent;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = getFocusedRect();
        onAdjustFouce(r);
        // AppDebug.i(TAG, "r = " + r);
        return new FocusRectParams(r, 0.5f, 0.5f);
    }

    @Override
    public void drawAfterFocus(Canvas arg0) {
    }

    @Override
    public void drawBeforeFocus(Canvas arg0) {

    }

    @Override
    public boolean isFinished() {
        return true;
    }

    private void onAdjustFouce(Rect rt) {
        rt.bottom -= adjust_buttom;
    }

    public void setBackgroudView(int visibility) {
        ImageView backgroud = (ImageView) this.findViewById(R.id.goodlist_menu_backgroud_imageview);
        backgroud.setVisibility(visibility);
    }

    public void setMenuLogoRes(int menulogo) {
        mMenuLogo = menulogo;
    }

    public void setMenuLogoOpaqueRes(int menulogoopaque) {
        mMenuLogo_opaque = menulogoopaque;
    }

    public void setMenuTextColorNotSelect(int color) {
        mNotSelect = color;
    }

    /**
     * 处理选中
     * @param select
     */
    public void setMenuChangeOfSelectState(boolean select) {
        TextView menu = (TextView) findViewById(R.id.goodlist_menu_logo_text);
        ImageView logo = (ImageView) findViewById(R.id.goodlist_menu_logo_imageview);

        if (select) {
            menu.setTextColor(Color.WHITE);
            logo.setImageResource(mMenuLogo_opaque);
        } else {
            menu.setTextColor(mNotSelect);
            logo.setImageResource(mMenuLogo);
        }
    }
}
