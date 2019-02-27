package com.yunos.tvtaobao.biz.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yunos.tv.core.common.AppDebug;

/**
 * 此适配器主要是过滤标准的BaseAdapter多次调用getView
 * @author yunzhong.qyz
 */
public abstract class LifeUiBaseAdapter extends BaseAdapter {

    private String TAG = "LifeUiBaseAdapter";

    private View mMakeFirstView = null;
    private boolean mFirstNewconvertView = false;

    private LayoutInflater mLayoutInflater = null;

    protected Context mContext = null;

    public LifeUiBaseAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        boolean newconvertView = false;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(getLayoutID(), null);
            newconvertView = true;
        }

        AppDebug.i(TAG, "getView  -->  position = " + position + "; convertView = " + convertView
                + "; LifeUiBaseAdapter.this = " + this);

        int count = getCount();
        if (count > 2) {
            // 过滤第1个View
            // 此过滤方式只能适用于  多行，并且一行超过两个以上，
            // 因为，当一行是一个的情况下，如果当前在重新重新获取一个的情况下会出错
            if (position == 0) {
                mFirstNewconvertView = newconvertView;
                mMakeFirstView = convertView;
                return convertView;
            }

            if (position == 1) {
                fillView(0, mMakeFirstView, mFirstNewconvertView, parent);
            }
        }

        fillView(position, convertView, newconvertView, parent);

        return convertView;
    }

    // 获取View 的ID
    public abstract int getLayoutID();

    // 填充View
    public abstract void fillView(int position, View convertView, boolean newConvertView, ViewGroup parent);

}