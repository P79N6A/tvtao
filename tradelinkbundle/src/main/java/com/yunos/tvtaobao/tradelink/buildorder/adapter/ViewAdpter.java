/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity.buildorder
 * FILE NAME: ViewAdpter.java
 * CREATED TIME: 2015-3-9
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tvtaobao.tradelink.buildorder.adapter;


import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.yunos.tv.app.widget.AbsBaseListView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.buildorder.PurchaseViewFactory;
import com.yunos.tvtaobao.tradelink.buildorder.PurchaseViewType;
import com.yunos.tvtaobao.tradelink.buildorder.view.BuildOrderItemView;
import com.yunos.tvtaobao.tradelink.buildorder.view.PurchaseViewHolder;

import java.util.List;

public class ViewAdpter extends BaseAdapter {

    private final String TAG = "ViewAdpter";

    private List<Component> components;
    private Context context;

    public ViewAdpter(List<Component> components, Context context) {
        this.context = context;
        this.components = components;
    }

    public void setData(List<Component> components) {
        this.components = components;
    }

    public List<Component> getComponents() {
        return components;
    }

    @Override
    public int getCount() {
        AppDebug.i(TAG, "components.size() = " + components.size());
        return components != null ? components.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return components.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PurchaseViewHolder viewHolder = null;
        int type = 0;
        if (convertView == null) {
            type = getItemViewType(position);
            if (type != PurchaseViewType.UNKNOWN.getIndex()) {
                viewHolder = PurchaseViewFactory.make(type, context);
                convertView = viewHolder.initView();
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (PurchaseViewHolder) convertView.getTag();
        }

        if (convertView instanceof BuildOrderItemView && convertView != null) {
            BuildOrderItemView buildOrderItemView = (BuildOrderItemView) convertView;
            int typeCurrent = getItemViewType(position);
            if (typeCurrent != PurchaseViewType.INPUT.getIndex()) {
                // 如果当前不是输入框
                Rect manualRect = new Rect();
                manualRect.setEmpty();
                int topPadding = context.getResources().getDimensionPixelSize(R.dimen.dp_4);
                manualRect.top = topPadding;
                manualRect.bottom = topPadding;
                buildOrderItemView.setAjustFocus(false, manualRect);
            }
        }
        AbsBaseListView.LayoutParams lp;
        if (viewHolder != null) {
            AppDebug.i(TAG, "viewHolder getView -->   components.get(position) = " + components.get(position));
            viewHolder.bindComponent(components.get(position));
        }
        if (viewHolder.getPreferedLayoutParams() != null) {
            lp = viewHolder.getPreferedLayoutParams();
        } else
            lp = new AbsBaseListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDimensionPixelSize(R.dimen.dp_72));
        convertView.setLayoutParams(lp);
        AppDebug.i(TAG, "viewHolder getView = " + viewHolder + "; position = " + position + "; type = " + type);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (getCount() <= 0) {
            return PurchaseViewType.UNKNOWN.getIndex();
        }
        if (components == null || position >= components.size() || position < 0) {
            return PurchaseViewType.UNKNOWN.getIndex();
        }

        Component component = components.get(position);
        return PurchaseViewFactory.getItemViewType(component);
    }

    @Override
    public int getViewTypeCount() {
        if (getCount() > 0) {
            return PurchaseViewType.size();
        }
        return super.getViewTypeCount();
    }
}
