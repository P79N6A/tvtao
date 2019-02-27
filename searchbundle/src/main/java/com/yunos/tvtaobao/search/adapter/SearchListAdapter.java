package com.yunos.tvtaobao.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.search.R;

import java.util.ArrayList;

/**
 * Created by huangdaju on 17/6/15.
 */

public class SearchListAdapter extends BaseAdapter {
    private String TAG = "SearchListAdapter";
    private ArrayList<String> mItemArray;
    private LayoutInflater mInflater;

    public SearchListAdapter(Context context) {
        mItemArray = new ArrayList<String>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSearchItemList(ArrayList<String> itemArray) {
        if (itemArray != null && itemArray.size() > 0) {
            mItemArray.clear();
            mItemArray.addAll(itemArray);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mItemArray == null ? 0 : mItemArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemArray == null ? null : mItemArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.ytm_search_result_item, null);
            holder = new Holder();
            holder.mItemName = (TextView) convertView.findViewById(R.id.item_name);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (position < 0 || position > mItemArray.size() - 1) {
            AppDebug.e(TAG, TAG + ".getView.position = " + position);
            return convertView;
        }

        if (mItemArray != null && mItemArray.get(position) != null) {
            if (!TextUtils.isEmpty(mItemArray.get(position))) {
                holder.mItemName.setText(mItemArray.get(position));
            }
        }

        return convertView;
    }

    public static class Holder {

        public TextView mItemName;
    }
}
