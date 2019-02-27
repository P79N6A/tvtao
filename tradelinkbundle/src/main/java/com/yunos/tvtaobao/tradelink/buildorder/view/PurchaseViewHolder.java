package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AbsListView;

import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.yunos.tv.app.widget.AbsBaseListView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tradelink.R;

public abstract class PurchaseViewHolder {

    protected static String TAG = "PurchaseViewHolder";

    protected Context context;

    protected Component component;

    // 分割线的高度
    protected int mDividerHight;
    // 分割线的颜色
    protected int mDividerColor;

    protected abstract View makeView();

    protected abstract void bindData();

    protected Context getContext() {
        return context;
    }

    protected abstract boolean onKeyListener(View v, int keyCode, KeyEvent event);

    public PurchaseViewHolder(Context context) {
        this.context = context;
        // 分割线的高度
        mDividerHight = context.getResources().getDimensionPixelSize(R.dimen.dp_2);
        // 分割线的颜色
        mDividerColor = context.getResources().getColor(R.color.ytm_buildorder_divider);
    }

    public View initView() {
        View view = makeView();
        if (view instanceof BuildOrderItemView) {
            BuildOrderItemView buildOrderItemView = (BuildOrderItemView) view;
            // 设置线的颜色和高度
            buildOrderItemView.setDividerDrawable(mDividerColor, mDividerHight);
            buildOrderItemView.setOnItemKeyDownListener(new OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    AppDebug.i(TAG, "setOnItemKeyDownListener --> keyCode = " + keyCode);
                    return onKeyListener(v, keyCode, event);
                }
            });
        }
        return view;
    }

    public void bindComponent(Component component) {
        this.component = component;
        boolean fake = isFakeComponent(component);
        AppDebug.i(TAG, "bindComponent --> component = " + component + "; this = " + this + ";fake = " + fake);
        if (!fake) {
            bindData();
        }
    }

    public boolean isFakeComponent(Component component) {
        if ("fake".equals(component.getTopic())) {
            // contentView.setVisibility(View.GONE);
            return true;
        } else {
            // contentView.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public AbsBaseListView.LayoutParams getPreferedLayoutParams() {
        return null;
    }

}
